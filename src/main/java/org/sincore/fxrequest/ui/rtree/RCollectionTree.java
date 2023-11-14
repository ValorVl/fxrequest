package org.sincore.fxrequest.ui.rtree;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.scene.control.FocusModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.sincore.fxrequest.data.PersistentCollection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RCollectionTree<T extends RTreeElement> extends TreeView<RTreeElement> {

    private final Map<TreeItem<T>, WeakListChangeListener<T>> weakListeners = new HashMap<>();

    private final ObjectProperty<ObservableList<? extends T>> elements = new SimpleObjectProperty<>();
    private final PersistentCollection<RTreeElement> treeElements = new PersistentCollection<>();

    public RCollectionTree(){
        super();
        init();
    }

    private void init(){

        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        addRootElement();
        setEditable(true);
        setCellFactory(new RTreeCellFactory());


//        rootProperty().addListener((observable, oldValue, newValue) -> {
//            clear(oldValue);
//            updateItems();
//        });
//
//        setItems(FXCollections.observableArrayList());
//
//        elements.addListener(observable -> {
//            clear(getRoot());
//            updateItems();
//        });
    }

//    private void clear(TreeItem<T> root) {
//        if (root != null) {
//            for (TreeItem<T> treeItem : root.getChildren()) {
//                removeRecursively(treeItem);
//            }
//
//            removeRecursively(root);
//            root.getChildren().clear();
//        }
//    }

//    private void updateItems() {
//
//        if (getItems() != null) {
//            for (T value : getItems()) {
//                getRoot().getChildren().add(addRecursively(value));
//            }
//
//            ListChangeListener<T> rootListener = getListChangeListener(getRoot().getChildren());
//            WeakListChangeListener<T> weakListChangeListener = new WeakListChangeListener<>(rootListener);
//            weakListeners.put(getRoot(), weakListChangeListener);
//            getItems().addListener(weakListChangeListener);
//        }
//    }

//    private ListChangeListener<T> getListChangeListener(final ObservableList<TreeItem<T>> treeItemChildren) {
//        return change -> {
//            while (change.next()) {
//                if (change.wasUpdated()) {
//                    // http://javafx-jira.kenai.com/browse/RT-23434
//                    //todo add update method
//                    continue;
//                }
//                if (change.wasRemoved()) {
//                    for (int i = change.getRemovedSize() - 1; i >= 0; i--) {
//                        removeRecursively(treeItemChildren.remove(change.getFrom() + i));
//                    }
//                }
//                // If items have been added
//                if (change.wasAdded()) {
//                    // Get the new items
//                    for (int i = change.getFrom(); i < change.getTo(); i++) {
//                        treeItemChildren.add(i, addRecursively(change.getList().get(i)));
//                    }
//                }
//                // If the list was sorted.
//                if (change.wasPermutated()) {
//                    // Store the new order.
//                    Map<Integer, TreeItem<T>> tempMap = new HashMap<>();
//
//                    for (int i = change.getTo() - 1; i >= change.getFrom(); i--) {
//                        int a = change.getPermutation(i);
//                        tempMap.put(a, treeItemChildren.remove(i));
//                    }
//
//                    getSelectionModel().clearSelection();
//
//                    // Add the items in the new order.
//                    for (int i = change.getFrom(); i < change.getTo(); i++) {
//                        treeItemChildren.add(tempMap.remove(i));
//                    }
//                }
//            }
//        };
//    }

//    private void removeRecursively(TreeItem<T> item) {
//        if (item.getValue() != null && item.getValue().getChildren() != null) {
//
//            if (weakListeners.containsKey(item)) {
//                item.getValue().getChildren().removeListener(weakListeners.remove(item));
//            }
//            for (TreeItem<T> treeItem : item.getChildren()) {
//                removeRecursively(treeItem);
//            }
//        }
//    }

//    private TreeItem<T> addRecursively(T value) {
//
//        TreeItem<T> treeItem = new TreeItem<>(value);
//        treeItem.setExpanded(true);
//
//        if (value != null && value.getChildren() != null) {
//            ListChangeListener<T> listChangeListener = getListChangeListener(treeItem.getChildren());
//            WeakListChangeListener<T> weakListener = new WeakListChangeListener<>(listChangeListener);
//            value.getChildren().addListener(weakListener);
//
//            weakListeners.put(treeItem, weakListener);
//            for (T child : value.getChildren()) {
//                treeItem.getChildren().add(addRecursively(child));
//            }
//        }
//        return treeItem;
//    }



    public void addTreeElement(final String title, RTreeElementType type){
        var selectedItem = getSelectionModel().getSelectedItem();

        if (RTreeElementType.REQUEST != selectedItem.getValue().getType()){
            var folder = buildElement(type, selectedItem.getValue().getId());
                folder.setIconLateral(type.getIconLateral());
                folder.setTitle(title);
            var treeItem = new TreeItem<>(folder);
                treeItem.setExpanded(true);

            selectedItem.getChildren().add(treeItem);
            getSelectionModel().select(treeItem);
        }
        requestFocus();
    }

    public void removeSelectedElement(){
        var selectedElement = getSelectionModel().getSelectedItem();
        if (RTreeElementType.ROOT != selectedElement.getValue().getType()){
            selectedElement.getParent().getChildren().remove(selectedElement);
        }
    }

    private void addRootElement(){
        var folder = buildElement(RTreeElementType.ROOT, null);
        folder.setIconLateral(RTreeElementType.ROOT.getIconLateral());
        var rootTreeItem = new TreeItem<>(folder);
        rootTreeItem.setExpanded(true);
        setRoot(rootTreeItem);
        getSelectionModel().select(rootTreeItem);
    }

    private RTreeElement buildElement(RTreeElementType type, UUID parentElementId){
        var element = new RTreeElement();
        element.setId(UUID.randomUUID());
        element.setType(type);
        element.setParentId(parentElementId);
        return element;
    }



}
