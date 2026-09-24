package com.jinloes.practice_plugin.catalog;

import com.jinloes.practice_plugin.run.ComplexityAnalysis;
import com.jinloes.practice_plugin.run.ComplexityReport;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExerciseCatalogTest {
    private static final List<String> TEMPLATE_NAMES =
            List.of("Solution.java", "ExamplesTest.java", "CorrectnessTest.java");

    @Test
    void exposesTheOriginalExerciseSetAndMetadata() {
        assertThat(ExerciseCatalog.all()).extracting(ExerciseCatalog.Exercise::id)
                .containsExactly(
                        "pair-sum",
                        "binary-search",
                        "balanced-delimiters",
                        "reverse-linked-list",
                        "array-stack",
                        "binary-min-heap",
                        "valid-anagram",
                        "first-unique-character",
                        "max-stock-profit",
                        "move-zeroes",
                        "merge-sorted-lists",
                        "valid-palindrome",
                        "add-strings",
                        "roman-to-integer",
                        "longest-common-prefix",
                        "missing-number",
                        "majority-element");
        assertThat(ExerciseCatalog.all()).hasSize(17);
        assertThat(ExerciseCatalog.all().stream().map(ExerciseCatalog.Exercise::title))
                .doesNotHaveDuplicates();
        for (ExerciseCatalog.Exercise exercise : ExerciseCatalog.all()) {
            assertThat(exercise.examples()).hasSize(exercise.exampleCount())
                    .allSatisfy(example -> {
                        assertThat(example.input()).isNotBlank();
                        assertThat(example.output()).isNotBlank();
                    });
            assertThat(exercise.fullCount())
                    .as("%s must hide more tests than it shows as examples", exercise.id())
                    .isGreaterThan(exercise.exampleCount());
            assertThat(exercise.hints()).hasSize(3).allSatisfy(hint -> assertThat(hint).isNotBlank());
            assertThat(exercise.inputSyntax())
                    .as("%s must document its input syntax", exercise.id())
                    .startsWith("Input: ").contains("for example: ").endsWith(exercise.sampleInput());
            assertThat(ExerciseCatalog.resource(exercise, "ExampleRunner.java"))
                    .as("%s/ExampleRunner.java usage text must match the catalog input syntax", exercise.id())
                    .contains(exercise.inputSyntax());
            assertThat(exercise.statement()).contains("Contract", "Examples");
            assertThat(ExerciseCatalog.find(exercise.id())).isSameAs(exercise);
        }
        assertThatThrownBy(() -> ExerciseCatalog.all().clear())
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> ExerciseCatalog.find("not-an-exercise"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void loadsUtf8TemplatesAndRejectsUnknownOrUnsafeResources() {
        for (ExerciseCatalog.Exercise exercise : ExerciseCatalog.all()) {
            for (String name : TEMPLATE_NAMES) {
                String source = ExerciseCatalog.resource(exercise, name);
                assertThat(source).contains("package com.jinloes.practice;");
                assertThat(source).contains(name.equals("Solution.java") ? "class Solution" : "@Test");
                assertThat(source).doesNotContain("@Timeout", "@ParameterizedTest", "@TestFactory");
                if (!name.equals("Solution.java")) {
                    assertThat(source)
                            .as("%s/%s must assert with AssertJ", exercise.id(), name)
                            .contains("import static org.assertj.core.api.Assertions.assertThat")
                            .doesNotContain("org.junit.jupiter.api.Assertions", "org.hamcrest",
                                    "throw new AssertionError");
                }
            }
            assertThat(ExerciseCatalog.resource(exercise, "Solution.java"))
                    .contains("UnsupportedOperationException(\"Implement ");
            assertThat(ExerciseCatalog.resource(exercise, "ExampleRunner.java"))
                    .as("%s/ExampleRunner.java must assert with AssertJ", exercise.id())
                    .contains("class ExampleRunner", "public static void main(String[] args)",
                            "import static org.assertj.core.api.Assertions.assertThat",
                            "if (args.length > 0) {", "private static void runInput(String input)")
                    .doesNotContain("org.junit", "org.hamcrest", "throw new AssertionError");
            assertThat(ExerciseCatalog.resource(exercise, "ExamplesTest.java"))
                    .doesNotContain("public class ExamplesTest");
            assertThat(ExerciseCatalog.resource(exercise, "CorrectnessTest.java"))
                    .doesNotContain("public class CorrectnessTest");
            assertThat(countTestMethods(ExerciseCatalog.resource(exercise, "ExamplesTest.java")))
                    .isEqualTo(exercise.exampleCount());
            assertThat(countTestMethods(ExerciseCatalog.resource(exercise, "CorrectnessTest.java"))
                    + countTestMethods(ExerciseCatalog.resource(exercise, "ExamplesTest.java")))
                    .isEqualTo(exercise.fullCount());
        }

        ExerciseCatalog.Exercise pairSum = ExerciseCatalog.find("pair-sum");
        assertThatThrownBy(() -> ExerciseCatalog.resource(pairSum, "../Solution.java"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ExerciseCatalog.resource(pairSum, "nested/Solution.java"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ExerciseCatalog.resource(pairSum, "/Solution.java"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ExerciseCatalog.resource(pairSum, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ExerciseCatalog.resource(
                new ExerciseCatalog.Exercise("unknown", "Unknown", "Test", "Easy", "Statement",
                        List.of(), List.of(), "Input: none, for example: x", "x",
                        "O(n)", "O(1)", 0, 0),
                "Solution.java"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ExerciseCatalog.resource(pairSum, "missing.txt"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void complexityProbeMeasuresEachReferenceSolutionAsItsIntendedComplexity() throws Exception {
        for (ExerciseCatalog.Exercise exercise : ExerciseCatalog.all()) {
            Path root = Files.createTempDirectory(Path.of("build"), "catalog-probe-" + exercise.id() + "-");
            try {
                Path sourceRoot = root.resolve("src/com/jinloes/practice");
                Path classes = root.resolve("classes");
                Path results = root.resolve("results");
                Files.createDirectories(sourceRoot);
                Files.createDirectories(classes);
                Files.createDirectories(results);
                Files.writeString(sourceRoot.resolve("Solution.java"), referenceSource(exercise),
                        StandardCharsets.UTF_8);
                Files.writeString(sourceRoot.resolve("Workload.java"),
                        ExerciseCatalog.resource(exercise, "Workload.java"), StandardCharsets.UTF_8);
                Files.writeString(sourceRoot.resolve("ComplexityProbe.java"),
                        ExerciseCatalog.harness("ComplexityProbe.java"), StandardCharsets.UTF_8);

                CatalogSandbox.compile(List.of(sourceRoot.resolve("Solution.java"), sourceRoot.resolve("Workload.java"),
                        sourceRoot.resolve("ComplexityProbe.java")), classes, exercise.id() + " probe");
                CatalogSandbox.launchProbe(classes.toString(), results.toString());

                ComplexityReport report = ComplexityAnalysis.read(results).orElseThrow(
                        () -> new AssertionError(exercise.id() + " probe wrote no measurement"));
                assertThat(report.samples())
                        .as("%s probe must measure several input sizes", exercise.id())
                        .hasSizeGreaterThanOrEqualTo(3);
                assertThat(report.note())
                        .as("%s probe must not fail while measuring the reference solution", exercise.id())
                        .doesNotContain("Error", "Exception");
                assertThat(report.timeClass())
                        .as("%s reference solution measured as %s, intended %s", exercise.id(),
                                report.timeClass(), exercise.intendedTime())
                        .matches(measured -> measured.equals(ComplexityReport.INCONCLUSIVE)
                                || measured.contains(exercise.intendedTime()));
                assertThat(report.spaceClass())
                        .as("%s reference solution allocated as %s, intended %s", exercise.id(),
                                report.spaceClass(), exercise.intendedSpace())
                        .matches(measured -> measured.equals(ComplexityReport.INCONCLUSIVE)
                                || measured.contains(exercise.intendedSpace()));
            } finally {
                CatalogSandbox.deleteTree(root);
            }
        }
    }

    @Test
    void referenceSolutionsPassTheirCompleteSuites() throws Exception {
        for (ExerciseCatalog.Exercise exercise : ExerciseCatalog.all()) {
            CatalogSandbox.RunSummary summary = CatalogSandbox.compileAndRun(exercise, referenceSource(exercise), "reference");
            assertThat(summary.failures())
                    .as("reference solution for %s: %s", exercise.id(), summary.failureDetails())
                    .isZero();
            assertThat(summary.testsFound())
                    .as("reference tests found for %s", exercise.id())
                    .isEqualTo(exercise.fullCount());
        }
    }

    @Test
    void starterAndKnownWrongSolutionsAreCaughtByTheSuites() throws Exception {
        for (ExerciseCatalog.Exercise exercise : ExerciseCatalog.all()) {
            CatalogSandbox.RunSummary starter = CatalogSandbox.compileAndRun(
                    exercise,
                    ExerciseCatalog.resource(exercise, "Solution.java"),
                    "starter");
            assertThat(starter.failures())
                    .as("starter should fail for %s", exercise.id())
                    .isGreaterThan(0);

            CatalogSandbox.RunSummary wrong = CatalogSandbox.compileAndRun(exercise, KnownWrongSolutions.source(exercise), "known-wrong");
            assertThat(wrong.failures())
                    .as("known wrong solution should fail for %s", exercise.id())
                    .isGreaterThan(0);
        }
    }

    @Test
    void startersContainOnlyLearnerApiAndGeneratedRunnersPassVisibleExamples() throws Exception {
        for (ExerciseCatalog.Exercise exercise : ExerciseCatalog.all()) {
            Path root = Files.createTempDirectory(Path.of("build"), "catalog-runner-" + exercise.id() + "-");
            try {
                Path sourceRoot = root.resolve("src/com/jinloes/practice");
                Path classes = root.resolve("classes");
                Files.createDirectories(sourceRoot);
                Files.createDirectories(classes);
                String starter = ExerciseCatalog.resource(exercise, "Solution.java");
                assertThat(starter).doesNotContain(
                        "static void main", "@Test", "assertEquals", "assertPair", "assertValues");
                Files.writeString(sourceRoot.resolve("Solution.java"), referenceSource(exercise), StandardCharsets.UTF_8);
                Files.writeString(sourceRoot.resolve("ExampleRunner.java"),
                        ExerciseCatalog.resource(exercise, "ExampleRunner.java"), StandardCharsets.UTF_8);

                CatalogSandbox.compile(List.of(sourceRoot.resolve("Solution.java"), sourceRoot.resolve("ExampleRunner.java")),
                        classes, exercise.id() + " generated runner");

                List<Path> libraries = ExampleLibraries.extractTo(root.resolve("libs"));
                String runtimeClasspath = classes + File.pathSeparator + ExampleLibraries.classpath(libraries);
                assertThat(CatalogSandbox.launchRunner(runtimeClasspath))
                        .as("%s runner without arguments must run the visible examples", exercise.id())
                        .contains("Examples passed: " + exercise.exampleCount());

                String custom = CatalogSandbox.launchRunner(runtimeClasspath, exercise.sampleInput());
                assertThat(custom)
                        .as("%s runner must accept its documented sample input", exercise.id())
                        .isNotBlank()
                        .doesNotContain("Examples passed:", "Exception in thread", exercise.inputSyntax());

                assertThat(CatalogSandbox.launchRunner(runtimeClasspath, "definitely not valid input"))
                        .as("%s runner must explain its input syntax when the input is unusable", exercise.id())
                        .contains(exercise.inputSyntax());
            } finally {
                CatalogSandbox.deleteTree(root);
            }
        }
    }

    private static String referenceSource(ExerciseCatalog.Exercise exercise) throws IOException {
        String path = "/reference/" + exercise.id() + "/Solution.java";
        try (var input = ExerciseCatalogTest.class.getResourceAsStream(path)) {
            if (input == null) {
                throw new IOException("Missing reference source: " + path);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static int countTestMethods(String source) {
        return (int) source.lines()
                .map(String::trim)
                .filter("@Test"::equals)
                .count();
    }
}
