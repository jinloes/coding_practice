package com.jinloes.system_design_plugin.workspace;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.Service;
import com.jinloes.system_design_plugin.catalog.DesignCatalog;
import com.jinloes.system_design_plugin.catalog.DesignCatalog.Problem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Learner attempts stored under {@code <config>/system-design-practice/attempts/<problem>/<attempt>/}. Each
 * attempt holds {@code design.excalidraw}, {@code notes.md}, and {@code feedback/<epochMillis>.json}. Files are
 * written through a temporary file and a non-replacing move, so an existing file is never overwritten.
 */
@Service(Service.Level.APP)
public final class DesignWorkspace {
    public static final String REVISION = "1";
    public static final String DIAGRAM = "design.excalidraw";
    public static final String NOTES = "notes.md";
    public static final String FEEDBACK = "feedback";
    public static final int MAX_FEEDBACK_HISTORY = 20;
    public static final String EMPTY_SCENE = """
            {
              "type": "excalidraw",
              "version": 2,
              "source": "https://system-design.invalid",
              "elements": [],
              "appState": {},
              "files": {}
            }
            """;

    private static final Pattern ATTEMPT_ID = Pattern.compile("[a-z][a-z0-9-]*-[a-f0-9]{32}");
    private static final Pattern FEEDBACK_FILE = Pattern.compile("[0-9]{1,19}\\.json");

    private final Path configRoot;
    private final Supplier<UUID> uuidSupplier;
    private final LongSupplier clock;

    public record Attempt(String id, String problemId, Path directory) {
        public Path diagram() {
            return directory.resolve(DIAGRAM);
        }

        public Path notes() {
            return directory.resolve(NOTES);
        }

        public Path feedbackDirectory() {
            return directory.resolve(FEEDBACK);
        }
    }

    public DesignWorkspace() {
        this(Path.of(PathManager.getConfigPath()));
    }

    DesignWorkspace(Path configRoot) {
        this(configRoot, UUID::randomUUID, System::currentTimeMillis);
    }

    DesignWorkspace(Path configRoot, Supplier<UUID> uuidSupplier, LongSupplier clock) {
        this.configRoot = configRoot.toAbsolutePath().normalize();
        this.uuidSupplier = uuidSupplier;
        this.clock = clock;
    }

    public Path attemptsDirectory() {
        return configRoot.resolve("system-design-practice").resolve("attempts").normalize();
    }

    /** Whether a VFS path lies at or inside {@link #attemptsDirectory()}, respecting segment and OS path rules. */
    public boolean isAttemptsPath(String vfsPath) {
        if (vfsPath == null || vfsPath.isBlank()) {
            return false;
        }
        try {
            return Path.of(vfsPath).toAbsolutePath().normalize().startsWith(attemptsDirectory());
        } catch (InvalidPathException invalid) {
            return false;
        }
    }

    public Attempt create(Problem problem) throws IOException {
        DesignCatalog.find(problem.id());
        Path problemDirectory = attemptsDirectory().resolve(problem.id());
        Files.createDirectories(problemDirectory);
        if (Files.isSymbolicLink(problemDirectory)) {
            throw new IOException("Refusing to create an attempt below a redirecting directory.");
        }
        for (int index = 0; index < 100; index++) {
            String id = problem.id() + "-" + uuidSupplier.get().toString().replace("-", "");
            Path directory = problemDirectory.resolve(id);
            if (Files.exists(directory, LinkOption.NOFOLLOW_LINKS)) {
                continue;
            }
            Files.createDirectory(directory);
            writeNew(directory.resolve(DIAGRAM), EMPTY_SCENE);
            writeNew(directory.resolve(NOTES), DesignCatalog.notesTemplate());
            return read(id);
        }
        throw new IOException("Could not create a unique attempt. Try again.");
    }

    /** Owned attempts of one problem, sorted by ID; foreign, redirecting, and incomplete entries are skipped. */
    public List<Attempt> attempts(String problemId) throws IOException {
        DesignCatalog.find(problemId);
        Path directory = attemptsDirectory().resolve(problemId);
        if (!Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS)) {
            return List.of();
        }
        List<Attempt> attempts = new ArrayList<>();
        try (var paths = Files.list(directory)) {
            for (Path path : paths.sorted(Comparator.comparing(Path::toString)).toList()) {
                try {
                    attempts.add(read(path.getFileName().toString()));
                } catch (IOException | IllegalArgumentException ignored) {
                    // Never adopt content the plugin cannot prove it owns.
                }
            }
        }
        return List.copyOf(attempts);
    }

    public Attempt read(String id) throws IOException {
        if (id == null || !ATTEMPT_ID.matcher(id).matches()) {
            throw new IOException("Invalid attempt ID: " + id);
        }
        String problemId = id.substring(0, id.lastIndexOf('-'));
        if (!DesignCatalog.exists(problemId)) {
            throw new IOException("Attempt belongs to an unknown problem: " + id);
        }
        Path directory = attemptsDirectory().resolve(problemId).resolve(id).normalize();
        Attempt attempt = new Attempt(id, problemId, directory);
        requireOwned(attempt);
        return attempt;
    }

    /**
     * The attempt whose {@code design.excalidraw} is exactly {@code file}, when the plugin owns it. Used to
     * decide which files the diagram editor may open and write.
     */
    public Optional<Attempt> ownerOfDiagram(Path file) {
        if (file == null) {
            return Optional.empty();
        }
        Path normalized = file.toAbsolutePath().normalize();
        if (!DIAGRAM.equals(String.valueOf(normalized.getFileName()))) {
            return Optional.empty();
        }
        Path directory = normalized.getParent();
        try {
            Attempt attempt = read(String.valueOf(directory.getFileName()));
            return attempt.diagram().equals(normalized) ? Optional.of(attempt) : Optional.empty();
        } catch (IOException | SecurityException exception) {
            return Optional.empty();
        }
    }

    /** SHA-256 over the revision, problem ID, notes bytes, and diagram bytes. */
    public String fingerprint(Attempt attempt) throws IOException {
        return fingerprintOwned(read(attempt.id()));
    }

    /** SHA-256 over the revision, problem ID, notes bytes, and diagram bytes for {@code attemptId}. */
    public String fingerprint(String attemptId) throws IOException {
        return fingerprintOwned(read(attemptId));
    }

    private String fingerprintOwned(Attempt stored) throws IOException {
        MessageDigest digest = digest("SHA-256");
        update(digest, ("revision=" + REVISION).getBytes(StandardCharsets.UTF_8));
        update(digest, ("problem=" + stored.problemId()).getBytes(StandardCharsets.UTF_8));
        update(digest, Files.readAllBytes(stored.notes()));
        update(digest, Files.readAllBytes(stored.diagram()));
        return HexFormat.of().formatHex(digest.digest());
    }

    static MessageDigest digest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException(algorithm + " is required by the Java runtime", impossible);
        }
    }

    public boolean isStale(Attempt attempt, String reviewedFingerprint) throws IOException {
        return !fingerprint(attempt).equals(reviewedFingerprint);
    }

    public String readNotes(Attempt attempt) throws IOException {
        return Files.readString(read(attempt.id()).notes(), StandardCharsets.UTF_8);
    }

    public String readDiagram(Attempt attempt) throws IOException {
        return Files.readString(read(attempt.id()).diagram(), StandardCharsets.UTF_8);
    }

    /**
     * Deletes an owned attempt directory and its files after re-checking ownership and verifying that
     * no entry inside the attempt tree is a symbolic link.
     */
    public void delete(String attemptId) throws IOException {
        Attempt stored = read(attemptId);
        List<Path> entries;
        try (var paths = Files.walk(stored.directory())) {
            entries = paths.sorted(Comparator.reverseOrder()).toList();
        }
        for (Path entry : entries) {
            if (Files.isSymbolicLink(entry)) {
                throw new IOException("Refusing to delete an attempt containing a redirecting entry.");
            }
        }
        for (Path entry : entries) {
            Files.delete(entry);
        }
    }

    /** Writes {@code feedback/<epochMillis>.json}, choosing a later timestamp if that name is taken. */
    public Path saveFeedback(Attempt attempt, String json) throws IOException {
        Attempt stored = read(attempt.id());
        Path directory = stored.feedbackDirectory();
        if (Files.isSymbolicLink(directory)) {
            throw new IOException("Refusing to write feedback through a redirecting directory.");
        }
        Files.createDirectories(directory);
        long millis = clock.getAsLong();
        for (int index = 0; index < 100; index++, millis++) {
            Path target = directory.resolve(millis + ".json");
            if (!Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
                writeNew(target, json);
                return target;
            }
        }
        throw new IOException("Could not choose a unique feedback file name.");
    }

    /** The newest feedback file the parser accepts; files it rejects (corrupt or foreign) are skipped. */
    public <T> Optional<T> latestFeedback(Attempt attempt, Function<String, T> parser) throws IOException {
        return readFeedbackOwned(read(attempt.id()), parser, 1).stream().findFirst();
    }

    /**
     * Up to {@link #MAX_FEEDBACK_HISTORY} valid feedback files for {@code attempt}, newest first; corrupt or
     * foreign files are skipped.
     */
    public <T> List<T> feedbackHistory(Attempt attempt, Function<String, T> parser) throws IOException {
        return readFeedbackOwned(read(attempt.id()), parser, MAX_FEEDBACK_HISTORY);
    }

    /**
     * Up to {@link #MAX_FEEDBACK_HISTORY} valid feedback files for {@code attemptId}, newest first; corrupt or
     * foreign files are skipped.
     */
    public <T> List<T> feedbackHistory(String attemptId, Function<String, T> parser) throws IOException {
        return readFeedbackOwned(read(attemptId), parser, MAX_FEEDBACK_HISTORY);
    }

    private <T> List<T> readFeedbackOwned(Attempt stored, Function<String, T> parser, int limit) throws IOException {
        Path directory = stored.feedbackDirectory();
        if (!Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS)) {
            return List.of();
        }
        List<Path> files;
        try (var paths = Files.list(directory)) {
            files = paths.filter(path -> FEEDBACK_FILE.matcher(path.getFileName().toString()).matches())
                    .filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                    .sorted(Comparator.comparingLong(DesignWorkspace::feedbackMillis).reversed())
                    .toList();
        }
        List<T> parsedItems = new ArrayList<>();
        for (Path file : files) {
            try {
                T parsed = parser.apply(Files.readString(file, StandardCharsets.UTF_8));
                if (parsed != null) {
                    parsedItems.add(parsed);
                    if (parsedItems.size() == limit) {
                        break;
                    }
                }
            } catch (IOException | RuntimeException ignored) {
                // A corrupt file never hides older valid feedback.
            }
        }
        return List.copyOf(parsedItems);
    }

    private void requireOwned(Attempt attempt) throws IOException {
        Path directory = attempt.directory();
        if (!Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS)
                || !Files.isRegularFile(attempt.diagram(), LinkOption.NOFOLLOW_LINKS)
                || !Files.isRegularFile(attempt.notes(), LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Attempt is missing or redirecting: " + attempt.id());
        }
        Path root = attemptsDirectory().toRealPath();
        Path expected = root.resolve(attempt.problemId()).resolve(attempt.id());
        if (!directory.toRealPath().equals(expected)) {
            throw new IOException("Attempt must remain inside the practice storage root.");
        }
    }

    private static long feedbackMillis(Path path) {
        String name = path.getFileName().toString();
        try {
            return Long.parseLong(name.substring(0, name.length() - ".json".length()));
        } catch (NumberFormatException overflow) {
            return -1;
        }
    }

    private static void writeNew(Path target, String content) throws IOException {
        Path temporary = Files.createTempFile(target.getParent(), ".write-", ".tmp");
        try {
            Files.writeString(temporary, content, StandardCharsets.UTF_8);
            // No REPLACE_EXISTING: an existing target makes the move fail rather than overwrite it.
            Files.move(temporary, target);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static void update(MessageDigest digest, byte[] value) {
        digest.update((byte) 0);
        digest.update(value);
    }
}
