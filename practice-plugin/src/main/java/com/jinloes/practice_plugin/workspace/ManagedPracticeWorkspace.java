package com.jinloes.practice_plugin.workspace;

import com.intellij.openapi.application.PathManager;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.run.CheckResult;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.state.PracticeProgress;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

public final class ManagedPracticeWorkspace {
    public static final String REVISION = "1";
    private static final Path SOLUTION_SUFFIX =
            Path.of("src/main/java/com/jinloes/practice/Solution.java");
    private final Path configRoot;

    public record Attempt(String id, String exerciseId, String revision, Path solution) {
        public Path directory() {
            Path directory = solution;
            for (int index = 0; index < SOLUTION_SUFFIX.getNameCount(); index++) {
                directory = directory.getParent();
            }
            return directory;
        }
    }

    public record LegacyImportResult(int imported, int skipped, String summary) {}

    public ManagedPracticeWorkspace() {
        this(Path.of(PathManager.getConfigPath()));
    }

    ManagedPracticeWorkspace(Path configRoot) {
        this.configRoot = configRoot.toAbsolutePath().normalize();
    }

    public Attempt create(Exercise exercise) throws IOException {
        for (int index = 0; index < 100; index++) {
            String id = exercise.id() + "-" + UUID.randomUUID().toString().replace("-", "");
            Path solution = solutionPath(exercise.id(), id);
            if (Files.exists(solution, LinkOption.NOFOLLOW_LINKS)) {
                continue;
            }
            Files.createDirectories(solution.getParent());
            Path temporary = Files.createTempFile(solution.getParent(), ".solution-", ".tmp");
            try {
                Files.writeString(temporary, ExerciseCatalog.resource(exercise, "Solution.java"),
                        StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
                Files.move(temporary, solution, StandardCopyOption.ATOMIC_MOVE);
            } finally {
                Files.deleteIfExists(temporary);
            }
            return read(id);
        }
        throw new IOException("Could not create a unique managed attempt. Try again.");
    }

    public Attempt copyLegacy(Path legacyRoot, PracticeWorkspace.Attempt legacy) throws IOException {
        Path root = legacyRoot.toRealPath();
        Path solution = legacy.solution().toRealPath();
        if (!PracticeWorkspace.isWorkspace(root) || !solution.startsWith(root)
                || !Files.isRegularFile(solution, LinkOption.NOFOLLOW_LINKS)
                || Files.isSymbolicLink(legacy.solution())) {
            throw new IOException("The legacy solution must be a regular file inside its marked practice project.");
        }
        Attempt copy = create(ExerciseCatalog.find(legacy.exerciseId()));
        Path temporary = Files.createTempFile(copy.solution().getParent(), ".legacy-", ".tmp");
        try {
            Files.copy(solution, temporary, StandardCopyOption.REPLACE_EXISTING);
            Files.move(temporary, copy.solution(), StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporary);
        }
        return copy;
    }

    public LegacyImportResult importLegacy(
            Path legacyRoot,
            PracticeProgress legacyProgress,
            ManagedPracticeProgress progress
    ) throws IOException {
        if (Files.isSymbolicLink(legacyRoot)) {
            throw new IOException("The legacy practice root must not redirect.");
        }
        Path root = legacyRoot.toRealPath();
        if (!PracticeWorkspace.isWorkspace(root)) {
            throw new IOException("The current project is not a marked legacy practice project.");
        }
        Path attemptsRoot = root.resolve("attempts");
        if (!Files.isDirectory(attemptsRoot, LinkOption.NOFOLLOW_LINKS)
                || Files.isSymbolicLink(attemptsRoot)
                || !attemptsRoot.toRealPath().equals(root.resolve("attempts"))) {
            throw new IOException("The legacy attempts directory must not redirect.");
        }

        int imported = 0;
        int skipped = 0;
        try (var entries = Files.list(attemptsRoot)) {
            for (Path entry : entries.sorted(Comparator.comparing(Path::toString)).toList()) {
                if (!Files.isDirectory(entry, LinkOption.NOFOLLOW_LINKS)
                        || Files.isSymbolicLink(entry)
                        || !Files.isRegularFile(entry.resolve(".practice-attempt"), LinkOption.NOFOLLOW_LINKS)
                        || Files.isSymbolicLink(entry.resolve(".practice-attempt"))) {
                    skipped++;
                    continue;
                }
                try {
                    PracticeWorkspace.Attempt legacy =
                            PracticeWorkspace.readAttempt(root, entry.getFileName().toString());
                    String mapped = progress.importedAttempt(root.toString(), legacy.id());
                    if (mapped != null) {
                        skipped++;
                        continue;
                    }
                    Attempt copy = copyLegacy(root, legacy);
                    progress.recordImport(root.toString(), legacy.id(), copy.id());
                    PracticeProgress.Entry old = legacyProgress.entry(legacy.id(), legacy.exerciseId());
                    ManagedPracticeProgress.Entry migrated = progress.entry(copy.id(), legacy.exerciseId());
                    migrated.status = CheckResult.Status.NOT_RUN.name();
                    migrated.checkedFingerprint = "";
                    migrated.checkedAt = "";
                    migrated.lastPassedAt = old.lastPassedAt;
                    migrated.details = "Imported from legacy attempt " + legacy.id()
                            + "; run a new full check for a current result.";
                    migrated.hintsRevealed = old.hintsRevealed;
                    migrated.tests = 0;
                    migrated.failures = 0;
                    if (legacy.id().equals(
                            legacyProgress.getState().selectedAttempts.get(legacy.exerciseId()))) {
                        progress.getState().selectedAttempts.put(legacy.exerciseId(), copy.id());
                    }
                    imported++;
                } catch (IOException | IllegalArgumentException exception) {
                    skipped++;
                }
            }
        }
        progress.getState().testSeconds = legacyProgress.getState().testSeconds;
        progress.getState().suiteSeconds = legacyProgress.getState().suiteSeconds;
        progress.getState().heapMb = legacyProgress.getState().heapMb;
        String summary = "Imported " + imported + " legacy attempt(s); skipped " + skipped
                + " already imported or invalid entry/entries. Originals were unchanged.";
        return new LegacyImportResult(imported, skipped, summary);
    }

    public List<Attempt> attempts(String exerciseId) throws IOException {
        ExerciseCatalog.find(exerciseId);
        Path directory = attemptsDirectory().resolve(exerciseId);
        if (!Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS) || Files.isSymbolicLink(directory)) {
            return List.of();
        }
        List<Attempt> attempts = new ArrayList<>();
        try (var paths = Files.list(directory)) {
            for (Path path : paths.sorted(Comparator.comparing(Path::toString)).toList()) {
                if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS) && !Files.isSymbolicLink(path)) {
                    try {
                        Attempt attempt = read(path.getFileName().toString());
                        if (exerciseId.equals(attempt.exerciseId())) {
                            attempts.add(attempt);
                        }
                    } catch (IOException | IllegalArgumentException ignored) {
                        // Foreign, redirecting, and incomplete content is never adopted.
                    }
                }
            }
        }
        return List.copyOf(attempts);
    }

    public Attempt read(String id) throws IOException {
        if (id == null || !id.matches("[a-z][a-z0-9-]*-[a-f0-9]{32}")) {
            throw new IOException("Invalid managed attempt ID: " + id);
        }
        int separator = id.lastIndexOf('-');
        String exerciseId = id.substring(0, separator);
        ExerciseCatalog.find(exerciseId);
        Path solution = solutionPath(exerciseId, id);
        requireOwnedSolution(solution);
        return new Attempt(id, exerciseId, REVISION, solution);
    }

    public String fingerprint(Attempt attempt) throws IOException {
        Attempt stored = read(attempt.id());
        if (!stored.solution().equals(attempt.solution().toAbsolutePath().normalize())
                || !stored.exerciseId().equals(attempt.exerciseId())) {
            throw new IOException("Managed attempt path does not match its ID.");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            update(digest, "revision=" + stored.revision());
            update(digest, "exercise=" + stored.exerciseId());
            update(digest, Files.readAllBytes(stored.solution()));
            Exercise exercise = ExerciseCatalog.find(stored.exerciseId());
            update(digest, ExerciseCatalog.resource(exercise, "ExamplesTest.java"));
            update(digest, ExerciseCatalog.resource(exercise, "CorrectnessTest.java"));
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException("SHA-256 is required by the Java runtime", impossible);
        }
    }

    public Path attemptsDirectory() {
        return configRoot.resolve("algorithm-practice/attempts").normalize();
    }

    private Path solutionPath(String exerciseId, String id) throws IOException {
        Path solution = attemptsDirectory().resolve(exerciseId).resolve(id).resolve(SOLUTION_SUFFIX).normalize();
        if (!solution.startsWith(attemptsDirectory())) {
            throw new IOException("Managed attempt must remain inside the practice storage root.");
        }
        return solution;
    }

    private void requireOwnedSolution(Path solution) throws IOException {
        if (!Files.isRegularFile(solution, LinkOption.NOFOLLOW_LINKS) || Files.isSymbolicLink(solution)) {
            throw new IOException("Managed solution is missing or redirecting: " + solution);
        }
        Path root = attemptsDirectory().toRealPath();
        Path actual = solution.toRealPath();
        if (!actual.startsWith(root)) {
            throw new IOException("Managed solution must remain inside the practice storage root.");
        }
    }

    private static void update(MessageDigest digest, String value) {
        update(digest, value.getBytes(StandardCharsets.UTF_8));
    }

    private static void update(MessageDigest digest, byte[] value) {
        digest.update((byte) 0);
        digest.update(value);
    }
}
