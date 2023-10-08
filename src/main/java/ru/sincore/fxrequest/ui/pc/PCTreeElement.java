package ru.sincore.fxrequest.ui.pc;

import lombok.Data;

import java.util.UUID;

@Data
public class PCTreeElement {
    private UUID id;
    private PCTreeElementType type;
    private String title;
    private String iconLateral;
}
