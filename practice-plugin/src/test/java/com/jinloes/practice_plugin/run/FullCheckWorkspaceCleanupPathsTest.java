package com.jinloes.practice_plugin.run;

import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.VerificationWorkspace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

class FullCheckWorkspaceCleanupPathsTest {
    private IdeaProjectTestFixture fixture;
    private Path host;

    @BeforeEach
    void setUpApplication() throws Exception {
        host = Path.of("build", "agent-fixtures", "full-check-cleanup-" + System.nanoTime())
                .toAbsolutePath().normalize();
        Files.createDirectories(host);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("full-check-cleanup", host, false).getFixture();
            fixture.setUp();
        });
    }

    @AfterEach
    void tearDownApplication() throws Exception {
        if (fixture != null) {
            EdtTestUtil.runInEdtAndWait(() -> fixture.tearDown());
        }
        if (host != null && Files.exists(host)) {
            try (var stream = Files.walk(host)) {
                stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (Exception ignored) {
                    }
                });
            }
        }
    }

    @Test
    void launchFailureCleanupMarksAndDeletesCreatedWorkspace() throws Exception {
        Path solution = host.resolve("attempt/src/main/java/com/jinloes/practice/Solution.java");
        Files.createDirectories(solution.getParent());
        Files.writeString(solution, referencePairSum(), StandardCharsets.UTF_8);
        var attempt = new ManagedPracticeWorkspace.Attempt("attempt", "pair-sum", "1", solution);

        VerificationWorkspace workspace = VerificationWorkspace.create(
                attempt, ExerciseCatalog.find("pair-sum"), "fingerprint");
        Path root = workspace.root();

        FullCheck.cleanupLaunchFailure(workspace);

        assertThat(root).doesNotExist();
    }

    private static String referencePairSum() throws Exception {
        try (var input = FullCheckWorkspaceCleanupPathsTest.class
                .getResourceAsStream("/reference/pair-sum/Solution.java")) {
            assertThat(input).isNotNull();
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
