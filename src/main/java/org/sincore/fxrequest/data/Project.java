package org.sincore.fxrequest.data;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Project {
    private UUID id;
    private String title;
    private Boolean isDefault;
    private LocalDateTime createdAt;

    public String toString(){
        return this.title;
    }
}
