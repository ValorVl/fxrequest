package org.sincore.fxrequest.ui.ctree;

import javafx.collections.*;
import javafx.css.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import org.sincore.fxrequest.ui.ctree.request.RTreeElement;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class FancyTreeCell extends TreeCell<FancyTreeNodeFacade<RTreeElement>> {

    private final AbstractTreeOperationHandler<FancyTreeNodeFacade<RTreeElement>> handler;
    private AbstractTreeOperationHandler.DropLocation dropLocation;
    private int cursorX;
    private int cursorY;
    private long cursorHoverSince;
    private long hoverExpandDuration = FancyTreeView.DEFAULT_HOVER_EXPAND_DURATION;
    private FancyTreeCellEditor editor;

    //
    // Styles for the cells
    //
    static final String CELL_STYLE_NAME = "fancytreecell";
    static final String DROP_AFTER_STYLE_NAME = "fancytreecell-drop-after";
    static final String DROP_BEFORE_STYLE_NAME = "fancytreecell-drop-before";
    static final String DROP_ON_STYLE_NAME = "fancytreecell-drop-on";
    private static final List<String> DRAG_STYLES = new ArrayList<>();

    static {
        DRAG_STYLES.add(DROP_AFTER_STYLE_NAME);
        DRAG_STYLES.add(DROP_BEFORE_STYLE_NAME);
        DRAG_STYLES.add(DROP_ON_STYLE_NAME);
    }

    //
    // Pseudo-styles for the cell
    //
    private static final PseudoClass EDITING_CLASS = PseudoClass.getPseudoClass("editing");

    //
    // Default styles for the cell (should never be removed)
    //
    private static final List<String> DEFAULT_STYLES = new ArrayList<>();

    static {
        DEFAULT_STYLES.add(CELL_STYLE_NAME);
        DEFAULT_STYLES.add("cell");
        DEFAULT_STYLES.add("indexed-cell");
        DEFAULT_STYLES.add("tree-cell");
    }

    private static final Map<AbstractTreeOperationHandler.DropLocation, String> DROP_LOCATION_TO_STYLE_MAP = new HashMap<>();

    static {
        DROP_LOCATION_TO_STYLE_MAP.put(AbstractTreeOperationHandler.DropLocation.BEFORE, DROP_BEFORE_STYLE_NAME);
        DROP_LOCATION_TO_STYLE_MAP.put(AbstractTreeOperationHandler.DropLocation.ON, DROP_ON_STYLE_NAME);
        DROP_LOCATION_TO_STYLE_MAP.put(AbstractTreeOperationHandler.DropLocation.AFTER, DROP_AFTER_STYLE_NAME);
    }


    FancyTreeCell(AbstractTreeOperationHandler<FancyTreeNodeFacade<RTreeElement>> handler, boolean enableDnd) {
        addStyle(CELL_STYLE_NAME);
        this.handler = handler;

        if (enableDnd)
            setupDragAndDrop();
    }

    private void setupDragAndDrop() {
        setOnDragEntered(e -> {
            resetCursorPosition();
            e.consume();
        });

        setOnDragDone(Event::consume);

        setOnDragDetected(e -> {
            var tree = (FancyTreeView<FancyTreeNodeFacade<RTreeElement>>) getTreeView();
            AbstractTreeOperationHandler<FancyTreeNodeFacade<RTreeElement>>.StartDragInfo result =
                    handler.startDrag(tree.getSelectionPaths(), tree.getSelectionModel().getSelectedItems());

            if (result == null)
                return;

            ClipboardContent content = new ClipboardContent();
            Map<DataFormat, Object> contentMap = result.content;
            for (DataFormat format : contentMap.keySet())
                content.put(format, result.content.get(format));

            Dragboard dragboard = startDragAndDrop(result.transferModes);
            dragboard.setContent(content);

            e.consume();
        });

        setOnDragDropped(e ->
        {
            boolean completed = handler.finishDrag(e.getTransferMode(), e.getDragboard(), getItem(), dropLocation);
            e.setDropCompleted(completed);
            e.consume();
        });

        setOnDragOver(e ->
        {
            removeStyle(DROP_BEFORE_STYLE_NAME);
            removeStyle(DROP_ON_STYLE_NAME);
            removeStyle(DROP_AFTER_STYLE_NAME);

            updateCursorPositionAndHoverTime(e);

            if (getItem() != null && !getItem().getChildren().isEmpty() && !(getTreeItem().isExpanded()) && isWaitingForTreeExpand())
                getTreeItem().setExpanded(true);

            Point2D sceneCoordinates = localToScene(0d, 0d);
            double cellHeight = getHeight();
            double mouseY = e.getSceneY() - (sceneCoordinates.getY());  // this will be the y-coord within the cell
            AbstractTreeOperationHandler.DragOverInfo info = handler.dragOver(e.getDragboard(), getItem());
            DropLocationCalculator calculator = new DropLocationCalculator(cellHeight, mouseY, info);
            dropLocation = calculator.getDropLocation();
            if (dropLocation != null) {
                addStyle(DROP_LOCATION_TO_STYLE_MAP.get(dropLocation));
                TransferMode[] modes = new TransferMode[info.transferModes.size()];
                info.transferModes.toArray(modes);
                e.acceptTransferModes(modes);
            }
            e.consume();
        });

        setOnDragExited(event ->
        {
            removeStyle(DROP_BEFORE_STYLE_NAME);
            removeStyle(DROP_ON_STYLE_NAME);
            removeStyle(DROP_AFTER_STYLE_NAME);
            event.consume();
        });
    }

    @Override
    protected void updateItem(FancyTreeNodeFacade<RTreeElement> item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
            clearStyles();
        } else {
            if (isEditing())
                cancelEdit();
            else
                updateCellUI(item);
        }
    }

    private void updateCellUI(FancyTreeNodeFacade<RTreeElement> item) {
        if (item == null) {
            setText(null);
            setGraphic(null);
        } else {
            Node node = item.getCustomCellUI();
            if (node == null) {
                setText(item.getLabelText());
                setGraphic(item.getIcon());
            } else {
                setText(null);
                setGraphic(node);
            }
            updateStyles(item);
        }
        pseudoClassStateChanged(EDITING_CLASS, isEditing());
    }

    private void updateStyles(FancyTreeNodeFacade<RTreeElement> item) {
        final ObservableList<String> appliedStyles = getStyleClass();
        final List<String> onDemandStyles = new ArrayList<>(item.getStyles());
        final List<String> stylesToRemove = new ArrayList<>();
        for (String style : appliedStyles) {
            if (!DEFAULT_STYLES.contains(style) && !DRAG_STYLES.contains(style) && !onDemandStyles.remove(style)) {
                stylesToRemove.add(style);
            }
        }
        if (!stylesToRemove.isEmpty())
            appliedStyles.removeAll(stylesToRemove);
        if (!onDemandStyles.isEmpty())
            appliedStyles.addAll(onDemandStyles);
    }

    private void clearStyles() {
        final ObservableList<String> appliedStyles = getStyleClass();
        final List<String> stylesToRemove = new ArrayList<>(appliedStyles);
        stylesToRemove.removeAll(DEFAULT_STYLES);
        stylesToRemove.removeAll(DRAG_STYLES);
        appliedStyles.removeAll(stylesToRemove);
    }

    private void addStyle(String newStyle) {
        if (!getStyleClass().contains(newStyle))
            getStyleClass().add(newStyle);
    }

    private void removeStyle(String removeStyle) {
        getStyleClass().remove(removeStyle);
    }

    // for hover-to-expand feature
    private void updateCursorPositionAndHoverTime(DragEvent e) {
        if (e.getSceneX() == cursorX && e.getSceneY() == cursorY)
            return;

        cursorX = (int) e.getSceneX();
        cursorY = (int) e.getSceneY();
        cursorHoverSince = System.currentTimeMillis();
    }

    // for hover-to-expand feature
    private boolean isWaitingForTreeExpand() {
        return System.currentTimeMillis() - cursorHoverSince > hoverExpandDuration;
    }

    // for hover-to-expand feature
    private void resetCursorPosition() {
        cursorX = 0;
        cursorY = 0;
        cursorHoverSince = 0;
    }

    @Override
    public void startEdit() {
        if (!isEditable() || !getTreeView().isEditable())
            return;

        TreeItem<FancyTreeNodeFacade<RTreeElement>> item = getTreeItem();
        if (item == null)
            return;

        final FancyTreeCellEditor cellEditor = getCellEditor();
        cellEditor.getNode().requestFocus();
        getItem().editStarting();

        super.startEdit();

        if (isEditing()) {
            setText(null);
            setGraphic(cellEditor.getNode());
        }
    }

    @Override
    public void cancelEdit() {
        if (getCellEditor() != null) {
            editor.cancelEdit();
            editor = null;  // if you don't do this, the editor could be re-used at a future time, which is VERY hard to debug. DAMHIKT
        }
        super.cancelEdit();
        updateCellUI(getItem());
        if (getItem() != null)
            getItem().editFinished();
    }

    @Override
    public void commitEdit(FancyTreeNodeFacade<RTreeElement> facade) {
        super.commitEdit(facade);
        updateCellUI(getItem());
        facade.editFinished();
        editor = null;  // if you don't do this, the editor could be re-used at a future time, which is VERY hard to debug. DAMHIKT
    }

    private FancyTreeCellEditor getCellEditor() {
        if (editor == null) {
            editor = getItem().getCustomEditorUI();
            if (editor == null)
                editor = new TextCellEditor();
            editor.setCell(this);
        }
        return editor;
    }

    /**
     * Set how long the user must hover (during drag) before a collapsed parent node will expand.
     */
    void setHoverExpandDuration(long hoverExpandDuration) {
        this.hoverExpandDuration = hoverExpandDuration;
    }

    private static class DropLocationCalculator {
        DropLocationCalculator(double cellHeight, double mouseY, AbstractTreeOperationHandler.DragOverInfo info) {
            this.cellHeight = cellHeight;
            this.mouseY = mouseY;
            this.info = info;
            calculate();
        }

        private void calculate() {
            // re-order and count the locations
            List<AbstractTreeOperationHandler.DropLocation> allowedLocations = new ArrayList<>();
            if (info.dropLocations.contains(AbstractTreeOperationHandler.DropLocation.BEFORE))
                allowedLocations.add(AbstractTreeOperationHandler.DropLocation.BEFORE);
            if (info.dropLocations.contains(AbstractTreeOperationHandler.DropLocation.ON))
                allowedLocations.add(AbstractTreeOperationHandler.DropLocation.ON);
            if (info.dropLocations.contains(AbstractTreeOperationHandler.DropLocation.AFTER))
                allowedLocations.add(AbstractTreeOperationHandler.DropLocation.AFTER);

            if (allowedLocations.size() == 1)
                dropLocation = allowedLocations.get(0);
            else if (allowedLocations.size() == 2) {
                if (mouseY < (cellHeight * 0.5d))
                    dropLocation = allowedLocations.get(0);
                else
                    dropLocation = allowedLocations.get(1);
            } else if (allowedLocations.size() == 3) {
                if (mouseY < (cellHeight * 0.25d))
                    dropLocation = allowedLocations.get(0);
                else if (mouseY > (cellHeight * 0.75d))
                    dropLocation = allowedLocations.get(2);
                else
                    dropLocation = allowedLocations.get(1);
            }
        }

        AbstractTreeOperationHandler.DropLocation getDropLocation() {
            return dropLocation;
        }

        double cellHeight;
        double mouseY;
        AbstractTreeOperationHandler.DragOverInfo info;
        AbstractTreeOperationHandler.DropLocation dropLocation;
    }
}
