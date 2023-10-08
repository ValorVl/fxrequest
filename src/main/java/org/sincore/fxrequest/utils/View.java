package org.sincore.fxrequest.utils;

import lombok.Getter;

@Getter
public enum View {
    CREATE_PROJECT_VIEW("/org/sincore/fxrequest/ui/controller/create-project-view.fxml", 600, 150, "Create project");

    private final String fxml;
    private final int width;
    private final int height;
    private final String title;

    View(String fxml, int width, int height, String title) {
        this.fxml = fxml;
        this.width = width;
        this.height = height;
        this.title = title;
    }

}
