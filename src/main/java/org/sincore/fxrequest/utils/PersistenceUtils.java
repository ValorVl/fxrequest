package org.sincore.fxrequest.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.java.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;

@Log
public class PersistenceUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(DateFormat.getDateInstance());
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    private static final String APP_DATA = ".fxrequest";

    public static ObjectMapper getMapper(){
        return objectMapper;
    }

    public static byte[] read(DataType dataType) throws IOException {
        var dataDir = getAppDataFolderPath().toAbsolutePath().normalize().toString();
        var pathToDataFile = Paths.get(dataDir, dataType.getFileName()).toFile();
        if (!pathToDataFile.exists()){
            return null;
        }
        return Files.readAllBytes(pathToDataFile.toPath());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void write(byte[] data, DataType dataType) throws IOException {
        var dataDir = PersistenceUtils.getAppDataFolderPath().toAbsolutePath().normalize().toString();
        var pathToDataFile = Paths.get(dataDir, dataType.getFileName()).toFile();

        if (!pathToDataFile.exists()){
            pathToDataFile.setWritable(true, true);
            pathToDataFile.setReadable(true, true);
            pathToDataFile.createNewFile();
        }
        Files.write(pathToDataFile.toPath(), data, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static Path getAppDataFolderPath(){
        return Paths.get(System.getProperty("user.home"), APP_DATA);
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void makeDataFolder(){
        if (!isDataFolderExist()){
            getAppDataFolderPath().toFile().mkdirs();
        }
    }


    public static boolean isDataFolderExist(){
        var path = getAppDataFolderPath().toFile();
        return path.isDirectory() && path.exists();
    }

}
