package org.sincore.fxrequest.ui.ctree.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class RTreeElement implements Serializable {
    private UUID id;
    private RTreeElementType type;
    private String name;
    private String iconLateral;
    private List<String> styles = new ArrayList<>();
    private List<RTreeElement> children = new ArrayList<>();
    private transient List<ChangeListener> changeListeners = new ArrayList<>();

    public RTreeElement(String name, RTreeElementType type){
        this.name = name;
        this.type = type;
        this.id = UUID.randomUUID();
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
            //noinspection Convert2streamapi   (causes a ConcurrentModificationException)
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


}
