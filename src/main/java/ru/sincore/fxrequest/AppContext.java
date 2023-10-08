package ru.sincore.fxrequest;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import ru.sincore.fxrequest.data.AppProperties;
import ru.sincore.fxrequest.properties.PropertiesProvider;

import java.text.DateFormat;


@Getter
public class AppContext {

    private final ObjectMapper objectMapper = setUpObjectMapper();
    private final PropertiesProvider propertiesProvider = new PropertiesProvider(setUpObjectMapper());

    public void init(){

    }

    private ObjectMapper setUpObjectMapper(){
        return null;
    }

}
