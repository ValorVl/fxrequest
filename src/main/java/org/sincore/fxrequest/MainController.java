package org.sincore.fxrequest;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.StatusBar;
import org.kordamp.ikonli.javafx.FontIcon;
import org.sincore.fxrequest.data.*;
import org.sincore.fxrequest.ui.RequestParamsTableView;
import org.sincore.fxrequest.ui.SelectorCellFactories;
import org.sincore.fxrequest.ui.StageFactory;
import org.sincore.fxrequest.ui.controller.CreateProjectController;
import org.sincore.fxrequest.ui.ctree.FancyTreeView;
import org.sincore.fxrequest.ui.ctree.request.*;
import org.sincore.fxrequest.utils.AppExceptionDialog;
import org.sincore.fxrequest.utils.DataType;
import org.sincore.fxrequest.utils.View;
import org.sincore.fxrequest.utils.ViewInstance;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@Slf4j
public class MainController implements Initializable {

    ObservableList<RequestParam> requestParamsData = FXCollections.observableArrayList();

    private final ObservableList<Environment> environments = FXCollections.observableArrayList();
    private final PersistentCollection<Project> projects = new PersistentCollection<>();

    @FXML
    private RequestParamsTableView requestParamTable;
    @FXML
    private StatusBar appStatusBar;
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
    private final RTreeElement rootNode = RTNodeBuilder.rootNode(new int[1]);

    private OpsHandler opsHandler = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info("application started");
        try {
            projects.init(DataType.PROJECTS);
        } catch (IOException e) {
            log.info(">>>", e);
        }

        Platform.runLater(() -> appStatusBar.setText("App started"));

        projectSelector.setItems(projects);
        projectSelector.setOnAction(event -> markProjectAsLastActive());
        projects.stream().filter(Project::getIsDefault).findFirst().ifPresent(p -> projectSelector.getSelectionModel().select(p));
        projectSelector.setCellFactory(SelectorCellFactories.projectProjectCallback);

        envSelector.setItems(environments);

        splitPane.setDividerPosition(0,0.3);
        httpMethodSelector.getItems().addAll(HttpMethod.getAsList());
        httpMethodSelector.getSelectionModel().select(HttpMethod.GET);

        this.opsHandler = new OpsHandler(rootNode);

        collectionTreeView = new FancyTreeView<>(opsHandler, true);
        collectionTreeView.setRoot(new RtreeNodeFacade(rootNode));
        collectionTreeView.expandAll();
        collectionTreeView.setShowRoot(false);
        collectionTreeView.setEditable(true);
        collectionTreeView.getSelectionModel().select(collectionTreeView.getRoot());
        collectionTreeView.getStylesheets().add(getClass().getResource("tree.css").toExternalForm());
        collectionTreeView.setContextMenu(initTreeViewContextMenu());

        requestCollectionTreeWrapper.autosize();
        requestCollectionTreeWrapper.setCenter(collectionTreeView);

        requestParamTable.setItems(requestParamsData);

        requestParamTable.setEditable(true);
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
            new AppExceptionDialog(e, null).show();
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
            if (Boolean.TRUE.equals(project.getIsDefault())){
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

        collectionTreeView.setProjectId(projectSelector.getValue().getId());
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
                var modelNodeRoot = selectedItem.getValue().getModelNode();
                modelNodeRoot.addAfter(RTNodeBuilder.createFolderNode(folderName), modelNodeRoot);
            } else if (selectedItem.getValue().getModelNode().getType() != RTreeElementType.REQUEST) {
                var modelNode = selectedItem.getValue().getModelNode();
                modelNode.addAfter(RTNodeBuilder.createFolderNode(folderName), modelNode);
            }
            //drop selection
            collectionTreeView.getSelectionModel().select(collectionTreeView.getRoot());
            //collectionTreeView.persist(projectSelector.getValue().getId());
        });
    }

    @FXML
    private void createRequestAction(ActionEvent event) {
        var folderCreationDialog = new TextInputDialog();
        folderCreationDialog.setTitle("Creating request");
        folderCreationDialog.setContentText("Request name");
        folderCreationDialog.showAndWait().ifPresent(folderName -> {
            var selectionModel = collectionTreeView.getSelectionModel();
            var selectedItem = selectionModel.getSelectedItem();
            if(selectedItem == null){
                selectedItem = collectionTreeView.getRoot();
                var modelNodeRoot = selectedItem.getValue().getModelNode();
                modelNodeRoot.addAfter(RTNodeBuilder.createRequestNode(folderName), modelNodeRoot);
            } else if (selectedItem.getValue().getModelNode().getType() != RTreeElementType.REQUEST) {
                var modelNode = selectedItem.getValue().getModelNode();
                modelNode.addAfter(RTNodeBuilder.createRequestNode(folderName), modelNode);
            }
            //drop selection
            collectionTreeView.getSelectionModel().select(collectionTreeView.getRoot());
            //collectionTreeView.persist(projectSelector.getValue().getId());
        });
    }

    private void deleteElementAction(ActionEvent event){
        collectionTreeView.getSelectionModel().getSelectedItem().getValue().destroy();
    }

    private ContextMenu initTreeViewContextMenu(){
        var separator = new SeparatorMenuItem();
        var addNewFolder = new MenuItem();
            addNewFolder.setText("Add new folder");
            addNewFolder.setGraphic(new FontIcon(RTreeElementType.FOLDER.getIconLateral()));
            addNewFolder.setOnAction(this::createFolderAction);
        var addRequest = new MenuItem();
            addRequest.setText("Add new request");
            addRequest.setGraphic(new FontIcon(RTreeElementType.REQUEST.getIconLateral()));
            addRequest.setOnAction(this::createRequestAction);
        var devTakeTreeSnapshot = new MenuItem("Take snapshot");
        var menu = new ContextMenu();
            menu.getItems().addAll(addNewFolder, addRequest, separator, devTakeTreeSnapshot);
        return menu;
    }

    @FXML
    private void deleteSelection(ActionEvent event) {
        var selectedIndex = collectionTreeView.getSelectionModel().getSelectedItems();
        opsHandler.handleDelete(selectedIndex);
    }
}
