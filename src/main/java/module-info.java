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

    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires org.slf4j;

    exports org.sincore.fxrequest;
    exports org.sincore.fxrequest.data;
    exports org.sincore.fxrequest.ui.ctree.request;
    exports org.sincore.fxrequest.utils;
    exports org.sincore.fxrequest.ui.ctree;

}
