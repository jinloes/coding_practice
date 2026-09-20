package com.jinloes.practice_plugin.catalog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bundled assertion libraries that the generated example runner compiles and runs against.
 */
public final class ExampleLibraries {
    private static final String BASE = "/example-libraries/";
    private static final String INDEX = BASE + "index.txt";
    private static final String JAR_PATTERN = "[A-Za-z0-9][A-Za-z0-9._-]*\\.jar";

    private ExampleLibraries() {
    }

    public static List<String> names() {
        try (InputStream input = ExampleLibraries.class.getResourceAsStream(INDEX)) {
            if (input == null) {
                throw new IllegalStateException("The plugin is missing its bundled example libraries index.");
            }
            List<String> names = new String(input.readAllBytes(), StandardCharsets.UTF_8).lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .toList();
            if (names.isEmpty() || names.stream().anyMatch(name -> !name.matches(JAR_PATTERN))) {
                throw new IllegalStateException("The bundled example libraries index is malformed.");
            }
            return names;
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read the bundled example libraries index.", exception);
        }
    }

    /**
     * Copies every bundled jar into {@code directory}, which must not already hold them.
     */
    public static List<Path> extractTo(Path directory) throws IOException {
        Files.createDirectories(directory);
        List<Path> jars = new ArrayList<>();
        for (String name : names()) {
            Path target = directory.resolve(name);
            try (InputStream input = ExampleLibraries.class.getResourceAsStream(BASE + name)) {
                if (input == null) {
                    throw new IOException("Missing bundled example library: " + name);
                }
                try (var output = Files.newOutputStream(target, StandardOpenOption.CREATE_NEW)) {
                    input.transferTo(output);
                }
            }
            jars.add(target);
        }
        return List.copyOf(jars);
    }

    public static String classpath(List<Path> jars) {
        return jars.stream().map(Path::toString).collect(Collectors.joining(File.pathSeparator));
    }
}
