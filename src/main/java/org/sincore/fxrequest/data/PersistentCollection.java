package org.sincore.fxrequest.data;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import org.sincore.fxrequest.utils.DataType;
import org.sincore.fxrequest.utils.PersistenceUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.sincore.fxrequest.utils.PersistenceUtils.getMapper;
import static org.sincore.fxrequest.utils.PersistenceUtils.read;

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
        final var bjson = getMapper().writeValueAsBytes(this);
        if (bjson.length > 0){
            PersistenceUtils.write(bjson, dataType, null);
        }
    }

    private List<T> readList(DataType dataType) throws IOException {
        final var bjson = read(dataType);

        if (bjson != null && bjson.length > 0){
            var javaType = getMapper().getTypeFactory().constructParametricType(List.class, dataType.getDataTypeClass());
            return getMapper().readValue(bjson, javaType);
        } else {
            return new ArrayList<>();
        }
    }



}
