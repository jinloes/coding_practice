package com.jinloes.practice_plugin.workspace;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.Service;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;

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

@Service(Service.Level.APP)
public final class ManagedPracticeWorkspace {
    public static final String REVISION = "1";

    /**
     * Where the generated practice project keeps the learner's solution, relative to the project
     * root. This string is on disk in every attempt directory a learner has ever created, so it is
     * effectively a storage format: changing it silently orphans existing attempts. One constant
     * makes that consequence visible at the single point where a change would be made.
     */
    public static final String SOLUTION_PATH = "src/main/java/com/jinloes/practice/Solution.java";

    private static final Path SOLUTION_SUFFIX = Path.of(SOLUTION_PATH);
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

    /**
     * The one workspace every caller shares. Two instances would each hand out attempt directories
     * under the same config root while caching nothing about the other's writes.
     */
    public static ManagedPracticeWorkspace get() {
        return ApplicationManager.getApplication().getService(ManagedPracticeWorkspace.class);
    }

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
