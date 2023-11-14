package org.sincore.fxrequest.ui.ctree;

import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import org.sincore.fxrequest.ui.ctree.request.RTreeElement;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class FancyTreeKeyHandler {
    private final TreeView<FancyTreeNodeFacade<RTreeElement>> tree;
    private final AbstractTreeOperationHandler<FancyTreeNodeFacade<RTreeElement>> handler;

    public FancyTreeKeyHandler(TreeView<FancyTreeNodeFacade<RTreeElement>> treeView, AbstractTreeOperationHandler<FancyTreeNodeFacade<RTreeElement>> handler) {
        tree = treeView;
        this.handler = handler;

        tree.setOnKeyPressed(event -> {
            var selectedItems = tree.getSelectionModel().getSelectedItems();
            boolean handled;
            if (event.getCode().equals(KeyCode.DELETE) && !event.isShiftDown())
                handled = this.handler.handleDelete(selectedItems);
            else if ((event.isShortcutDown() && event.getCode().equals(KeyCode.C))
                    || (event.isShortcutDown() && event.getCode().equals(KeyCode.INSERT)))
                handled = this.handler.handleCopy(selectedItems);
            else if ((event.isShortcutDown() && event.getCode().equals(KeyCode.X))
                    || (event.isShiftDown() && event.getCode().equals(KeyCode.DELETE)))
                handled = this.handler.handleCut(selectedItems);
            else if ((event.isShortcutDown() && event.getCode().equals(KeyCode.V))
                    || (event.isShiftDown() && event.getCode().equals(KeyCode.INSERT)))
                handled = this.handler.handlePaste(selectedItems);
            else if (event.isShortcutDown() && event.getCode().equals(KeyCode.Z))
                handled = this.handler.handleUndo();
            else
                return;

            if (handled)
                event.consume();
        });
    }
}


