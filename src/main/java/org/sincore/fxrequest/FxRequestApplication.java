package org.sincore.fxrequest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.sincore.fxrequest.utils.PersistenceUtils;

import java.io.IOException;

public class FxRequestApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FxRequestApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
        stage.setTitle("FxRequest");
        stage.setScene(scene);
        stage.setOnShowing(event -> PersistenceUtils.makeDataFolder());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


}
