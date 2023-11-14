package org.sincore.fxrequest.ui.ctree;

import javafx.scene.control.*;
import javafx.scene.control.skin.*;
import org.sincore.fxrequest.ui.ctree.request.RTreeElement;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class FancyTreeViewSkin extends TreeViewSkin<RTreeElement> {
    FancyTreeViewSkin(TreeView tree) {
        super(tree);
    }

    boolean isIndexVisible(int index) {
        VirtualFlow<TreeCell<RTreeElement>> flow = getVirtualFlow();
            return flow.getFirstVisibleCell() != null &&
                    flow.getLastVisibleCell() != null &&
                    flow.getFirstVisibleCell().getIndex() <= index &&
                    flow.getLastVisibleCell().getIndex() >= index;
    }
}


