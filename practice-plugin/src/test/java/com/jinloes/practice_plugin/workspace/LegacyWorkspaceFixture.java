package com.jinloes.practice_plugin.workspace;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Writes the legacy on-disk practice workspace layout that {@link PracticeWorkspace} still reads.
 *
 * <p>The production writer was removed once it had no production caller; legacy support is
 * read-only. This fixture is a faithful port of that writer so tests keep exercising the reader
 * against the exact bytes previously shipped plugin versions produced. Any change here changes what
 * the reader is tested against, so it must stay byte-compatible with the legacy format.
 */
public final class LegacyWorkspaceFixture {
    private static final List<String> BOOTSTRAP = List.of(
            "gradlew", "gradlew.bat", "gradle/wrapper/gradle-wrapper.jar",
            "gradle/wrapper/gradle-wrapper.properties");

    private LegacyWorkspaceFixture() {}

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
            try (InputStream input = LegacyWorkspaceFixture.class.getResourceAsStream("/bootstrap/" + name)) {
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
        write(root.resolve(PracticeWorkspace.MARKER), "schema=1\n");
    }

    public static PracticeWorkspace.Attempt createAttempt(Path root, Exercise exercise) throws IOException {
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
                "exercise=" + exercise.id() + "\nrevision=" + PracticeWorkspace.REVISION
                        + "\ncreated=" + Instant.now() + "\n");
        return new PracticeWorkspace.Attempt(id, exercise.id(), PracticeWorkspace.REVISION, directory);
    }

    private static void requireWorkspace(Path root) throws IOException {
        if (!Files.isRegularFile(root.resolve(PracticeWorkspace.MARKER))
                || !"schema=1".equals(Files.readString(root.resolve(PracticeWorkspace.MARKER)).trim())) {
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
        Files.writeString(path, text, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
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
