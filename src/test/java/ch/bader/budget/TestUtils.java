package ch.bader.budget;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class TestUtils {

    public static String loadFileAsString(final String fileName) throws IOException, URISyntaxException {
        final ClassLoader classLoader = TestUtils.class.getClassLoader();
        final Path path = Path.of(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
        return Files.readString(path);
    }

    public static File loadFile(final String fileName) {
        final ClassLoader classLoader = TestUtils.class.getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
    }

    public static String asJsonString(final Object obj) {
        final Jsonb jsonb = JsonbBuilder.create();
        return jsonb.toJson(obj);
    }
}
