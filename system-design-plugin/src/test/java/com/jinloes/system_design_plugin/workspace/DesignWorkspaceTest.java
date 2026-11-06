package com.jinloes.system_design_plugin.workspace;

import com.jinloes.system_design_plugin.catalog.DesignCatalog;
import com.jinloes.system_design_plugin.catalog.DesignCatalog.Problem;
import com.jinloes.system_design_plugin.workspace.DesignWorkspace.Attempt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DesignWorkspaceTest {
    private static final Problem FIRST = DesignCatalog.all().get(0);
    private static final Problem SECOND = DesignCatalog.all().get(1);

    @TempDir Path temporary;

    private final AtomicLong clock = new AtomicLong(1_000);

    private DesignWorkspace workspace() {
        return new DesignWorkspace(temporary.resolve("config"), UUID::randomUUID, clock::get);
    }

    @Test
    void createsAnAttemptWithAnEmptySceneAndTheNotesTemplate() throws IOException {
        DesignWorkspace workspace = workspace();

        Attempt attempt = workspace.create(FIRST);

        assertThat(attempt.id()).matches(FIRST.id() + "-[a-f0-9]{32}");
        assertThat(attempt.directory()).isEqualTo(temporary.resolve("config")
                .resolve("system-design-practice/attempts").resolve(FIRST.id()).resolve(attempt.id()));
        assertThat(workspace.readDiagram(attempt)).isEqualTo(DesignWorkspace.EMPTY_SCENE);
        assertThat(com.google.gson.JsonParser.parseString(workspace.readDiagram(attempt)).getAsJsonObject()
                .get("type").getAsString()).isEqualTo("excalidraw");
        assertThat(workspace.readNotes(attempt)).isEqualTo(DesignCatalog.notesTemplate());
        assertThat(workspace.create(FIRST).id()).isNotEqualTo(attempt.id());
    }

    @Test
    void retriesTakenIdsAndGivesUpAfterOneHundredCollisions() throws IOException {
        UUID fixed = UUID.fromString("01234567-89ab-cdef-0123-456789abcdef");
        Deque<UUID> ids = new ArrayDeque<>(List.of(fixed, fixed, UUID.randomUUID()));
        DesignWorkspace workspace = new DesignWorkspace(temporary.resolve("config"), ids::pop, clock::get);
        Attempt first = workspace.create(FIRST);
        Attempt second = workspace.create(FIRST);
        assertThat(second.id()).isNotEqualTo(first.id());

        DesignWorkspace stuck = new DesignWorkspace(temporary.resolve("config"), () -> fixed, clock::get);
        assertThatThrownBy(() -> stuck.create(FIRST)).isInstanceOf(IOException.class)
                .hasMessageContaining("unique attempt");
    }

    @Test
    void listsOnlyOwnedAttemptsOfTheRequestedProblem() throws IOException {
        DesignWorkspace workspace = workspace();
        assertThat(workspace.attempts(FIRST.id())).isEmpty();
        Attempt mine = workspace.create(FIRST);
        workspace.create(SECOND);
        Path problemDirectory = workspace.attemptsDirectory().resolve(FIRST.id());
        Files.createDirectories(problemDirectory.resolve("not-an-attempt"));
        Files.createDirectories(problemDirectory.resolve(FIRST.id() + "-" + "0".repeat(32)));
        Files.writeString(problemDirectory.resolve("stray.txt"), "x");
        Path foreign = problemDirectory.resolve(SECOND.id() + "-" + "1".repeat(32));
        Files.createDirectories(foreign);

        assertThat(workspace.attempts(FIRST.id())).containsExactly(mine);
        assertThatThrownBy(() -> workspace.attempts("no-such-problem"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsInvalidIdsUnknownProblemsAndMissingFiles() throws IOException {
        DesignWorkspace workspace = workspace();
        Attempt attempt = workspace.create(FIRST);

        assertThatThrownBy(() -> workspace.read(null)).isInstanceOf(IOException.class);
        assertThatThrownBy(() -> workspace.read("../" + attempt.id())).isInstanceOf(IOException.class);
        assertThatThrownBy(() -> workspace.read("Bad-" + "0".repeat(32))).isInstanceOf(IOException.class);
        assertThatThrownBy(() -> workspace.read("unknown-" + "0".repeat(32)))
                .isInstanceOf(IOException.class).hasMessageContaining("unknown problem");

        Files.delete(attempt.notes());
        assertThatThrownBy(() -> workspace.read(attempt.id())).isInstanceOf(IOException.class);
    }

    @Test
    void rejectsSymlinkedFilesAndDirectoriesThatLeaveTheRoot() throws IOException {
        DesignWorkspace workspace = workspace();
        Attempt attempt = workspace.create(FIRST);
        Path outsideNotes = temporary.resolve("outside.md");
        Files.writeString(outsideNotes, "outside");
        Files.delete(attempt.notes());
        Files.createSymbolicLink(attempt.notes(), outsideNotes);
        assertThatThrownBy(() -> workspace.read(attempt.id())).isInstanceOf(IOException.class);

        Path outsideAttempt = temporary.resolve("outside-attempt");
        Files.createDirectories(outsideAttempt);
        Files.writeString(outsideAttempt.resolve(DesignWorkspace.DIAGRAM), "{}");
        Files.writeString(outsideAttempt.resolve(DesignWorkspace.NOTES), "notes");
        String id = FIRST.id() + "-" + "a".repeat(32);
        Files.createSymbolicLink(workspace.attemptsDirectory().resolve(FIRST.id()).resolve(id), outsideAttempt);
        assertThatThrownBy(() -> workspace.read(id)).isInstanceOf(IOException.class);

        Path redirectedProblem = temporary.resolve("redirected-problem");
        Files.createDirectories(redirectedProblem.resolve(SECOND.id() + "-" + "b".repeat(32)));
        String redirectedId = SECOND.id() + "-" + "b".repeat(32);
        Path secondProblem = workspace.attemptsDirectory().resolve(SECOND.id());
        Files.createSymbolicLink(secondProblem, redirectedProblem);
        Path inside = redirectedProblem.resolve(redirectedId);
        Files.writeString(inside.resolve(DesignWorkspace.DIAGRAM), "{}");
        Files.writeString(inside.resolve(DesignWorkspace.NOTES), "notes");
        assertThatThrownBy(() -> workspace.read(redirectedId)).isInstanceOf(IOException.class);
        assertThatThrownBy(() -> workspace.create(SECOND)).isInstanceOf(IOException.class);
    }

    @Test
    void identifiesOnlyOwnedDiagramFiles() throws IOException {
        DesignWorkspace workspace = workspace();
        Attempt attempt = workspace.create(FIRST);

        assertThat(workspace.ownerOfDiagram(attempt.diagram())).contains(attempt);
        assertThat(workspace.ownerOfDiagram(attempt.notes())).isEmpty();
        assertThat(workspace.ownerOfDiagram(null)).isEmpty();
        assertThat(workspace.ownerOfDiagram(Path.of(DesignWorkspace.DIAGRAM).getRoot())).isEmpty();
        assertThat(workspace.ownerOfDiagram(temporary.resolve(attempt.id()).resolve(DesignWorkspace.DIAGRAM)))
                .isEmpty();
        assertThat(workspace.ownerOfDiagram(temporary.resolve("x").resolve(DesignWorkspace.DIAGRAM))).isEmpty();
    }

    @Test
    void fingerprintTracksNotesAndDiagramBytesOnly() throws IOException {
        DesignWorkspace workspace = workspace();
        Attempt attempt = workspace.create(FIRST);
        String original = workspace.fingerprint(attempt);
        assertThat(workspace.fingerprint(attempt)).isEqualTo(original).hasSize(64);
        assertThat(workspace.isStale(attempt, original)).isFalse();

        Files.writeString(attempt.notes(), "changed", StandardCharsets.UTF_8);
        String afterNotes = workspace.fingerprint(attempt);
        assertThat(afterNotes).isNotEqualTo(original);
        assertThat(workspace.isStale(attempt, original)).isTrue();

        Files.writeString(attempt.diagram(), "{\"elements\":[1]}", StandardCharsets.UTF_8);
        assertThat(workspace.fingerprint(attempt)).isNotEqualTo(afterNotes);

        Files.writeString(attempt.diagram(), DesignWorkspace.EMPTY_SCENE, StandardCharsets.UTF_8);
        Files.writeString(attempt.notes(), DesignCatalog.notesTemplate(), StandardCharsets.UTF_8);
        assertThat(workspace.fingerprint(attempt)).isEqualTo(original);

        Attempt other = workspace.create(SECOND);
        Files.writeString(other.notes(), DesignCatalog.notesTemplate(), StandardCharsets.UTF_8);
        assertThat(workspace.fingerprint(other)).as("the problem id is part of the fingerprint")
                .isNotEqualTo(original);
    }

    @Test
    void savesFeedbackWithoutOverwritingAndReturnsTheNewestValidFile() throws IOException {
        DesignWorkspace workspace = workspace();
        Attempt attempt = workspace.create(FIRST);
        assertThat(workspace.latestFeedback(attempt, text -> text)).isEmpty();

        Path first = workspace.saveFeedback(attempt, "valid-1");
        Path second = workspace.saveFeedback(attempt, "valid-2");
        assertThat(first.getFileName()).hasToString("1000.json");
        assertThat(second.getFileName()).hasToString("1001.json");
        assertThat(Files.readString(first)).isEqualTo("valid-1");
        assertThat(workspace.latestFeedback(attempt, text -> text)).contains("valid-2");

        clock.set(5_000);
        workspace.saveFeedback(attempt, "corrupt");
        Files.writeString(attempt.feedbackDirectory().resolve("notes.txt"), "valid-ignored");
        Files.writeString(attempt.feedbackDirectory().resolve("99999999999999999999.json"), "valid-overflow");
        Files.createDirectories(attempt.feedbackDirectory().resolve("9000.json"));
        assertThat(workspace.latestFeedback(attempt, DesignWorkspaceTest::parseValid)).contains("valid-2");
        assertThat(workspace.latestFeedback(attempt, text -> null)).isEmpty();
    }

    @Test
    void feedbackNamesNeverReplaceExistingFiles() throws IOException {
        DesignWorkspace workspace = new DesignWorkspace(temporary.resolve("config"), UUID::randomUUID, () -> 7);
        Attempt attempt = workspace.create(FIRST);
        for (int index = 0; index < 100; index++) {
            workspace.saveFeedback(attempt, "f" + index);
        }
        assertThat(Files.readString(attempt.feedbackDirectory().resolve("7.json"))).isEqualTo("f0");
        assertThatThrownBy(() -> workspace.saveFeedback(attempt, "late")).isInstanceOf(IOException.class)
                .hasMessageContaining("unique feedback");
    }

    @Test
    void refusesRedirectedFeedbackDirectoriesAndExistingTargets() throws IOException {
        DesignWorkspace workspace = workspace();
        Attempt attempt = workspace.create(FIRST);
        Path elsewhere = temporary.resolve("elsewhere");
        Files.createDirectories(elsewhere);
        Files.createSymbolicLink(attempt.feedbackDirectory(), elsewhere);

        assertThatThrownBy(() -> workspace.saveFeedback(attempt, "x")).isInstanceOf(IOException.class);
        assertThat(workspace.latestFeedback(attempt, text -> text)).isEmpty();
        assertThat(elsewhere).isEmptyDirectory();

        Attempt existing = workspace.create(SECOND);
        assertThatThrownBy(() -> Files.move(Files.createTempFile(existing.directory(), "t", ".tmp"),
                existing.notes())).isInstanceOf(FileAlreadyExistsException.class);
    }

    private static String parseValid(String text) {
        if (!text.startsWith("valid-")) {
            throw new IllegalArgumentException("corrupt");
        }
        return text;
    }
}
