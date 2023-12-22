package org.sincore.fxrequest.ui;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.sincore.fxrequest.data.RequestParam;

public class RequestParamsTableView extends TableView<RequestParam> {

    public RequestParamsTableView(){
        TableColumn<RequestParam, String> key = new TableColumn<>("Key");
        key.setSortable(false);
        key.setPrefWidth(300);

        TableColumn<RequestParam, String> vale = new TableColumn<>("Value");
        vale.setSortable(false);
        vale.setPrefWidth(300);

        TableColumn<RequestParam, String> description = new TableColumn<>("Description");
        description.setSortable(false);
        description.prefWidthProperty().bind(this.widthProperty().divide(3));

        key.setCellValueFactory(new PropertyValueFactory<>("key"));
        key.setCellValueFactory(new PropertyValueFactory<>("value"));
        key.setCellValueFactory(new PropertyValueFactory<>("description"));

        getColumns().addAll(key, vale, description);
    }

}
