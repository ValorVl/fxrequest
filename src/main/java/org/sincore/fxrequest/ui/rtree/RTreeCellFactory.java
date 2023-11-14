package org.sincore.fxrequest.ui.rtree;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.*;
import javafx.util.Callback;
import org.sincore.fxrequest.utils.StringUtils;

import java.util.Objects;

public class RTreeCellFactory implements Callback<TreeView<RTreeElement>, TreeCell<RTreeElement>> {

    private static final DataFormat JAVA_FORMAT = new DataFormat("application/x-java-serialized-object");
    private static final String DROP_HINT_STYLE = "-fx-border-color: #eea82f; -fx-border-width: 0 0 2 0; -fx-padding: 3 3 1 3";

    private TreeItem<RTreeElement> draggedItem;
    private TreeCell<RTreeElement> dropTarget;

    @Override
    public TreeCell<RTreeElement> call(TreeView<RTreeElement> treeView) {
        var cell = new TreeCell<RTreeElement>(){
            @Override
            protected void updateItem(RTreeElement rTreeElement, boolean b) {
                super.updateItem(rTreeElement, b);
                if(rTreeElement == null || b){
                    setGraphic(null);
                } else {
                    var treeCallComponent = new TreeCallComponent(rTreeElement);
                    setGraphic(treeCallComponent);
                }
            }
        };

        cell.setOnDragDetected(event -> dragDetected(event, cell, treeView));
        cell.setOnDragOver((DragEvent event) -> dragOver(event, cell, treeView));
        cell.setOnDragDropped((DragEvent event) -> drop(event, cell, treeView));
        cell.setOnDragDone((DragEvent event) -> clearDropLocation());

        return cell;
    }

    private void clearDropLocation() {
        if(dropTarget != null){
            dropTarget.setStyle(StringUtils.EMPTY_STRING);
        }
    }

    private void drop(DragEvent event, TreeCell<RTreeElement> cell, TreeView<RTreeElement> treeView) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (!db.hasContent(JAVA_FORMAT)) return;

        TreeItem<RTreeElement> thisItem = cell.getTreeItem();
        TreeItem<RTreeElement> droppedItemParent = draggedItem.getParent();

        // remove from previous location
        droppedItemParent.getChildren().remove(draggedItem);

        // dropping on parent node makes it the first child
        if (Objects.equals(droppedItemParent, thisItem)) {
            thisItem.getChildren().add(0, draggedItem);
            treeView.getSelectionModel().select(draggedItem);
        }
        else {
            // add to new location
            int indexInParent = thisItem.getParent().getChildren().indexOf(thisItem);
            thisItem.getParent().getChildren().add(indexInParent + 1, draggedItem);
        }
        treeView.getSelectionModel().select(draggedItem);
        event.setDropCompleted(success);
    }

    private void dragOver(DragEvent event, TreeCell<RTreeElement> cell, TreeView<RTreeElement> treeView) {
        if (!event.getDragboard().hasContent(JAVA_FORMAT)) return;
        TreeItem<RTreeElement> thisItem = cell.getTreeItem();

        // can't drop on itself
        if (draggedItem == null || thisItem == null || thisItem == draggedItem) return;
        // ignore if this is the root
        if (draggedItem.getParent() == null) {
            clearDropLocation();
            return;
        }

        event.acceptTransferModes(TransferMode.MOVE);
        if (!Objects.equals(dropTarget, cell)) {
            clearDropLocation();
            this.dropTarget = cell;
            dropTarget.setStyle(DROP_HINT_STYLE);
        }
    }

    private void dragDetected(MouseEvent event, TreeCell<RTreeElement> treeCell, TreeView<RTreeElement> treeView){
        this.draggedItem = treeCell.getTreeItem();

        if (RTreeElementType.ROOT == draggedItem.getValue().getType()) return;

        var dragboard = treeCell.startDragAndDrop(TransferMode.MOVE);
        var clipboardContent = new ClipboardContent();
        clipboardContent.put(JAVA_FORMAT, draggedItem.getValue());

        dragboard.setContent(clipboardContent);
        dragboard.setDragView(treeCell.snapshot(null, null));
        event.consume();
    }
}
