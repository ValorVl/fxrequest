package org.sincore.fxrequest.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;

@Slf4j
public final class PersistenceUtils {

    private PersistenceUtils(){}

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(DateFormat.getDateInstance());
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    private static final String APP_DATA = ".fxrequest";

    public static ObjectMapper getMapper(){
        return objectMapper;
    }

    public static byte[] read(DataType dataType) throws IOException {
        var dataDir = getAppDataFolderPath().toAbsolutePath().normalize().toString();
        var pathToDataFile = Paths.get(dataDir, dataType.getFileName()).toFile();
        if (!pathToDataFile.exists()){
            return new byte[0];
        }
        return Files.readAllBytes(pathToDataFile.toPath());
    }

    public static void write(byte[] data, DataType dataType, Path subFolders) throws IOException {
        var dataDir = PersistenceUtils.getAppDataFolderPath().toAbsolutePath().normalize().toString();
        Path pathToDataFile;

        if (subFolders == null){
            pathToDataFile = Paths.get(dataDir, dataType.getFileName());
        } else {
            var pathWithSubFolder = Paths.get(dataDir, subFolders.toString());
            var isExist = pathWithSubFolder.toFile().exists();
            if(!isExist){
                var isCreated = pathWithSubFolder.toFile().mkdirs();
                if(isCreated){
                    log.debug("Folder created {}", pathWithSubFolder.toAbsolutePath());
                }
            }
            pathToDataFile = Paths.get(pathWithSubFolder.toString(), dataType.getFileName());
        }

        Files.write(pathToDataFile, data, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }

    private static Path getAppDataFolderPath(){
        return Paths.get(System.getProperty("user.home"), APP_DATA);
    }


    public static void makeDataFolder(){
        var dataDirectory = getAppDataFolderPath().toFile();
        if (!isDataFolderExist()){
            final boolean isFolderCreated = dataDirectory.mkdirs();
            if (!isFolderCreated){
                log.error("Data directory {} is not created", dataDirectory);
            }
        }
    }


    public static boolean isDataFolderExist(){
        var path = getAppDataFolderPath().toFile();
        return path.isDirectory() && path.exists();
    }

}
