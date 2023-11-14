package org.sincore.fxrequest.ui.rtree;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import lombok.Data;
import org.sincore.fxrequest.data.HierarchyData;

import java.io.Serializable;
import java.util.UUID;

@Data
public class RTreeElement implements Serializable {
    private UUID id;
    private RTreeElementType type;
    private String title;
    private String iconLateral;
    private UUID parentId;
}
