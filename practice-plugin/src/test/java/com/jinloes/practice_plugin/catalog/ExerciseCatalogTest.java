package com.jinloes.practice_plugin.catalog;

import com.jinloes.practice_plugin.run.ComplexityAnalysis;
import com.jinloes.practice_plugin.run.ComplexityReport;
import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

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
                        "merge-sorted-lists");
        assertThat(ExerciseCatalog.all()).hasSize(11);
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

                compile(List.of(sourceRoot.resolve("Solution.java"), sourceRoot.resolve("Workload.java"),
                        sourceRoot.resolve("ComplexityProbe.java")), classes, exercise.id() + " probe");
                launchProbe(classes.toString(), results.toString());

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
                deleteTree(root);
            }
        }
    }

    @Test
    void referenceSolutionsPassTheirCompleteSuites() throws Exception {
        for (ExerciseCatalog.Exercise exercise : ExerciseCatalog.all()) {
            RunSummary summary = compileAndRun(exercise, referenceSource(exercise), "reference");
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
            RunSummary starter = compileAndRun(
                    exercise,
                    ExerciseCatalog.resource(exercise, "Solution.java"),
                    "starter");
            assertThat(starter.failures())
                    .as("starter should fail for %s", exercise.id())
                    .isGreaterThan(0);

            RunSummary wrong = compileAndRun(exercise, wrongSource(exercise), "known-wrong");
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

                compile(List.of(sourceRoot.resolve("Solution.java"), sourceRoot.resolve("ExampleRunner.java")),
                        classes, exercise.id() + " generated runner");

                List<Path> libraries = ExampleLibraries.extractTo(root.resolve("libs"));
                String runtimeClasspath = classes + File.pathSeparator + ExampleLibraries.classpath(libraries);
                assertThat(launchRunner(runtimeClasspath))
                        .as("%s runner without arguments must run the visible examples", exercise.id())
                        .contains("Examples passed: " + exercise.exampleCount());

                String custom = launchRunner(runtimeClasspath, exercise.sampleInput());
                assertThat(custom)
                        .as("%s runner must accept its documented sample input", exercise.id())
                        .isNotBlank()
                        .doesNotContain("Examples passed:", "Exception in thread", exercise.inputSyntax());

                assertThat(launchRunner(runtimeClasspath, "definitely not valid input"))
                        .as("%s runner must explain its input syntax when the input is unusable", exercise.id())
                        .contains(exercise.inputSyntax());
            } finally {
                deleteTree(root);
            }
        }
    }

    private static String launchRunner(String classpath, String... arguments) throws Exception {
        List<String> command = new ArrayList<>(List.of(
                Path.of(System.getProperty("java.home"), "bin",
                        System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java").toString(),
                "-classpath", classpath,
                "com.jinloes.practice.ExampleRunner"));
        command.addAll(List.of(arguments));
        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        try {
            assertThat(process.waitFor(30, java.util.concurrent.TimeUnit.SECONDS))
                    .as("example runner finished for arguments %s", List.of(arguments)).isTrue();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertThat(process.exitValue())
                    .as("example runner exit code for arguments %s: %s", List.of(arguments), output)
                    .isZero();
            return output;
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
                process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
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

    private static RunSummary compileAndRun(
            ExerciseCatalog.Exercise exercise,
            String solution,
            String label
    ) throws Exception {
        Files.createDirectories(Path.of("build"));
        Path root = Files.createTempDirectory(Path.of("build"),
                "catalog-" + exercise.id() + "-" + label + "-");
        try {
            Path sourceRoot = root.resolve("src/com/jinloes/practice");
            Path classes = root.resolve("classes");
            Files.createDirectories(sourceRoot);
            Files.createDirectories(classes);
            Files.writeString(sourceRoot.resolve("Solution.java"), solution, StandardCharsets.UTF_8);
            Files.writeString(
                    sourceRoot.resolve("ExamplesTest.java"),
                    ExerciseCatalog.resource(exercise, "ExamplesTest.java"),
                    StandardCharsets.UTF_8);
            Files.writeString(
                    sourceRoot.resolve("CorrectnessTest.java"),
                    ExerciseCatalog.resource(exercise, "CorrectnessTest.java"),
                    StandardCharsets.UTF_8);
            compile(List.of(
                    sourceRoot.resolve("Solution.java"),
                    sourceRoot.resolve("ExamplesTest.java"),
                    sourceRoot.resolve("CorrectnessTest.java")), classes, exercise.id() + " " + label);
            return execute(classes);
        } finally {
            deleteTree(root);
        }
    }

    private static void launchProbe(String classpath, String results) throws Exception {
        Process process = new ProcessBuilder(
                Path.of(System.getProperty("java.home"), "bin",
                        System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java").toString(),
                "-classpath", classpath,
                "com.jinloes.practice.ComplexityProbe", results)
                .redirectErrorStream(true).start();
        try {
            assertThat(process.waitFor(90, java.util.concurrent.TimeUnit.SECONDS))
                    .as("complexity probe finished").isTrue();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertThat(process.exitValue()).as("complexity probe exit code: %s", output).isZero();
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
                process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
            }
        }
    }

    private static void compile(List<Path> sources, Path classes, String description) throws Exception {
        String classpath = System.getProperty(
                "practice.test.classpath",
                System.getProperty("java.class.path"));
        List<String> arguments = new ArrayList<>(List.of(
                Path.of(System.getProperty("java.home"), "bin",
                        System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java").toString(),
                "--module", "jdk.compiler/com.sun.tools.javac.Main",
                "--release", "17",
                "-encoding", "UTF-8",
                "-classpath", classpath,
                "-d", classes.toString()));
        arguments.addAll(sources.stream().map(Path::toString).toList());
        Path diagnostics = classes.getParent().resolve("compiler.log");
        Process process = new ProcessBuilder(arguments).redirectErrorStream(true)
                .redirectOutput(diagnostics.toFile()).start();
        try {
            assertThat(process.waitFor(30, java.util.concurrent.TimeUnit.SECONDS))
                    .as("compiler finished for %s", description).isTrue();
            assertThat(process.exitValue())
                    .as("compile %s: %s", description, Files.readString(diagnostics))
                    .isZero();
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
                process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
            }
        }
    }

    private static RunSummary execute(Path classes) throws Exception {
        URL[] classpath = {classes.toUri().toURL()};
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try (URLClassLoader loader = new URLClassLoader(classpath, previous)) {
            Thread.currentThread().setContextClassLoader(loader);
            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(selectClass("com.jinloes.practice.ExamplesTest"),
                            selectClass("com.jinloes.practice.CorrectnessTest"))
                    .build();
            Launcher launcher = LauncherFactory.create();
            SummaryGeneratingListener listener = new SummaryGeneratingListener();
            launcher.registerTestExecutionListeners(listener);
            launcher.execute(request);
            var summary = listener.getSummary();
            String details = summary.getFailures().stream()
                    .map(failure -> failure.getTestIdentifier().getDisplayName()
                            + ": " + failure.getException().getMessage())
                    .collect(Collectors.joining("; "));
            return new RunSummary(summary.getTestsFoundCount(), summary.getTestsFailedCount(), details);
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    private static void deleteTree(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(root)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }

    private static String wrongSource(ExerciseCatalog.Exercise exercise) {
        return switch (exercise.id()) {
            case "pair-sum" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static int[] findPair(int[] numbers, int target) {
                            return numbers.length >= 2 ? new int[]{0, 1} : new int[0];
                        }
                    }
                    """;
            case "binary-search" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static int search(int[] sorted, int target) {
                            return sorted.length == 0 ? -1 : 0;
                        }
                    }
                    """;
            case "balanced-delimiters" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static boolean isBalanced(String input) {
                            return input.length() % 2 == 0;
                        }
                    }
                    """;
            case "reverse-linked-list" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static class Node {
                            public int value;
                            public Node next;
                            public Node(int value, Node next) {
                                this.value = value;
                                this.next = next;
                            }
                        }
                        public static Node reverse(Node head) {
                            return head;
                        }
                    }
                    """;
            case "array-stack" -> """
                    package com.jinloes.practice;
                    import java.util.ArrayDeque;
                    public class Solution {
                        private final ArrayDeque<Integer> values = new ArrayDeque<>();
                        public void push(int value) { values.addLast(value); }
                        public int pop() { return values.removeFirst(); }
                        public int peek() { return values.getFirst(); }
                        public int size() { return values.size(); }
                        public boolean isEmpty() { return values.isEmpty(); }
                    }
                    """;
            case "binary-min-heap" -> """
                    package com.jinloes.practice;
                    import java.util.ArrayDeque;
                    public class Solution {
                        private final ArrayDeque<Integer> values = new ArrayDeque<>();
                        public void add(int value) { values.addLast(value); }
                        public int removeMin() { return values.removeFirst(); }
                        public int peek() { return values.getFirst(); }
                        public int size() { return values.size(); }
                        public boolean isEmpty() { return values.isEmpty(); }
                    }
                    """;
            case "valid-anagram" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static boolean isAnagram(String first, String second) {
                            return first.length() == second.length();
                        }
                    }
                    """;
            case "first-unique-character" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static int firstUniqueIndex(String word) {
                            return word.isEmpty() ? -1 : 0;
                        }
                    }
                    """;
            case "max-stock-profit" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static int maxProfit(int[] prices) {
                            return prices.length < 2 ? 0 : prices[prices.length - 1] - prices[0];
                        }
                    }
                    """;
            case "move-zeroes" -> """
                    package com.jinloes.practice;
                    import java.util.Arrays;
                    public class Solution {
                        public static void moveZeroes(int[] numbers) {
                            Arrays.sort(numbers);
                        }
                    }
                    """;
            case "merge-sorted-lists" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static class Node {
                            public int value;
                            public Node next;
                            public Node(int value, Node next) {
                                this.value = value;
                                this.next = next;
                            }
                        }
                        public static Node merge(Node first, Node second) {
                            return first != null ? first : second;
                        }
                    }
                    """;
            default -> throw new IllegalArgumentException("No known wrong source for " + exercise.id());
        };
    }

    private record RunSummary(long testsFound, long failures, String failureDetails) {
    }
}
