package org.sincore.fxrequest.ui.ctree.request;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import org.sincore.fxrequest.ui.ctree.FancyTreeNodeFacade;
import org.sincore.fxrequest.ui.ctree.AbstractTreeOperationHandler;

import java.util.List;

public class OpsHandler extends AbstractTreeOperationHandler<FancyTreeNodeFacade<RTreeElement>> {

    private static final DataFormat LIST_OF_NODES = new DataFormat("application/x-ListOfRTNodes");

    private final RTreeElement root;

    private int dragCount;
    private ObservableList<TreeItem<FancyTreeNodeFacade<RTreeElement>>> draggedItems;
    private List<RTreeElement> droppedNodes;
    private Object droppedContent;
    private String doubleClickedNodeName;

    public OpsHandler(RTreeElement root){
        this.root = root;
    }

    public String getDoubleClickedNodeName(){
        return this.doubleClickedNodeName;
    }

    @Override
    public void selectionChanged(ObservableList<TreeItem<FancyTreeNodeFacade<RTreeElement>>> selectedItems) {
        super.selectionChanged(selectedItems);
        System.out.println(">>> changed " + selectedItems.size());
    }

    @Override
    public boolean handleDelete(ObservableList<TreeItem<FancyTreeNodeFacade<RTreeElement>>> selectedItems) {
        for (var item : selectedItems) {
            var nodeToRemove = item.getValue().getModelNode();
            var parent = root.findParentFor(nodeToRemove);
            if (parent != null)
                parent.removeChild(nodeToRemove);
        }
        return true;
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
    public AbstractTreeOperationHandler<FancyTreeNodeFacade<RTreeElement>>.StartDragInfo startDrag(List<List<Integer>> selectionPaths,
                                                                                                   ObservableList<TreeItem<FancyTreeNodeFacade<RTreeElement>>> selectedItems) {
        var startDragInfo = new StartDragInfo();
        this.draggedItems = selectedItems;

        var selections = selectedItems.stream().map(item -> item.getValue().getModelNode()).toList();
        startDragInfo.addContent(LIST_OF_NODES, selections);
        dragCount = selectedItems.size();

        return startDragInfo;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean finishDrag(TransferMode transferMode, Dragboard dragboard, FancyTreeNodeFacade<RTreeElement> item, DropLocation location) {
        if (dragboard.getContent(LIST_OF_NODES) != null)
        {
            droppedNodes = (List<RTreeElement>) dragboard.getContent(LIST_OF_NODES);
            if (droppedNodes.contains(item.getModelNode()))
                return false;  // TODO indicate this?
            RTreeElement parent;
            int addIndex;
            switch (location) {
                case BEFORE -> {
                    parent = root.findParentFor(item.getModelNode());
                    addIndex = parent.getChildren().indexOf(item.getModelNode());
                }
                case ON -> {
                    parent = item.getModelNode();
                    addIndex = (item.getModelNode()).getChildren().size();
                }
                case AFTER -> {
                    parent = root.findParentFor(item.getModelNode());
                    addIndex = parent.getChildren().indexOf(item.getModelNode()) + 1;
                }
                default -> {
                    return false;
                }
            }


            for (RTreeElement node : droppedNodes)
            {
                RTreeElement nodeToDrop;
                if (transferMode.equals(TransferMode.COPY)) {
                    nodeToDrop = RTreeElement.deepCopy(node, true);
                } else {
                    nodeToDrop = node;
                    Platform.runLater(() ->root.findParentFor(node).removeChild(nodeToDrop));
                }

                final int indexToAddAt = addIndex;
                Platform.runLater(() -> parent.addChild(indexToAddAt, nodeToDrop)); // updates to tree should be done on the UI thread when possible?
                addIndex++;
            }
            return true;
        }

        return false;
    }

    @Override
    public void handleDoubleClick(TreeCell<FancyTreeNodeFacade<RTreeElement>> cell, boolean controlDown, boolean shiftDown, boolean altDown) {
        if (cell.getTreeItem() == null){
            return;
        }
        this.doubleClickedNodeName = cell.getTreeItem().getValue().getModelNode().getName();
        if(cell.getTreeView().isEditable() && !cell.isEditing()){
            cell.startEdit();
        }
    }

    @Override
    public ContextMenu getContextMenu(ObservableList<TreeItem<FancyTreeNodeFacade<RTreeElement>>> selectedItems) {
        return super.getContextMenu(selectedItems);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DragOverInfo dragOver(Dragboard dragboard, FancyTreeNodeFacade<RTreeElement> ontoNode) {
        var info = new DragOverInfo();
        if (dragboard.getContent(LIST_OF_NODES) != null){
            droppedNodes = (List<RTreeElement>) dragboard.getContent(LIST_OF_NODES);
            if (ontoNode != null && droppedNodes.contains(ontoNode.getModelNode())){
                return info;
            }
        }
        for (RTreeElement element : droppedNodes){
            if (element.isAncestorOf(ontoNode.getModelNode())){
                return info;
            }
        }
        info.addAllModesAndLocations();
        return info;
    }

    @Override
    protected MenuItem[] createEditMenuItems(ObservableList<TreeItem<FancyTreeNodeFacade<RTreeElement>>> selectedItems, EditType... types) {
        return super.createEditMenuItems(selectedItems, types);
    }
}
