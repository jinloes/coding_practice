package com.jinloes.practice_plugin.workspace;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Read-only reader for the legacy on-disk practice workspace layout.
 *
 * <p>The plugin no longer creates these workspaces; {@code ManagedPracticeWorkspace} owns all new
 * attempts. This reader stays so learners can still import workspaces written by previously shipped
 * plugin versions, and the format it parses must not change. Tests write the legacy layout through
 * {@code LegacyWorkspaceFixture}.
 */
public final class PracticeWorkspace {
    public static final String MARKER = ".practice-workspace";
    public static final String REVISION = "1";

    private PracticeWorkspace() {}

    public record Attempt(String id, String exerciseId, String revision, Path directory) {
        public Path solution() {
            return directory.resolve(ManagedPracticeWorkspace.SOLUTION_PATH);
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
}
