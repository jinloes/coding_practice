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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Bundled assertion and test libraries that generated example and verification runners compile and
 * execute against offline.
 */
public final class ExampleLibraries {
    private static final String EXAMPLE_BASE = "/example-libraries/";
    private static final String VERIFICATION_BASE = "/verification-libraries/";
    private static final String JAR_PATTERN = "[A-Za-z0-9][A-Za-z0-9._-]*\\.jar";

    private ExampleLibraries() {
    }

    public static List<String> names() {
        return names(EXAMPLE_BASE, "example", ExampleLibraries.class::getResourceAsStream);
    }

    public static List<String> verificationNames() {
        return names(VERIFICATION_BASE, "verification", ExampleLibraries.class::getResourceAsStream);
    }

    static List<String> names(Function<String, InputStream> opener) {
        return names(EXAMPLE_BASE, "example", opener);
    }

    static List<String> names(String base, String kind, Function<String, InputStream> opener) {
        try (InputStream input = opener.apply(base + "index.txt")) {
            if (input == null) {
                throw new IllegalStateException("The plugin is missing its bundled " + kind + " libraries index.");
            }
            return names(new String(input.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read the bundled " + kind + " libraries index.", exception);
        }
    }

    static List<String> names(String index) {
        List<String> names = index.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();
        if (names.isEmpty() || names.stream().anyMatch(name -> !name.matches(JAR_PATTERN))) {
            throw new IllegalStateException("The bundled example libraries index is malformed.");
        }
        return names;
    }

    /**
     * Copies every bundled example jar into {@code directory}, which must not already hold them.
     */
    public static List<Path> extractTo(Path directory) throws IOException {
        return extractTo(directory, ExampleLibraries.class::getResourceAsStream);
    }

    static List<Path> extractTo(Path directory, Function<String, InputStream> opener) throws IOException {
        return extractGroup(EXAMPLE_BASE, "example", directory, opener);
    }

    /**
     * Copies every bundled example and verification jar into {@code directory} for an offline full
     * check.
     */
    public static List<Path> extractVerificationTo(Path directory) throws IOException {
        return extractVerificationTo(directory, ExampleLibraries.class::getResourceAsStream);
    }

    public static List<Path> extractVerificationTo(
            Path directory,
            Function<String, InputStream> opener
    ) throws IOException {
        List<Path> jars = new ArrayList<>(extractGroup(EXAMPLE_BASE, "example", directory, opener));
        jars.addAll(extractGroup(VERIFICATION_BASE, "verification", directory, opener));
        return List.copyOf(jars);
    }

    private static List<Path> extractGroup(
            String base,
            String kind,
            Path directory,
            Function<String, InputStream> opener
    ) throws IOException {
        Files.createDirectories(directory);
        List<Path> jars = new ArrayList<>();
        for (String name : names(base, kind, opener)) {
            Path target = directory.resolve(name);
            try (InputStream input = opener.apply(base + name)) {
                if (input == null) {
                    throw new IOException("Missing bundled " + kind + " library: " + name);
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
