package ru.sincore.fxrequest.ui;

import javafx.fxml.Initializable;

import java.util.function.Consumer;

public interface Controller<T> extends Initializable {
    void init(Consumer<T> onComplete, Consumer<Void> onClose);

}
