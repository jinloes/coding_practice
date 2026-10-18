package com.jinloes.practice_plugin.catalog;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Example;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.catalog.GuideCatalog.Guide;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CatalogErrorPathsTest {
    @TempDir Path temporary;

    @Test
    void exerciseValueObjectsRejectInvalidRequiredFieldsAndCounts() {
        assertThat(new Exercise("demo", "Demo", "Topic", "Easy", "Statement", List.of(example()), List.of("hint"),
                "Input: x", "x", "O(1)", "O(1)", 1, 2).fullCount()).isEqualTo(2);

        assertInvalidExercise("input syntax",
                () -> exerciseWith(" ", "x", "O(1)", "O(1)", List.of(example()), 1, 1));
        assertInvalidExercise("sample",
                () -> exerciseWith("Input: x", " ", "O(1)", "O(1)", List.of(example()), 1, 1));
        assertInvalidExercise("intended time",
                () -> exerciseWith("Input: x", "x", " ", "O(1)", List.of(example()), 1, 1));
        assertInvalidExercise("intended space",
                () -> exerciseWith("Input: x", "x", "O(1)", " ", List.of(example()), 1, 1));
        assertInvalidExercise("negative examples",
                () -> exerciseWith("Input: x", "x", "O(1)", "O(1)", List.of(), -1, 0));
        assertInvalidExercise("full count before examples",
                () -> exerciseWith("Input: x", "x", "O(1)", "O(1)", List.of(example()), 1, 0));
        assertInvalidExercise("example size mismatch",
                () -> exerciseWith("Input: x", "x", "O(1)", "O(1)", List.of(), 1, 1));

        assertThatThrownBy(() -> new Example(" ", "out"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Example input");
        assertThatThrownBy(() -> new Example("in", " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Example input");
    }

    @Test
    void exerciseCatalogRejectsMalformedIndexesResourcesAndManifests() {
        assertThat(ExerciseCatalog.indexedIds(" pair-sum \n\nbinary-search\n"))
                .containsExactly("pair-sum", "binary-search");
        assertThatThrownBy(() -> ExerciseCatalog.indexedIds("Bad_ID\n"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid exercise ID");
        assertThatThrownBy(() -> ExerciseCatalog.indexedIds("pair-sum\npair-sum\n"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicate exercise ID");
        assertThatThrownBy(() -> ExerciseCatalog.harness("Other.java"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown harness");
        assertThatThrownBy(() -> ExerciseCatalog.resource(ExerciseCatalog.find("pair-sum"), ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsafe resource filename");
        assertThatThrownBy(() -> ExerciseCatalog.resource(ExerciseCatalog.find("pair-sum"), "bad..name"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsafe resource filename");
        assertThatThrownBy(() -> ExerciseCatalog.read("/missing.txt", path -> null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("/missing.txt");
        assertThatThrownBy(() -> ExerciseCatalog.read("/broken.txt", path -> throwingInput()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Could not read exercise resource");

        assertInvalidManifest("not json", "Malformed exercise manifest");
        assertInvalidManifest(manifest("other", examples(), hints()), "does not match");
        assertInvalidManifest(manifest("demo", examples(), hints()).replace("Input: x", "Input: y"),
                "usage text does not show");
        assertInvalidManifest("{\"id\":\"demo\"}", "missing 'inputSyntax'");
        assertInvalidManifest("{\"id\":\"demo\",\"inputSyntax\":\"Input: x\"}", "missing 'title'");
        assertInvalidManifest(manifestWith("\"title\":1"), "member 'title' is not a string");
        assertInvalidManifest(manifestWith("\"title\":{}"), "member 'title' is not a string");
        assertInvalidManifest(manifest("demo", examples(), hints()).replace("\"examples\":[" + examples() + "]",
                "\"examples\":\"bad\""), "member 'examples' is not an array");
        assertInvalidManifest(manifest("demo", "1", hints()), "must hold objects");
        assertInvalidManifest(manifest("demo", examples(), "1"), "member 'hints' is not a string");
        assertInvalidManifest(manifest("demo", examples(), hints()), "No test methods",
                "class ExamplesTest {}", "@Test void full() {}");
    }

    @Test
    void testAnnotationCounterIgnoresCommentsAndLiteralsAndRejectsAmbiguousTemplates() {
        String source = """
                // @Test
                class Demo {
                    String literal = "@Test";
                    char quote = '\\'';
                    /* @Test */
                    @Test void real() {}
                }
                """;
        assertThat(ExerciseCatalog.countTests(source, "Demo.java")).isEqualTo(1);
        assertThat(ExerciseCatalog.countTests("@Test void real() {} String escaped = \"\\\\\\\"\";", "Demo.java"))
                .isEqualTo(1);
        assertThatThrownBy(() -> ExerciseCatalog.countTests("/* @Test", "Demo.java"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Unterminated block comment");
        assertThatThrownBy(() -> ExerciseCatalog.countTests("// @Test", "Demo.java"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No test methods");
        assertThatThrownBy(() -> ExerciseCatalog.countTests("/", "Demo.java"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No test methods");
        assertThatThrownBy(() -> ExerciseCatalog.countTests("\"\"\" @Test \"\"\"", "Demo.java"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Text blocks");
        assertThat(ExerciseCatalog.countTests("@Test void real() {} int half = total / 2;", "Demo.java"))
                .isEqualTo(1);
        assertThatThrownBy(() -> ExerciseCatalog.countTests("\"unterminated\n@Test void real() {}", "Demo.java"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Unterminated literal");
        assertThatThrownBy(() -> ExerciseCatalog.countTests("\"", "Demo.java"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Unterminated literal");
    }

    @Test
    void exampleLibrariesValidateIndexAndBundledJars() throws IOException {
        assertThat(ExampleLibraries.names()).isNotEmpty();
        assertThat(ExampleLibraries.names(" assertj-core.jar \n\nhelper-1.0.jar\n"))
                .containsExactly("assertj-core.jar", "helper-1.0.jar");
        assertThatThrownBy(() -> ExampleLibraries.names(" \n"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("malformed");
        assertThatThrownBy(() -> ExampleLibraries.names("../bad.jar\n"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("malformed");
        assertThatThrownBy(() -> ExampleLibraries.names(path -> null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("missing");
        assertThatThrownBy(() -> ExampleLibraries.names(path -> throwingInput()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot read");

        Path target = temporary.resolve("libs");
        assertThat(ExampleLibraries.extractTo(target, path -> switch (path) {
            case "/example-libraries/index.txt" -> stream("one.jar\n");
            case "/example-libraries/one.jar" -> stream("jar");
            default -> null;
        })).singleElement().satisfies(path -> assertThat(path).hasContent("jar"));
        assertThatThrownBy(() -> ExampleLibraries.extractTo(temporary.resolve("missing"), path -> switch (path) {
            case "/example-libraries/index.txt" -> stream("one.jar\n");
            default -> null;
        })).isInstanceOf(IOException.class)
                .hasMessageContaining("Missing bundled example library: one.jar");
    }

    @Test
    void guideAndTrackResourceReadersReportMissingAndUnreadableResources() {
        assertThatThrownBy(() -> GuideCatalog.read("/guides/missing.json", path -> null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Missing guide resource");
        assertThatThrownBy(() -> GuideCatalog.read("/guides/broken.json", path -> throwingInput()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Could not read guide resource");
        assertThatThrownBy(() -> TrackCatalog.read("/tracks/missing.json", path -> null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Missing track resource");
        assertThatThrownBy(() -> TrackCatalog.read("/tracks/broken.json", path -> throwingInput()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Could not read track resource");
    }

    @Test
    void guideParserRejectsNonArrayNonStringAndNonPrimitiveMembers() {
        Function<String, Exercise> lookup = lookup(Map.of("a", fake("a", "Easy")));

        assertThatThrownBy(() -> GuideCatalog.parse("demo",
                "{\"id\":\"demo\",\"title\":\"Demo\",\"summary\":\"s\",\"lesson\":\"l\",\"exercises\":\"a\"}",
                lookup))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least one exercise");
        assertThatThrownBy(() -> GuideCatalog.parse("demo",
                "{\"id\":\"demo\",\"title\":\"Demo\",\"summary\":\"s\",\"lesson\":\"l\",\"exercises\":[{}]}",
                lookup))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("exercise ID strings");
        assertThatThrownBy(() -> GuideCatalog.parse("demo",
                "{\"id\":\"demo\",\"title\":{},\"summary\":\"s\",\"lesson\":\"l\",\"exercises\":[\"a\"]}",
                lookup))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("non-blank 'title'");
        assertThatThrownBy(() -> GuideCatalog.parse("demo",
                "{\"id\":\"demo\",\"title\":1,\"summary\":\"s\",\"lesson\":\"l\",\"exercises\":[\"a\"]}",
                lookup))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("non-blank 'title'");
        Guide guide = GuideCatalog.parse("demo",
                "{\"id\":\"demo\",\"title\":\"Demo\",\"summary\":\"s\",\"lesson\":\"l\",\"exercises\":[\"a\"]}",
                lookup);
        assertThat(guide.exercises()).extracting(Exercise::id).containsExactly("a");
    }

    private static void assertInvalidExercise(String description, ThrowingRunnable runnable) {
        assertThatThrownBy(runnable::run)
                .as(description)
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Exercise exerciseWith(
            String inputSyntax,
            String sampleInput,
            String intendedTime,
            String intendedSpace,
            List<Example> examples,
            int exampleCount,
            int fullCount
    ) {
        return new Exercise("demo", "Demo", "Topic", "Easy", "Statement", examples, List.of("hint"),
                inputSyntax, sampleInput, intendedTime, intendedSpace, exampleCount, fullCount);
    }

    private static void assertInvalidManifest(String json, String message) {
        assertInvalidManifest(json, message, "@Test void example() {}", "@Test void full() {}");
    }

    private static void assertInvalidManifest(String json, String message, String examplesTest, String correctnessTest) {
        assertThatThrownBy(() -> ExerciseCatalog.loadExercise(
                "demo", json, "Usage: Input: x", examplesTest, correctnessTest))
                .as(json)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(message);
    }

    private static String manifestWith(String replacement) {
        return manifest("demo", examples(), hints())
                .replace("\"title\":\"Demo\"", replacement);
    }

    private static String manifest(String id, String examples, String hints) {
        return "{\"id\":\"" + id + "\",\"title\":\"Demo\",\"topic\":\"Topic\",\"difficulty\":\"Easy\","
                + "\"statement\":\"Contract\\nExamples\",\"examples\":[" + examples + "],"
                + "\"hints\":[" + hints + "],\"inputSyntax\":\"Input: x\",\"sampleInput\":\"x\","
                + "\"intendedTime\":\"O(1)\",\"intendedSpace\":\"O(1)\"}";
    }

    private static String examples() {
        return "{\"input\":\"x\",\"output\":\"y\"}";
    }

    private static String hints() {
        return "\"hint\"";
    }

    private static Example example() {
        return new Example("in", "out");
    }

    private static Function<String, Exercise> lookup(Map<String, Exercise> exercises) {
        return id -> {
            Exercise exercise = exercises.get(id);
            if (exercise == null) {
                throw new IllegalArgumentException("Unknown exercise ID: " + id);
            }
            return exercise;
        };
    }

    private static Exercise fake(String id, String difficulty) {
        return new Exercise(id, id.toUpperCase(), "Topic", difficulty, "Statement", List.of(), List.of(),
                "Input: x", "x", "O(1)", "O(1)", 0, 0);
    }

    private static InputStream stream(String value) {
        return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    }

    private static InputStream throwingInput() {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("boom");
            }
        };
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
