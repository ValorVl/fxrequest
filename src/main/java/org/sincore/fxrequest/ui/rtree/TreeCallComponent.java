package org.sincore.fxrequest.ui.rtree;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class TreeCallComponent extends HBox {
    private final FontIcon icon = new FontIcon();
    private final Label text = new Label();

    {
        getChildren().addAll(icon, text);
    }

    public TreeCallComponent(RTreeElement element){
        icon.setIconLiteral(element.getIconLateral());
        icon.setIconSize(18);
        this.text.setText(element.getTitle());
    }
}
