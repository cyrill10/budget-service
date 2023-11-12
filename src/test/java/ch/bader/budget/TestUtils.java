package ch.bader.budget;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class TestUtils {

    public static String loadFileAsString(String fileName) throws IOException, URISyntaxException {
        ClassLoader classLoader = TestUtils.class.getClassLoader();
        Path path = Path.of(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
        return Files.readString(path);
    }

    public static File loadFile(String fileName) {
        ClassLoader classLoader = TestUtils.class.getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
    }

    public static String asJsonString(final Object obj) {
        JavaTimeModule module = new JavaTimeModule();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
