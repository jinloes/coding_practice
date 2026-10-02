package com.jinloes.practice_plugin.catalog;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * One compile-compatible but incorrect solution per catalog exercise, stored at
 * {@code src/test/resources/known-wrong/<id>/Solution.java}. Each must fail at least one test in its
 * exercise's suites; add one whenever an exercise is added to the catalog.
 */
final class KnownWrongSolutions {
    private KnownWrongSolutions() {
    }

    static String source(ExerciseCatalog.Exercise exercise) {
        String path = "/known-wrong/" + exercise.id() + "/Solution.java";
        try (InputStream input = KnownWrongSolutions.class.getResourceAsStream(path)) {
            if (input == null) {
                throw new IllegalArgumentException("No known wrong source for " + exercise.id() + " at " + path);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not read " + path, exception);
        }
    }
}
