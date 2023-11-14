package org.sincore.fxrequest.ui.ctree.request;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import org.sincore.fxrequest.ui.ctree.FancyTreeNodeFacade;
import org.sincore.fxrequest.ui.ctree.AbstractTreeOperationHandler;

import java.util.List;

public class OpsHandler extends AbstractTreeOperationHandler<FancyTreeNodeFacade<RTreeElement>> {

    private RTreeElement root;

    public OpsHandler(RTreeElement root){
        this.root = root;
    }

    @Override
    public void selectionChanged(ObservableList<TreeItem<FancyTreeNodeFacade<RTreeElement>>> selectedItems) {
        super.selectionChanged(selectedItems);
    }

    @Override
    public boolean handleDelete(ObservableList<TreeItem<FancyTreeNodeFacade<RTreeElement>>> selectedItems) {
        return super.handleDelete(selectedItems);
    }

    @Override
    public boolean handleCut(ObservableList<TreeItem<FancyTreeNodeFacade<RTreeElement>>> selectedItems) {
        return super.handleCut(selectedItems);
    }

    @Override
    public boolean handleCopy(ObservableList<TreeItem<FancyTreeNodeFacade<RTreeElement>>> selectedItems) {
        return super.handleCopy(selectedItems);
    }

    @Override
    public boolean handlePaste(ObservableList<TreeItem<FancyTreeNodeFacade<RTreeElement>>> selectedItems) {
        return super.handlePaste(selectedItems);
    }

    @Override
    public boolean handleUndo() {
        return super.handleUndo();
    }

    @Override
    public AbstractTreeOperationHandler<FancyTreeNodeFacade<RTreeElement>>.StartDragInfo startDrag(List<List<Integer>> selectionPaths, ObservableList<TreeItem<FancyTreeNodeFacade<RTreeElement>>> selectedItems) {
        return super.startDrag(selectionPaths, selectedItems);
    }

    @Override
    public boolean finishDrag(TransferMode transferMode, Dragboard dragboard, FancyTreeNodeFacade<RTreeElement> item, DropLocation location) {
        return super.finishDrag(transferMode, dragboard, item, location);
    }

    @Override
    public void handleDoubleClick(TreeCell<FancyTreeNodeFacade<RTreeElement>> cell, boolean controlDown, boolean shiftDown, boolean altDown) {
        super.handleDoubleClick(cell, controlDown, shiftDown, altDown);
    }

    @Override
    public ContextMenu getContextMenu(ObservableList<TreeItem<FancyTreeNodeFacade<RTreeElement>>> selectedItems) {
        return super.getContextMenu(selectedItems);
    }

    @Override
    public DragOverInfo dragOver(Dragboard dragboard, FancyTreeNodeFacade<RTreeElement> ontoNode) {
        return super.dragOver(dragboard, ontoNode);
    }

    @Override
    protected MenuItem[] createEditMenuItems(ObservableList<TreeItem<FancyTreeNodeFacade<RTreeElement>>> selectedItems, EditType... types) {
        return super.createEditMenuItems(selectedItems, types);
    }
}
