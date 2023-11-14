package org.sincore.fxrequest.ui.ctree;

import javafx.scene.control.*;
import org.sincore.fxrequest.ui.ctree.request.RTreeElement;

/**
 * The model-facing API to a tree item. Allows the model to notify the tree of asynchronous changes
 * that require updates in the tree.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class FancyTreeItemFacade {

    private final TreeItem<FancyTreeNodeFacade<RTreeElement>> item;

    @SuppressWarnings("WeakerAccess")  // part of the public API
    public FancyTreeItemFacade(TreeItem<FancyTreeNodeFacade<RTreeElement>> item) {
        this.item = item;
    }

    /**
     * Re-render the node. Should be called when non-structural changes require a change to the visual presentation.
     */
    public void refreshDisplay() {
        item.setValue(item.getValue().copyAndDestroy());
    }

    public void addChild(FancyTreeNodeFacade<RTreeElement> child, int index) {
        FancyTreeItemBuilder.addChild(item, child, index);
    }

    public void removeChild(int index, FancyTreeNodeFacade<RTreeElement> child) {
        try {
            FancyTreeNodeFacade<RTreeElement> node = item.getChildren().get(index).getValue();
            if (child == null || node.getModelNode().equals(child.getModelNode())) {
                TreeItem<FancyTreeNodeFacade<RTreeElement>> removeItem = item.getChildren().remove(index);
                removeItem.getValue().destroy();
            } else
                throw new IllegalArgumentException(String.format("The indexed sub-item (%d) didn't match the node selected for removal: %s", index, child.getModelNode().toString()));
        } catch (Exception e) {
            // index doesn't exist
            String childDescription = "(unknown)";
            if (child != null)
                childDescription = child.getModelNode().toString();
            throw new IllegalArgumentException(String.format("Unable to locate the indexed sub-item (%d) for removal: %s", index, childDescription));
        }
    }
}

