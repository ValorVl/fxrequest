package org.sincore.fxrequest.ui;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.sincore.fxrequest.data.Project;

public final class SelectorCellFactories  {

    private SelectorCellFactories(){}

    public static final Callback<ListView<Project>, ListCell<Project>> projectProjectCallback = c -> new ListCell<>() {
        @Override
        protected void updateItem(Project item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                setText(item.getTitle());
            } else {
                setText(null);
            }
        }
    };
}
