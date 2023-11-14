package org.sincore.fxrequest.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.sincore.fxrequest.utils.View;
import org.sincore.fxrequest.utils.ViewInstance;

import java.io.IOException;

public class StageFactory<O, T extends Controller<O>> {
    public ViewInstance<T> getViewInstance(View view, Window owner) throws IOException {
        var fxmlResource = getClass().getResource(view.getFxml());
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlResource);
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(fxmlLoader.load(), view.getWidth(), view.getHeight()));
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(view.getTitle());

        T controller = fxmlLoader.getController();

        ViewInstance<T> viewInstance = new ViewInstance<>();
        viewInstance.setStage(stage);
        viewInstance.setController(controller);
        return viewInstance;
    }

}
