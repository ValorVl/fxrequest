package org.sincore.fxrequest.data;

import javafx.collections.ObservableList;

public interface HierarchyData<T extends HierarchyData<T>> {
    ObservableList<T> getChildren();
}
