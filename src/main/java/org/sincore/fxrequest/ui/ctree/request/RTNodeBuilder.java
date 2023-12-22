package org.sincore.fxrequest.ui.ctree.request;

import java.util.Arrays;

public class RTNodeBuilder {

    private RTNodeBuilder(){}

    private static final String ROOT_NODE_NAME = "root";

    public static RTreeElement rootNode(int[] numOfDescendants){
        var root = new RTreeElement(ROOT_NODE_NAME, RTreeElementType.ROOT);
        root.setIconLateral(RTreeElementType.ROOT.getIconLateral());
        addDescendents(root, numOfDescendants);
        return root;
    }

    public static RTreeElement createFolderNode(String name){
        return new RTreeElement(name, RTreeElementType.FOLDER);
    }

    public static RTreeElement createRequestNode(String name){
        return new RTreeElement(name, RTreeElementType.REQUEST);
    }

    private static void addDescendents(RTreeElement parent, int[] numDescendents)
    {
        if (numDescendents.length < 1)
            return;

        int[] numGrandchildren = null;
        if (numDescendents.length > 1)
            numGrandchildren = Arrays.copyOfRange(numDescendents, 1, numDescendents.length);

        for (int i = 0; i < numDescendents[0]; i++) {
            var child = new RTreeElement(parent.getName(), parent.getType());
            if (numGrandchildren !=  null)
                addDescendents(child, numGrandchildren);
            parent.addChild(child);
        }
    }

}
