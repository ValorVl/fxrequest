package org.sincore.fxrequest.ui.ctree;

import javafx.application.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import org.sincore.fxrequest.ui.ctree.request.RTreeElement;

import java.util.*;

/**
 * A tree with lots of fancy features already implemented...so you don't have to. It is intended to help
 * implement a tree on a complex hierarchical data model without forcing the model to comply with the expectations
 * of TreeView. This includes asynchronous state changes and complex user interactions, such as drag-and-drop.
 * <p>
 * Fancy features include:
 * - update view when the model node properties change (not just when the entire model node is replaced). This requires the custom FancyTreeNode extension to notify the FancyTreeItemFacade when these changes happen.
 * - update view from asynchronous events (as above)
 * - smart scroll-to-item behavior
 * - expand tree to make an item visible
 * - cut/copy/paste keystroke support implemented, including common OS key combinations (Windows)
 * - drag and drop support implemented, including tree-aware drop targets (drop before, into, or after)
 * - hover cursor over a tree item to expand it during drag
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
//@SuppressWarnings("ALL")
public class FancyTreeView<T extends FancyTreeNodeFacade<RTreeElement>> extends TreeView<FancyTreeNodeFacade<RTreeElement>> {

    static final long DEFAULT_HOVER_EXPAND_DURATION = 2000;

    private final AbstractTreeOperationHandler<FancyTreeNodeFacade<RTreeElement>> opsHandler;
    private final boolean enableDnd;
    private ContextMenu contextMenu = null;
    private long hoverExpandDuration = DEFAULT_HOVER_EXPAND_DURATION;




    public FancyTreeView(AbstractTreeOperationHandler<FancyTreeNodeFacade<RTreeElement>> opsHandler, boolean enableDnd) {
        this.opsHandler = opsHandler;
        this.enableDnd = enableDnd;
        new FancyTreeKeyHandler(this, this.opsHandler);
        setCellFactory(param ->
        {
            FancyTreeCell cell = new FancyTreeCell(this.opsHandler, this.enableDnd);
            cell.setHoverExpandDuration(hoverExpandDuration);

            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent e) ->
            {
                if (e.getClickCount() == 2 && e.getButton().equals(MouseButton.PRIMARY)) {
                    e.consume();
                    this.opsHandler.handleDoubleClick(cell, e.isControlDown(), e.isShiftDown(), e.isAltDown());
//                    Platform.runLater(() -> _ops_handler.handleDoubleClick(cell, e.isControlDown(), e.isShiftDown(), e.isAltDown()));
                }
            });

            return cell;
        });
        setSkin(new FancyTreeViewSkin(this));
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                this.opsHandler.selectionChanged(getSelectionModel().getSelectedItems()));

        addEventFilter(MouseEvent.MOUSE_RELEASED, e ->
        {
            if (contextMenu != null) {
                contextMenu.hide();
                contextMenu = null;
            }
            if (e.getButton() == MouseButton.SECONDARY) {
                var selections = getSelectionModel().getSelectedItems();
                contextMenu = this.opsHandler.getContextMenu(selections);
                if (contextMenu != null)
                    contextMenu.show(this, e.getScreenX(), e.getScreenY());
            }
        });
    }

    @SuppressWarnings("WeakerAccess") // part of public API
    public void expandAll() {
        TreeItem<FancyTreeNodeFacade<RTreeElement>> root = getRoot();
        expandNodeAndChilren(root);
    }

    private void expandNodeAndChilren(TreeItem<FancyTreeNodeFacade<RTreeElement>> node) {
        node.setExpanded(true);
        node.getChildren().forEach(this::expandNodeAndChilren);
    }

    public void collapseAll() {
        TreeItem<FancyTreeNodeFacade<RTreeElement>> root = getRoot();
        collapseNodeAndChildren(root);
    }

    private void collapseNodeAndChildren(TreeItem<FancyTreeNodeFacade<RTreeElement>> node) {
        node.getChildren().forEach(this::collapseNodeAndChildren);
        node.setExpanded(false);
    }

    @SuppressWarnings("WeakerAccess") // part of public API
    public List<TreeItem<FancyTreeNodeFacade<RTreeElement>>> expandToMakeVisible(Object node) {
        var item = findItemForModelNode(node);
        if (item == null)
            return Collections.emptyList();

        List<TreeItem<FancyTreeNodeFacade<RTreeElement>>> expanded = new ArrayList<>();
        item = item.getParent();
        while (item != null) {
            if (!item.isExpanded()) {
                item.setExpanded(true);
                expanded.add(item);
            }
            item = item.getParent();
        }
        return expanded;
    }

    public List<TreeItem<FancyTreeNodeFacade<RTreeElement>>> expandAndScrollTo(Object node) {
        var expanded = expandToMakeVisible(node);
        scrollToVisibleItem(node);
        return expanded;
    }

    public List<TreeItem<FancyTreeNodeFacade<RTreeElement>>> expandScrollToAndSelect(Object node) {
        var item = findItemForModelNode(node);
        if (item == null)
            return Collections.emptyList();

        var expanded = expandToMakeVisible(node);
        scrollToVisibleItem(node);

        getSelectionModel().select(item);
        return expanded;
    }

    private TreeItem<FancyTreeNodeFacade<RTreeElement>> findItemForModelNode(Object node) {
        return findItemForModelNode(getRoot(), node);
    }

    private TreeItem<FancyTreeNodeFacade<RTreeElement>> findItemForModelNode(TreeItem<FancyTreeNodeFacade<RTreeElement>> item, Object node) {
        if (item.getValue().getModelNode() == node)
            return item;
        for (var child : item.getChildren()) {
            var found = findItemForModelNode(child, node);
            if (found != null)
                return found;
        }
        return null;
    }

    public int findIndexOfVisibleItem(TreeItem<FancyTreeNodeFacade<RTreeElement>> targetItem) {
        int index = 0;
        TreeItem<FancyTreeNodeFacade<RTreeElement>> item = getTreeItem(index);
        while (item != null) {
            if (item == targetItem)
                return index;
            index++;
            item = getTreeItem(index);
        }

        return -1;
    }

    public void scrollToAndMakeVisible(Object node) {
        var item = findItemForModelNode(node);
        if (item == null)
            return;
        expandToMakeVisible(item); // do this first - can't scoll to an item if it is hidden (any ancestor is not expanded)

        int index = findIndexOfVisibleItem(item);
        if (!((FancyTreeViewSkin) getSkin()).isIndexVisible(index))  // don't scroll if it is already visible on screen
            Platform.runLater(() -> scrollTo(index));
    }

    public void scrollToVisibleItem(Object node) {
        var item = findItemForModelNode(node);
        if (item == null)
            return;

        int index = findIndexOfVisibleItem(item);
        if (!((FancyTreeViewSkin) getSkin()).isIndexVisible(index))  // don't scroll if it is already visible on screen
            Platform.runLater(() -> scrollTo(index));
    }

    @SuppressWarnings("WeakerAccess") // part of public API
    public List<List<Integer>> getSelectionPaths() {
        ObservableList<TreeItem<FancyTreeNodeFacade<RTreeElement>>> selectedItems = getSelectionModel().getSelectedItems();
        List<List<Integer>> paths = new ArrayList<>();
        for (TreeItem<FancyTreeNodeFacade<RTreeElement>> item : selectedItems) {
            List<Integer> path = new ArrayList<>();
            createPathToItem(path, item);
            paths.add(path);
        }
        return paths;
    }

    private void createPathToItem(List<Integer> path, TreeItem<FancyTreeNodeFacade<RTreeElement>> item) {
        while (item.getParent() != null) {
            var parent = item.getParent();
            path.add(0, parent.getChildren().indexOf(item));
            item = parent;
        }
    }

    public void setRoot(FancyTreeNodeFacade<RTreeElement> rootFacade) {
        setRoot(FancyTreeItemBuilder.create(rootFacade));
    }

    @SuppressWarnings("WeakerAccess")  // part of public API
    public void setHoverExpandDuration(long hoverExpandDuration) {
        this.hoverExpandDuration = hoverExpandDuration;
    }

}
