package ru.sincore.fxrequest.utils;

import lombok.Getter;
import ru.sincore.fxrequest.data.Environment;
import ru.sincore.fxrequest.data.Project;

@Getter
public enum DataType {
    PROJECTS("projects.json", Project.class),
    ENVS("envs.json", Environment.class),
    REQUESTS("requests.json", Environment.class),
    ;

    private final String fileName;
    private final Class<?> dataTypeClass;

    DataType(String fileName, Class<?> dataTypeClass) {
        this.fileName = fileName;
        this.dataTypeClass = dataTypeClass;
    }

}
