package com.jinloes.practice_plugin.workspace;

import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.ide.scratch.ScratchRootType;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

public final class ScratchAttemptStore {
    public static final String REVISION = "1";
    private static final String DIRECTORY = "Algorithm Practice";
    private final Project project;
    private final Path scratchRoot;

    public record Attempt(String id, String exerciseId, String revision, Path solution) {
        public Path directory() {
            return solution.getParent();
        }
    }

    public ScratchAttemptStore(Project project) {
        this(project, Path.of(ScratchFileService.getInstance().getRootPath(ScratchRootType.getInstance())));
    }

    ScratchAttemptStore(Path scratchRoot) {
        this(null, scratchRoot);
    }

    private ScratchAttemptStore(Project project, Path scratchRoot) {
        this.project = project;
        this.scratchRoot = scratchRoot.toAbsolutePath().normalize();
    }

    public Attempt create(Exercise exercise) throws IOException {
        for (int index = 0; index < 100; index++) {
            String id = exercise.id() + "-" + UUID.randomUUID().toString().replace("-", "");
            Path solution = solutionPath(exercise.id(), id);
            if (Files.exists(solution, LinkOption.NOFOLLOW_LINKS)) {
                continue;
            }
            createSolution(exercise, solution);
            return read(id);
        }
        throw new IOException("Could not create a unique scratch attempt. Try again.");
    }

    public Attempt copyLegacy(Path legacyRoot, PracticeWorkspace.Attempt legacy) throws IOException {
        Path root = legacyRoot.toRealPath();
        Path solution = legacy.solution().toRealPath();
        if (!solution.startsWith(root) || !Files.isRegularFile(solution, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("The legacy solution must remain inside its marked practice project.");
        }
        Attempt copy = create(ExerciseCatalog.find(legacy.exerciseId()));
        Files.writeString(copy.solution(), Files.readString(solution, StandardCharsets.UTF_8), StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING);
        return copy;
    }

    public List<Attempt> attempts(String exerciseId) throws IOException {
        ExerciseCatalog.find(exerciseId);
        Path directory = attemptsDirectory().resolve(exerciseId);
        if (!Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS)) {
            return List.of();
        }
        List<Attempt> attempts = new ArrayList<>();
        try (var paths = Files.list(directory)) {
            for (Path path : paths.sorted().toList()) {
                if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                    try {
                        Attempt attempt = read(path.getFileName().toString());
                        if (exerciseId.equals(attempt.exerciseId())) {
                            attempts.add(attempt);
                        }
                    } catch (IOException ignored) {
                        // Foreign or incomplete scratch content is never adopted or changed.
                    }
                }
            }
        }
        return List.copyOf(attempts);
    }

    public Attempt read(String id) throws IOException {
        if (!id.matches("[a-z][a-z0-9-]*-[a-f0-9]{32}")) {
            throw new IOException("Invalid scratch attempt ID: " + id);
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
        if (!stored.solution().equals(attempt.solution().toAbsolutePath().normalize())) {
            throw new IOException("Scratch attempt path does not match its ID.");
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

    public Path scratchDirectory() {
        return attemptsDirectory();
    }

    private void createSolution(Exercise exercise, Path solution) throws IOException {
        if (project == null) {
            Files.createDirectories(solution.getParent());
            Files.writeString(solution, ExerciseCatalog.resource(exercise, "Solution.java"), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW);
            return;
        }
        String relativePath = scratchRoot.relativize(solution).toString().replace('\\', '/');
        VirtualFile file = ScratchRootType.getInstance().createScratchFile(
                project,
                relativePath,
                JavaLanguage.INSTANCE,
                ExerciseCatalog.resource(exercise, "Solution.java"),
                ScratchFileService.Option.create_if_missing);
        if (file == null || !Path.of(file.getPath()).toAbsolutePath().normalize().equals(solution)) {
            throw new IOException("IDE could not create the requested Java scratch: " + relativePath);
        }
    }

    private Path solutionPath(String exerciseId, String id) throws IOException {
        Path solution = attemptsDirectory().resolve(exerciseId).resolve(id).resolve("Solution.java").normalize();
        if (!solution.startsWith(attemptsDirectory())) {
            throw new IOException("Scratch attempt must remain inside Algorithm Practice.");
        }
        return solution;
    }

    private Path attemptsDirectory() {
        return scratchRoot.resolve(DIRECTORY).normalize();
    }

    private void requireOwnedSolution(Path solution) throws IOException {
        if (!Files.isRegularFile(solution, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Scratch solution is missing: " + solution);
        }
        Path root = attemptsDirectory().toRealPath();
        Path actual = solution.toRealPath();
        if (!actual.startsWith(root)) {
            throw new IOException("Scratch solution must remain inside Algorithm Practice.");
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
