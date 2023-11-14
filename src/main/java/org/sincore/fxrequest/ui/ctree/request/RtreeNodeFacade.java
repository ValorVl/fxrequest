package org.sincore.fxrequest.ui.ctree.request;

import javafx.scene.Node;
import org.sincore.fxrequest.ui.ctree.FancyTreeCellEditor;
import org.sincore.fxrequest.ui.ctree.FancyTreeItemFacade;
import org.sincore.fxrequest.ui.ctree.FancyTreeNodeFacade;

import java.util.ArrayList;
import java.util.List;

public class RtreeNodeFacade implements FancyTreeNodeFacade<RTreeElement> {

    private final RTreeElement dataModel;
    private FancyTreeItemFacade facade;
    private List<FancyTreeNodeFacade<RTreeElement>> children = new ArrayList<>();
    private boolean isEditing = false;

    public RtreeNodeFacade(RTreeElement dataModel){
        this.dataModel = dataModel;
        this.dataModel.addChangeListener(this.changeListener);
        this.children.addAll(dataModel.getChildren().stream().map(RtreeNodeFacade::new).toList());
    }

    public RtreeNodeFacade(RTreeElement dataModel, FancyTreeItemFacade facade, List<FancyTreeNodeFacade<RTreeElement>> childrens){
        this.dataModel = dataModel;
        this.facade = facade;
        this.children = childrens;
    }

    @Override
    public FancyTreeNodeFacade<RTreeElement> copyAndDestroy() {
        synchronized (dataModel){
            var rtreeNodeFacadeCopy = new RtreeNodeFacade(this.dataModel, this.facade, this.children);
            dataModel.replaceChangeListener(changeListener, rtreeNodeFacadeCopy.changeListener);
            return rtreeNodeFacadeCopy;
        }
    }

    @Override
    public void destroy() {
        this.dataModel.removeChangeListener(changeListener);
        facade = null;
    }

    @Override
    public List<FancyTreeNodeFacade<RTreeElement>> getChildren() {
        return this.children;
    }

    @Override
    public Node getCustomCellUI() {
        return null;
    }

    @Override
    public String getLabelText() {
        return dataModel.getName();
    }

    @Override
    public RTreeElement getModelNode() {
        return this.dataModel;
    }

    @Override
    public List<String> getStyles() {
        return dataModel.getStyles();
    }

    @Override
    public void editStarting() {
        this.isEditing = true;
    }

    @Override
    public FancyTreeCellEditor getCustomEditorUI() {
        return null;
    }

    @Override
    public void editFinished() {
        this.isEditing = false;
    }

    @Override
    public Node getIcon() {
        return null;
    }

    @Override
    public void setTreeItemFacade(FancyTreeItemFacade itemFacade) {
        this.facade = itemFacade;
    }

    @Override
    public void setLabelText(String newValue) {
        dataModel.setName(newValue);
    }

    private final ChangeListener changeListener = new ChangeListener() {
        @Override
        public void childAdded(RTreeElement child, int index) {
            if(facade != null){
                var childFacade = new RtreeNodeFacade(child);
                children.add(index, childFacade);
                facade.addChild(childFacade, index);
            }
        }

        @Override
        public void childRemoved(RTreeElement child, int index) {
            facade.removeChild(index, new RtreeNodeFacade(child));
            children.remove(index);
        }

        @Override
        public void propertyChanged() {
            if (facade != null && !isEditing){
                facade.refreshDisplay();
            }
        }
    };
}
