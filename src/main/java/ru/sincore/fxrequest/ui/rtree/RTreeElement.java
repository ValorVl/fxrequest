package ru.sincore.fxrequest.ui.rtree;

import lombok.Data;

import java.util.UUID;

@Data
public class RTreeElement {
    private UUID id;
    private RTreeElementType type;
    private String title;
    private String iconLateral;
    private RTreeElement parent;
}
