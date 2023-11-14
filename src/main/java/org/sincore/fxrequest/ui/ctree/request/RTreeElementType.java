package org.sincore.fxrequest.ui.ctree.request;

import lombok.Getter;

@Getter
public enum RTreeElementType {
    ROOT("fltfal-collections-24"),
    FOLDER("fltral-folder-24"),
    REQUEST("fltfal-document-24");

    private final String iconLateral;

    RTreeElementType(String iconLateral){
        this.iconLateral = iconLateral;
    }

}
