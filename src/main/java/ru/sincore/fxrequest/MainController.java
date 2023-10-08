package ru.sincore.fxrequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import lombok.extern.java.Log;
import ru.sincore.fxrequest.data.*;
import ru.sincore.fxrequest.ui.StageFactory;
import ru.sincore.fxrequest.ui.controller.CreateProjectController;
import ru.sincore.fxrequest.ui.pc.PCTreeElement;
import ru.sincore.fxrequest.utils.DataType;
import ru.sincore.fxrequest.utils.UIUtils;
import ru.sincore.fxrequest.utils.View;
import ru.sincore.fxrequest.utils.ViewInstance;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;

@Log
public class MainController implements Initializable {

    private final ObservableList<Environment> environments = FXCollections.observableArrayList();

    private final PersistentCollection<Project> projects = new PersistentCollection<>();

    @FXML
    private BorderPane main;
    @FXML
    private ComboBox<Project> projectSelector;
    @FXML
    private ComboBox<Environment> envSelector;
    @FXML
    private TreeView<PCTreeElement> projectsAndCollectionsTree;
    @FXML
    private Button executeRequestButton;
    @FXML
    private ComboBox<HttpMethod> httpMethodSelector;

    @FXML
    private SplitPane splitPane;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            projects.init(DataType.PROJECTS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        projectSelector.setItems(projects);

        projects.stream().filter(Project::getIsDefault).findFirst().ifPresent(p -> projectSelector.getSelectionModel().select(p));

        projectSelector.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Project> call(ListView<Project> param) {
                return new ListCell<>() {
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
        });

        envSelector.setItems(environments);

        splitPane.setDividerPosition(0,0.1);
        httpMethodSelector.getItems().addAll(HttpMethod.getAsList());
        httpMethodSelector.getSelectionModel().select(HttpMethod.GET);

        projectsAndCollectionsTree.setShowRoot(false);

    }

    @FXML
    private void appExit() {
        Platform.exit();
    }

    @FXML
    private void onProjectCreate() throws IOException {
        final StageFactory<Project, CreateProjectController> stageFactory = new StageFactory<>();
        ViewInstance<CreateProjectController> viewInstance = stageFactory.getViewInstance(View.CREATE_PROJECT_VIEW, main.getScene().getWindow());
        viewInstance.getController().init(handleCreateProject, unused -> {
            System.out.println("closed");
        });
        viewInstance.getStage().showAndWait();
    }

    private final Consumer<Project> handleCreateProject = project -> {
        projects.add(project);
        try {
            projects.sync(DataType.PROJECTS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };
}
