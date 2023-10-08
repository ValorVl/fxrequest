package ru.sincore.fxrequest.properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.java.Log;
import ru.sincore.fxrequest.data.AppProperties;

@Log
@Getter
public class PropertiesProvider {

    private final ObjectMapper objectMapper;

    public PropertiesProvider(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    private final AppProperties appProperties = new AppProperties();

}
