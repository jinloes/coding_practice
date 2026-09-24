package com.jinloes.practice_plugin.integration;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.run.PracticeRunner;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.SwingUtilities;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Shared IntelliJ fixture for execution integration tests: a host project without an SDK and one
 * managed pair-sum attempt holding the reference solution, with tight check limits.
 */
@Tag("gradle-integration")
abstract class PracticeExecutionTestBase {
    @TempDir Path temporary;
    IdeaProjectTestFixture fixture;
    ManagedPracticeWorkspace workspace;
    ManagedPracticeWorkspace.Attempt attempt;

    @BeforeEach
    void setUp() throws Exception {
        Path host = temporary.resolve("host");
        Files.createDirectories(host);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("managed-practice-execution", host, false).getFixture();
            fixture.setUp();
            WriteAction.run(() -> ProjectRootManager.getInstance(fixture.getProject()).setProjectSdk(null));
        });
        workspace = ManagedPracticeWorkspace.get();
        attempt = workspace.create(ExerciseCatalog.find("pair-sum"));
        Files.writeString(attempt.solution(), referencePairSum(), StandardCharsets.UTF_8);
        var limits = ManagedPracticeProgress.get().getState();
        limits.testSeconds = 5;
        limits.suiteSeconds = 60;
        limits.heapMb = 256;
    }

    @AfterEach
    void tearDown() throws Exception {
        if (fixture != null) {
            fixture.getProject().getService(PracticeRunner.class).stop();
            EdtTestUtil.runInEdtAndWait(() -> {
                PracticeModuleWorkspace.get(fixture.getProject()).dispose();
                fixture.tearDown();
            });
        }
    }

    private static String referencePairSum() throws Exception {
        try (var input = PracticeExecutionTestBase.class
                .getResourceAsStream("/reference/pair-sum/Solution.java")) {
            assertThat(input).isNotNull();
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    static void runOnPlainSwingEdt(CheckedRunnable action) throws Exception {
        AtomicReference<Throwable> failure = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            try {
                action.run();
            } catch (Throwable exception) {
                failure.set(exception);
            }
        });
        Throwable exception = failure.get();
        if (exception == null) {
            return;
        }
        if (exception instanceof Exception checked) {
            throw checked;
        }
        if (exception instanceof Error error) {
            throw error;
        }
        throw new RuntimeException(exception);
    }

    @FunctionalInterface
    interface CheckedRunnable {
        void run() throws Exception;
    }

}
