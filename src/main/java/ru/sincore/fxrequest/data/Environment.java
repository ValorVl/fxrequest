package ru.sincore.fxrequest.data;

import lombok.Data;

import java.util.UUID;

@Data
public class Environment {
    private String title;
    private UUID projectId;
}
