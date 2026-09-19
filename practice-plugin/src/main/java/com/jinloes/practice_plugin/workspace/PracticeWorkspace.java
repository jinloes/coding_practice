package com.jinloes.practice_plugin.workspace;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public final class PracticeWorkspace {
    public static final String MARKER = ".practice-workspace";
    public static final String REVISION = "1";
    private static final List<String> BOOTSTRAP = List.of(
            "gradlew", "gradlew.bat", "gradle/wrapper/gradle-wrapper.jar",
            "gradle/wrapper/gradle-wrapper.properties");

    private PracticeWorkspace() {}

    public record Attempt(String id, String exerciseId, String revision, Path directory) {
        public Path solution() {
            return directory.resolve("src/main/java/com/jinloes/practice/Solution.java");
        }
    }

    public static boolean isWorkspace(Path root) {
        if (!Files.isRegularFile(root.resolve(MARKER))) {
            return false;
        }
        try {
            return "schema=1".equals(Files.readString(root.resolve(MARKER)).trim());
        } catch (IOException e) {
            java.util.logging.Logger.getLogger(PracticeWorkspace.class.getName())
                    .log(java.util.logging.Level.WARNING, "Cannot read practice workspace marker: " + root, e);
            return false;
        }
    }

    public static void create(Path root) throws IOException {
        if (Files.exists(root)) {
            throw new IOException("Choose a new directory. Existing directories are never overwritten.");
        }
        Files.createDirectories(root.toAbsolutePath().getParent());
        Files.createDirectory(root);
        write(root.resolve("settings.gradle"), """
                rootProject.name = 'algorithm-practice'
                file('attempts').listFiles()?.findAll {
                    it.isDirectory() && it.name ==~ /[a-z][a-z0-9-]*/
                }?.sort { it.name }?.each {
                    include ":${it.name}"
                    project(":${it.name}").projectDir = it
                }
                """);
        write(root.resolve("build.gradle"), buildTemplate());
        write(root.resolve(".gitignore"), ".gradle/\n.idea/\n**/build/\n.practice-results/\n*.iml\n");
        Files.createDirectory(root.resolve("attempts"));
        for (String name : BOOTSTRAP) {
            Path destination = root.resolve(name);
            Files.createDirectories(destination.getParent());
            try (InputStream input = PracticeWorkspace.class.getResourceAsStream("/bootstrap/" + name)) {
                if (input == null) {
                    throw new IOException("Plugin is missing Gradle bootstrap resource: " + name);
                }
                Files.copy(input, destination);
            }
        }
        if (!root.resolve("gradlew").toFile().setExecutable(true) &&
                !System.getProperty("os.name").startsWith("Windows")) {
            throw new IOException("Could not make gradlew executable: " + root.resolve("gradlew"));
        }
        write(root.resolve(MARKER), "schema=1\n");
    }

    public static Attempt createAttempt(Path root, Exercise exercise) throws IOException {
        requireWorkspace(root);
        requireAttemptsDirectory(root);
        String id = exercise.id() + "-" + UUID.randomUUID().toString().replace("-", "");
        Path directory = root.resolve("attempts").resolve(id);
        Files.createDirectory(directory);
        write(directory.resolve("src/main/java/com/jinloes/practice/Solution.java"),
                ExerciseCatalog.resource(exercise, "Solution.java"));
        for (String name : List.of("ExamplesTest.java", "CorrectnessTest.java")) {
            write(directory.resolve("src/test/java/com/jinloes/practice/" + name),
                    ExerciseCatalog.resource(exercise, name));
        }
        write(directory.resolve("build.gradle"), "// Configuration is inherited from the practice project.\n");
        write(directory.resolve(".practice-attempt"),
                "exercise=" + exercise.id() + "\nrevision=" + REVISION + "\ncreated=" + Instant.now() + "\n");
        return new Attempt(id, exercise.id(), REVISION, directory);
    }

    public static List<Attempt> attempts(Path root, String exerciseId) throws IOException {
        requireWorkspace(root);
        requireAttemptsDirectory(root);
        try (var directories = Files.list(root.resolve("attempts"))) {
            var results = new java.util.ArrayList<Attempt>();
            for (Path directory : directories.sorted().toList()) {
                if (!Files.isDirectory(directory) || !Files.isRegularFile(directory.resolve(".practice-attempt"))) {
                    continue;
                }
                Attempt attempt = readAttempt(root, directory.getFileName().toString());
                if (attempt.exerciseId().equals(exerciseId)) {
                    results.add(attempt);
                }
            }
            return List.copyOf(results);
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public static Attempt readAttempt(Path root, String id) throws IOException {
        requireWorkspace(root);
        requireAttemptsDirectory(root);
        if (!id.matches("[a-z][a-z0-9-]*")) {
            throw new IOException("Invalid attempt ID: " + id);
        }
        Path directory = root.resolve("attempts").resolve(id);
        if (!directory.toRealPath().startsWith(root.toRealPath().resolve("attempts"))) {
            throw new IOException("Attempt must remain inside the practice project.");
        }
        Properties properties = new Properties();
        try (var reader = Files.newBufferedReader(directory.resolve(".practice-attempt"))) {
            properties.load(reader);
        }
        String exercise = properties.getProperty("exercise", "");
        ExerciseCatalog.find(exercise);
        String revision = properties.getProperty("revision", "");
        if (!REVISION.equals(revision)) {
            throw new IOException("Unsupported exercise revision. Keep this attempt and start a new one.");
        }
        return new Attempt(id, exercise, revision, directory);
    }

    public static String fingerprint(Path root, Attempt attempt) throws IOException {
        Attempt stored = readAttempt(root, attempt.id());
        if (!stored.directory().toRealPath().equals(attempt.directory().toRealPath())) {
            throw new IOException("Attempt must remain inside the practice project.");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (var paths = Files.walk(attempt.directory().resolve("src"))) {
                for (Path path : paths.filter(Files::isRegularFile).sorted().toList()) {
                    digest.update(attempt.directory().relativize(path).toString().getBytes(StandardCharsets.UTF_8));
                    digest.update(Files.readAllBytes(path));
                }
            }
            for (Path path : List.of(root.resolve("settings.gradle"), root.resolve("build.gradle"),
                    attempt.directory().resolve("build.gradle"), attempt.directory().resolve(".practice-attempt"))) {
                digest.update(Files.readAllBytes(path));
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (UncheckedIOException e) {
            throw e.getCause();
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException("SHA-256 is required by the Java runtime", impossible);
        }
    }

    private static void requireWorkspace(Path root) throws IOException {
        if (!Files.isRegularFile(root.resolve(MARKER))
                || !"schema=1".equals(Files.readString(root.resolve(MARKER)).trim())) {
            throw new IOException("Open a supported plugin-created practice project first.");
        }
    }

    private static void requireAttemptsDirectory(Path root) throws IOException {
        if (!root.resolve("attempts").toRealPath().equals(root.toRealPath().resolve("attempts"))) {
            throw new IOException("The attempts directory must not redirect outside the practice project.");
        }
    }

    private static void write(Path path, String text) throws IOException {
        Files.createDirectories(path.getParent());
        Files.writeString(path, text, StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.CREATE_NEW);
    }

    private static String buildTemplate() {
        return """
                subprojects {
                    apply plugin: 'java'
                    repositories { mavenCentral() }
                    dependencies {
                        testImplementation platform('org.junit:junit-bom:5.11.4')
                        testImplementation 'org.junit.jupiter:junit-jupiter'
                        testImplementation 'org.assertj:assertj-core:3.26.3'
                        testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
                    }
                    tasks.withType(JavaCompile).configureEach {
                        options.release = 17
                        options.encoding = 'UTF-8'
                    }
                    def runId = providers.gradleProperty('practiceRun').orElse('manual').get()
                    if (!(runId ==~ /[a-zA-Z0-9-]+/)) {
                        throw new GradleException('Invalid practice run ID')
                    }
                    def resultDir = rootProject.layout.projectDirectory.dir(".practice-results/${runId}")
                    tasks.withType(JavaCompile).configureEach {
                        doFirst {
                            resultDir.asFile.mkdirs()
                            resultDir.file('phase').asFile.text = 'compiling'
                        }
                    }
                    tasks.withType(Test).configureEach {
                        useJUnitPlatform()
                        maxParallelForks = 1
                        maxHeapSize = providers.gradleProperty('practiceHeapMb').orElse('256').get() + 'm'
                        systemProperty 'junit.jupiter.execution.timeout.default',
                            providers.gradleProperty('practiceTestSeconds').orElse('5').get() + 's'
                        systemProperty 'junit.jupiter.execution.timeout.thread.mode.default', 'separate_thread'
                        reports.junitXml.outputLocation = resultDir.dir(project.name)
                        reports.junitXml.includeSystemOutLog = false
                        reports.junitXml.includeSystemErrLog = false
                        testLogging {
                            events 'failed', 'skipped'
                            exceptionFormat 'full'
                        }
                        doFirst {
                            resultDir.asFile.mkdirs()
                            resultDir.file('phase').asFile.text = 'testing'
                            resultDir.file('started').asFile.text = System.currentTimeMillis().toString()
                        }
                    }
                }
                """;
    }
}
