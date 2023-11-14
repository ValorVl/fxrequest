package org.sincore.fxrequest.ui.ctree;

import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import org.sincore.fxrequest.ui.ctree.request.RTreeElement;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class AbstractTreeOperationHandler<T extends FancyTreeNodeFacade<RTreeElement>> {
    public void selectionChanged(ObservableList<TreeItem<T>> selectedItems) {
    }

    public boolean handleDelete(ObservableList<TreeItem<T>> selectedItems) {
        return false;
    }

    public boolean handleCut(ObservableList<TreeItem<T>> selectedItems) {
        return false;
    }

    public boolean handleCopy(ObservableList<TreeItem<T>> selectedItems) {
        return false;
    }

    public boolean handlePaste(ObservableList<TreeItem<T>> selectedItems) {
        return false;
    }

    public boolean handleUndo() {
        return false;
    }

    public StartDragInfo startDrag(List<List<Integer>> selectionPaths, ObservableList<TreeItem<T>> selectedItems) {
        return null;
    }

    public boolean finishDrag(TransferMode transferMode, Dragboard dragboard, T item, DropLocation location) {
        return false;
    }

    public void handleDoubleClick(TreeCell<T> cell, boolean controlDown, boolean shiftDown, boolean altDown) {
    }

    public ContextMenu getContextMenu(ObservableList<TreeItem<T>> selectedItems) {
        return null;
    }

    public DragOverInfo dragOver(Dragboard dragboard, T ontoNode) {
        DragOverInfo info = new DragOverInfo();
        info.addAllModesAndLocations();
        return info;
    }

    @SuppressWarnings("WeakerAccess") // part of public API
    public enum DropLocation {
        BEFORE,
        AFTER,
        ON
    }

    public class StartDragInfo {
        public StartDragInfo() {
            transferModes = new TransferMode[]{TransferMode.COPY, TransferMode.MOVE};
        }

        public void addContent(DataFormat format, Object content) {
            this.content.put(format, content);
        }

        @SuppressWarnings("WeakerAccess")  // part of public API
        public Map<DataFormat, Object> content = new HashMap<>();

        @SuppressWarnings("WeakerAccess")  // part of public API
        public TransferMode[] transferModes;
    }

    public static class DragOverInfo {
        public void addAllModesAndLocations() {
            addTransferMode(TransferMode.COPY);
            addTransferMode(TransferMode.MOVE);
            addDropLocation(DropLocation.BEFORE);
            addDropLocation(DropLocation.ON);
            addDropLocation(DropLocation.AFTER);
        }

        @SuppressWarnings("WeakerAccess")  // part of public API
        public void addTransferMode(TransferMode mode) {
            transferModes.add(mode);
        }

        @SuppressWarnings("WeakerAccess,unused")  // part of public API
        public void removeTransferMode(TransferMode mode) {
            transferModes.remove(mode);
        }

        @SuppressWarnings("WeakerAccess")  // part of public API
        public void addDropLocation(DropLocation location) {
            dropLocations.add(location);
        }

        @SuppressWarnings("WeakerAccess,unused")  // part of public API
        public void removeDropLocation(DropLocation location) {
            dropLocations.remove(location);
        }

        List<TransferMode> transferModes = new ArrayList<>();
        List<DropLocation> dropLocations = new ArrayList<>();
    }

    /**
     * Creates context menu items for the EditTypes provided. Call this from the
     * #getContextMenu() method to easily add them to the menu.
     */
    protected MenuItem[] createEditMenuItems(ObservableList<TreeItem<T>> selectedItems, EditType... types) {
        MenuItem[] items = new MenuItem[types.length];
        int index = 0;
        for (EditType type : types) {
            MenuItem item = new MenuItem(type.name());
            item.setId(type.getMenuId());
            items[index++] = item;
            switch (type) {
                case CUT:
                    item.setOnAction(event -> handleCut(selectedItems));
                    break;
                case COPY:
                    item.setOnAction(event -> handleCopy(selectedItems));
                    break;
                case DELETE:
                    item.setOnAction(event -> handleDelete(selectedItems));
                    break;
                case PASTE:
                    item.setOnAction(event -> handlePaste(selectedItems));
                    break;
            }
        }
        return items;
    }

    public enum EditType {
        CUT("ftoh-et-cut"),
        COPY("ftoh-et-copy"),
        PASTE("ftoh-et-paste"),
        DELETE("ftoh-et-delete");

        EditType(String id) {
            this.id = id;
        }

        public String getMenuId() {
            return id;
        }

        private final String id;
    }
}


