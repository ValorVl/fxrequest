package org.sincore.fxrequest.ui.ctree.request;

public interface ChangeListener {
    void childAdded(RTreeElement child, int index);
    void childRemoved(RTreeElement child, int index);
    void propertyChanged();
}
