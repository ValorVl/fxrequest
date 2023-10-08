package ru.sincore.fxrequest.data;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import ru.sincore.fxrequest.utils.DataType;
import ru.sincore.fxrequest.utils.PersistenceUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import static ru.sincore.fxrequest.utils.PersistenceUtils.getMapper;
import static ru.sincore.fxrequest.utils.PersistenceUtils.read;

public class PersistentCollection<T> extends SimpleListProperty<T> {

    public PersistentCollection(){
        super(FXCollections.observableArrayList());
    }

    public void init(DataType dataType) throws IOException {
        addAll(readList(dataType));
    }

    public void sync(DataType dataType) throws IOException {
        saveCollection(dataType);
    }

    private void saveCollection(DataType dataType) throws IOException {
        final byte[] bjson = getMapper().writeValueAsBytes(this);
        if (bjson.length > 0){
            PersistenceUtils.write(bjson, dataType);
        }
    }

    private List<T> readList(DataType dataType) throws IOException {
        final byte[] bjson = read(dataType);

        if (bjson != null && bjson.length > 0){
            var javaType = getMapper().getTypeFactory().constructParametricType(List.class, dataType.getDataTypeClass());
            return getMapper().readValue(bjson, javaType);
        } else {
            return new ArrayList<>();
        }
    }



}
