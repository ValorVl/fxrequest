package ru.sincore.fxrequest.data;

import lombok.Data;

@Data
public class AppProperties {

    private String appName;
    private String appVersion;

    public String getFullVersion(){
        return appName + " " + appVersion;
    }
}
