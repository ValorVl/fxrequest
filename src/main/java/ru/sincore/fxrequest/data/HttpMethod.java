package ru.sincore.fxrequest.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.List;

@Getter
public enum HttpMethod {
    POST("POST"),
    GET("GET"),
    PUT("PUT"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
    PATCH("PATCH"),
    DELETE("DELETE");

    private final String methodName;

    HttpMethod(String methodName) {
        this.methodName = methodName;
    }

    public static ObservableList<HttpMethod> getAsList(){
        return FXCollections.observableList(List.of(HttpMethod.values()));
    }
}
