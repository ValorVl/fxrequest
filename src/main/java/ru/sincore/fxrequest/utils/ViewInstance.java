package ru.sincore.fxrequest.utils;

import javafx.stage.Stage;
import lombok.Data;
import ru.sincore.fxrequest.ui.Controller;

@Data
public class ViewInstance<T>{
    private T controller;
    private Stage stage;
};
