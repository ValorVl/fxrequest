open module ru.sincore.fxrequest {
    requires java.base;
    requires java.net.http;
    requires java.logging;

    requires javafx.controls;
    requires javafx.fxml;

    requires lombok;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.fluentui;

    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports ru.sincore.fxrequest;
    exports ru.sincore.fxrequest.data;
    exports ru.sincore.fxrequest.ui.rtree;
    exports ru.sincore.fxrequest.utils;

}
