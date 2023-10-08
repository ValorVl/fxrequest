package ru.sincore.fxrequest.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import ru.sincore.fxrequest.data.Project;
import ru.sincore.fxrequest.ui.Controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.function.Consumer;

public class CreateProjectController implements Controller<Project> {

    @FXML
    private TextField projectNameInput;
    @FXML
    private BorderPane main;

    private Consumer<Project> onComplete = null;
    private Consumer<Void> onClose = null;

    private final ValidationSupport validationSupport = new ValidationSupport();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        validationSupport.registerValidator(projectNameInput, Validator.createEmptyValidator("Can't be empty"));
    }

    @Override
    public void init(Consumer<Project> onComplete, Consumer<Void> onClose) {
        this.onComplete = onComplete;
        this.onClose = onClose;
    }

    @FXML
    private void onClose() {
        onClose.accept(null);
        projectNameInput.clear();
        main.getScene().getWindow().hide();
    }

    @FXML
    private void onSave() {
        var text = projectNameInput.getText();
        var project = new Project();
            project.setId(UUID.randomUUID());
            project.setTitle(text);
            project.setIsDefault(false);
            project.setCreatedAt(LocalDateTime.now());

        if(!validationSupport.isInvalid()){
            onComplete.accept(project);
            onClose();
        } else {
            validationSupport.redecorate();
        }
    }
}
