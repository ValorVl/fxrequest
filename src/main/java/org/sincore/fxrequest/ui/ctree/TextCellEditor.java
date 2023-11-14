package org.sincore.fxrequest.ui.ctree;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TextCellEditor implements FancyTreeCellEditor {

	/**
	 * False until an edit has been completed. Then true to prevent further events from duplicating the commit.
	 */
	private boolean done = false;

	static final String NODE_STYLE = "fancyfxtree-default-cell-editor";

    TextCellEditor() {
        field = new TextField("value1");
        field.getStyleClass().add(NODE_STYLE);

        field.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (Boolean.FALSE.equals(newValue) && !done) // focus lost
            {
                done = true;
                cell.getItem().setLabelText(field.getText());
            }
        });
        field.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                cell.getItem().setLabelText(field.getText());
                done = true;
                cell.commitEdit(cell.getItem());
                event.consume();
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                done = true;
                cell.cancelEdit();
                event.consume();
            }
        });
    }

    @Override
    public Node getNode() {
        return field;
    }

    @Override
    public void setCell(FancyTreeCell cell) {
        this.cell = cell;
        field.setText(cell.getItem().getLabelText());
    }

    private final TextField field;
    private FancyTreeCell cell;

    @Override
    public void cancelEdit() {
        done = true;
    }

}
