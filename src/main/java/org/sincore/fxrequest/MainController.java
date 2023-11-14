package org.sincore.fxrequest;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import lombok.extern.java.Log;
import org.sincore.fxrequest.data.Environment;
import org.sincore.fxrequest.data.HttpMethod;
import org.sincore.fxrequest.data.PersistentCollection;
import org.sincore.fxrequest.data.Project;
import org.sincore.fxrequest.ui.StageFactory;
import org.sincore.fxrequest.ui.controller.CreateProjectController;
import org.sincore.fxrequest.ui.ctree.FancyTreeView;
import org.sincore.fxrequest.ui.ctree.request.OpsHandler;
import org.sincore.fxrequest.ui.ctree.request.RTNodeBuilder;
import org.sincore.fxrequest.ui.ctree.request.RTreeElement;
import org.sincore.fxrequest.ui.ctree.request.RtreeNodeFacade;
import org.sincore.fxrequest.utils.DataType;
import org.sincore.fxrequest.utils.View;
import org.sincore.fxrequest.utils.ViewInstance;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;

@Log
public class MainController implements Initializable {

    private final ObservableList<Environment> environments = FXCollections.observableArrayList();
    private final PersistentCollection<Project> projects = new PersistentCollection<>();

    @FXML
    private BorderPane requestCollectionTreeWrapper;
    @FXML
    private BorderPane main;
    @FXML
    private ComboBox<Project> projectSelector;
    @FXML
    private ComboBox<Environment> envSelector;
    @FXML
    private Button executeRequestButton;
    @FXML
    private ComboBox<HttpMethod> httpMethodSelector;
    @FXML
    private SplitPane splitPane;

    private FancyTreeView<RtreeNodeFacade> collectionTreeView;
    private RTreeElement rootNode = RTNodeBuilder.rootNode();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            projects.init(DataType.PROJECTS);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Can't");
        }

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



        collectionTreeView = new FancyTreeView<>(new OpsHandler(rootNode), true);
        collectionTreeView.setRoot(new RtreeNodeFacade(rootNode));
        collectionTreeView.expandAll();
        collectionTreeView.setShowRoot(false);
        collectionTreeView.setEditable(true);
        collectionTreeView.getStylesheets().add(getClass().getResource("tree.css").toExternalForm());
        collectionTreeView.setContextMenu(initTreeViewContextMeny());

        requestCollectionTreeWrapper.autosize();
        requestCollectionTreeWrapper.setCenter(collectionTreeView);
    }

    @FXML
    private void appExit() {
        Platform.exit();
    }

    @FXML
    private void onProjectCreate() throws IOException {
        final StageFactory<Project, CreateProjectController> stageFactory = new StageFactory<>();
        ViewInstance<CreateProjectController> viewInstance = stageFactory.getViewInstance(View.CREATE_PROJECT_VIEW, main.getScene().getWindow());
        viewInstance.getController().init(handleCreateProject, unused -> System.out.println("closed"));
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


    @FXML
    private void createFolderAction(ActionEvent event) {
        var folderCreationDialog = new TextInputDialog();
        folderCreationDialog.setTitle("Creating folder");
        folderCreationDialog.setContentText("Folder name");
        folderCreationDialog.showAndWait().ifPresent(folderName -> {
            var selectionModel = collectionTreeView.getSelectionModel();
            var selectedItem = selectionModel.getSelectedItem();
            if(selectedItem == null){
                selectedItem = collectionTreeView.getRoot();
            }
            var modelNode = selectedItem.getValue().getModelNode();
            var parent = rootNode.findParentFor(modelNode);
            parent.addAfter(RTNodeBuilder.createFolderNode(folderName), modelNode);
        });
    }

    @FXML
    private void createRequestAction() {
        var folderCreationDialog = new TextInputDialog();
        folderCreationDialog.setTitle("Creating request");
        folderCreationDialog.setContentText("Request name");
//        folderCreationDialog.showAndWait().ifPresent(folderName -> requestCollectionTree.addTreeElement(
//                folderName,
//                RTreeElementType.REQUEST
//        ));
    }

    @FXML
    public void removeElementAction() {
        //requestCollectionTree.removeSelectedElement();
    }

    private ContextMenu initTreeViewContextMeny(){
        var addNewFolder = new MenuItem();
            addNewFolder.setText("Add new folder");
            addNewFolder.setOnAction(this::createFolderAction);
        var menu = new ContextMenu();
            menu.getItems().add(addNewFolder);
        return menu;
    }
}
