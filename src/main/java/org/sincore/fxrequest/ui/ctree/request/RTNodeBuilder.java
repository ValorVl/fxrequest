package org.sincore.fxrequest.ui.ctree.request;

public class RTNodeBuilder {

    private static final String ROOT_NODE_NAME = "root";

    public static RTreeElement rootNode(){
        return new RTreeElement(ROOT_NODE_NAME, RTreeElementType.ROOT);
    }

    public static RTreeElement createFolderNode(String name){
        return new RTreeElement(name, RTreeElementType.FOLDER);
    }

    public static RTreeElement createRequestNode(String name){
        return new RTreeElement(name, RTreeElementType.REQUEST);
    }

}
