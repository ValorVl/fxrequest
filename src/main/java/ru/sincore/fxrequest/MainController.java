package ru.sincore.fxrequest;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import lombok.extern.java.Log;
import ru.sincore.fxrequest.data.*;
import ru.sincore.fxrequest.ui.StageFactory;
import ru.sincore.fxrequest.ui.controller.CreateProjectController;
import ru.sincore.fxrequest.ui.rtree.RTreeElement;
import ru.sincore.fxrequest.utils.DataType;
import ru.sincore.fxrequest.utils.View;
import ru.sincore.fxrequest.utils.ViewInstance;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.function.Consumer;

@Log
public class MainController implements Initializable {

    private final ObservableList<Environment> environments = FXCollections.observableArrayList();

    private final PersistentCollection<Project> projects = new PersistentCollection<>();
    private final PersistentCollection<RTreeElement> rTree = new PersistentCollection<>();

    @FXML
    private BorderPane main;
    @FXML
    private ComboBox<Project> projectSelector;
    @FXML
    private ComboBox<Environment> envSelector;
    @FXML
    private TreeView<RTreeElement> requestCollectionTree;
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

        //requestCollectionTree.setShowRoot(false);
        initRTree();

        projectSelector.setItems(projects);

        projectSelector.setOnAction(event -> markProjectAsLastActive());
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

        splitPane.setDividerPosition(0,0.3);
        httpMethodSelector.getItems().addAll(HttpMethod.getAsList());
        httpMethodSelector.getSelectionModel().select(HttpMethod.GET);

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

    /**
     * TODO: In the future, it should be moved to the utility class
     * Mark the selected project as the last active project.
     * <br>
     * <br>
     * This method retrieves the selected project from the project selector and sets it as the default project.
     * It also sets the isDefault flag of other projects to false.
     * After updating the projects list, it syncs the changes to the data storage.
     *
     * @throws RuntimeException if an IOException occurs while performing the data synchronization
     */
    private void markProjectAsLastActive(){
        var selectedProject = projectSelector.getValue();
        projects.replaceAll(project -> {
            if (project.getIsDefault()){
                project.setIsDefault(false);
            }
            if (project.getId().equals(selectedProject.getId())){
                project.setIsDefault(true);
            }
            return project;
        });

        try {
            projects.sync(DataType.PROJECTS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initRTree(){
        TreeItem<RTreeElement> rootElement = new TreeItem<>();
        RTreeElement element = new RTreeElement();
        element.setId(UUID.randomUUID());
        element.setTitle("root");
        element.setParent(null);
        rootElement.setValue(element);
        requestCollectionTree.setRoot(rootElement);
    }

    private void fillRTree() throws IOException {
        rTree.init(DataType.PROJECTS);
        var root = requestCollectionTree.getRoot();
        var selectedProject = projectSelector.getValue();

        //root.getChildren().add()
    }
}
