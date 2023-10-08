package ru.sincore.fxrequest.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.apache.hc.core5.http.Method;

import java.util.List;

@Getter
public enum HttpMethod {
    POST("POST", Method.POST),
    GET("GET", Method.GET),
    PUT("PUT", Method.PUT),
    HEAD("HEAD", Method.HEAD),
    OPTIONS("OPTIONS", Method.OPTIONS),
    PATCH("PATCH", Method.PATCH),
    DELETE("DELETE", Method.DELETE);

    private final String methodName;

    HttpMethod(String methodName, Method method) {
        this.methodName = methodName;
    }

    public static ObservableList<HttpMethod> getAsList(){
        return FXCollections.observableList(List.of(HttpMethod.values()));
    }
}
