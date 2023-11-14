package org.sincore.fxrequest.ui.ctree;

import javafx.scene.control.*;
import org.sincore.fxrequest.ui.ctree.request.RTreeElement;

import java.util.List;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
final class FancyTreeItemBuilder {

    private FancyTreeItemBuilder(){}

    static TreeItem<FancyTreeNodeFacade<RTreeElement>> create(FancyTreeNodeFacade<RTreeElement> root) {
        var rootItem = new TreeItem<>(root);
        root.setTreeItemFacade(new FancyTreeItemFacade(rootItem));
        addChildren(rootItem, root);
        return rootItem;
    }

    private static void addChildren(TreeItem<FancyTreeNodeFacade<RTreeElement>> item, FancyTreeNodeFacade<RTreeElement> node) {
        for (var childNode : node.getChildren())
            addChild(item, childNode);
    }

    private static void addChild(TreeItem<FancyTreeNodeFacade<RTreeElement>> item, FancyTreeNodeFacade<RTreeElement> childNode) {
        addChild(item, childNode, item.getChildren().size());
    }

    static void addChild(TreeItem<FancyTreeNodeFacade<RTreeElement>> item, FancyTreeNodeFacade<RTreeElement> childNode, int index) {
        var childItem = new TreeItem<>(childNode);
        item.getChildren().add(index, childItem);
        addChildren(childItem, childNode);
        childNode.setTreeItemFacade(new FancyTreeItemFacade(childItem));
    }
}


