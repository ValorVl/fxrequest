package org.sincore.fxrequest.ui.ctree.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;
import org.sincore.fxrequest.ui.ctree.FancyTreeNodeFacade;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Getter
@Setter
public class RTreeElement implements Serializable {

    private static final String COPY_LABEL_SUFFIX = " (copy)";

    private UUID id;
    private RTreeElementType type;
    private String name;
    private String iconLateral;
    private List<String> styles = new ArrayList<>();
    private List<RTreeElement> children = new ArrayList<>();

    @JsonIgnore
    private transient List<ChangeListener> changeListeners = new ArrayList<>();

    public RTreeElement(RTreeElement original){
        this.name = original.getName();
        this.type = original.getType();
        this.iconLateral = original.getIconLateral();
    }

    public RTreeElement(String name, RTreeElementType type){
        this.name = name;
        this.type = type;
        this.id = UUID.randomUUID();
        this.iconLateral = type.getIconLateral();
    }

    public RTreeElement findParentFor(RTreeElement target) {
        if (children.contains(target))
            return this;
        for (RTreeElement child : children) {
            var parent = child.findParentFor(target);
            if (parent != null)
                return parent;
        }
        return null;
    }

    public void addAfter(RTreeElement toAdd, RTreeElement toFollow)
    {
        int index = children.indexOf(toFollow) + 1;
        children.add(index, toAdd);
        fireChildAdded(toAdd, index);
    }

    public void addChild(RTreeElement child) {
        children.add(child);
        fireChildAdded(child, children.size() - 1);
    }

    void addChild(int index, RTreeElement child) {
        children.add(index, child);
        fireChildAdded(child, index);
    }

    public void removeChild(RTreeElement child) {
        int index = children.indexOf(child);
        if (children.remove(child))
            fireChildRemoved(child, index);
    }

    boolean isAncestorOf(RTreeElement target) {
        if (children.contains(target))
            return true;
        for (RTreeElement child : children)
            if (child.isAncestorOf(target))
                return true;
        return false;
    }

    static RTreeElement deepCopy(RTreeElement original, boolean annotateLabel) {
        var copy = new RTreeElement(original);
        if (annotateLabel)
            copy.setName(getCopyName(original));
        for (RTreeElement child : original.getChildren())
            copy.addChild(deepCopy(child, annotateLabel));
        return copy;
    }

    public static String getCopyName(RTreeElement node){
        return node.getName() + COPY_LABEL_SUFFIX;
    }

    public List<RTreeElement> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RTreeElement that)) return false;
        return Objects.equals(getId(), that.getId()) && getType() == that.getType() && Objects.equals(getName(),
                that.getName()) && Objects.equals(getIconLateral(), that.getIconLateral()) && Objects.equals(getStyles(),
                that.getStyles()) && Objects.equals(getChildren(), that.getChildren());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getType(), getName(), getIconLateral(), getStyles(), getChildren());
    }

    @Override
    public String toString() {
        return this.name;
    }

    private void firePropertyChange() {
        if (!changeListeners.isEmpty()){
            for (ChangeListener listener : changeListeners)
                listener.propertyChanged();
        }
    }

    private void fireChildAdded(RTreeElement node, int index) {
        if (!changeListeners.isEmpty()){
            for (ChangeListener listener : changeListeners)
                listener.childAdded(node, index);
        }
    }

    private void fireChildRemoved(RTreeElement node, int index) {
        if (!changeListeners.isEmpty()){
            for (ChangeListener listener : changeListeners)
                listener.childRemoved(node, index);
        }
    }

    synchronized void addChangeListener(ChangeListener listener) {
        if (!changeListeners.contains(listener))
            changeListeners.add(listener);
    }

    synchronized void removeChangeListener(ChangeListener listener) {
        if(!changeListeners.isEmpty()){
            changeListeners.remove(listener);
        }
    }

    synchronized void replaceChangeListener(ChangeListener oldListener, ChangeListener newListener) {
        if (!changeListeners.isEmpty()) {
            changeListeners.remove(oldListener);
            changeListeners.add(newListener);
        }
    }

    public void visitTree(RTreeElement item, Consumer<RTreeElement> onEnter){

        onEnter.accept(item);

        for(var nowItem : item.getChildren()){
            visitTree(nowItem, onEnter);
        }
    }


}
