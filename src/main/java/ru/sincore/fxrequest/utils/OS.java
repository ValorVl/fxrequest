package ru.sincore.fxrequest.utils;

import java.util.Locale;

public enum OS {
    OSX("mac"),
    WIN("win"),
    LINUX("linux"),
    UNKNOWN("unknown");

    private final String osName;

    OS(String name){
        this.osName = name;
    }

    public String getOsName(){
        return this.osName;
    }

    public OS getOs(){
        var osFromEnv = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        switch (osFromEnv){
            case "win" -> {
                return OS.WIN;
            }
            case "mac" -> {
                return OS.OSX;
            }
            case "linux" -> {
                return OS.LINUX;
            }
            default -> {
                return OS.UNKNOWN;
            }
        }
    }
}
