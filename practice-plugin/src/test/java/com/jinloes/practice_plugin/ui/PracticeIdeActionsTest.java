package com.jinloes.practice_plugin.ui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionUiKind;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.run.PracticeRunner;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.JComboBox;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PracticeIdeActionsTest {
    private static final List<String> ACTION_IDS = List.of(
            "AlgorithmPractice.StartOrOpenAttempt",
            "AlgorithmPractice.RunExamples",
            "AlgorithmPractice.DebugExamples",
            "AlgorithmPractice.CheckSolution",
            "AlgorithmPractice.StopRun");

    @TempDir Path temporary;
    private IdeaProjectTestFixture fixture;

    @BeforeEach
    void setUp() throws Exception {
        Path host = temporary.resolve("host");
        Files.createDirectories(host);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("algorithm-practice-actions", host, false).getFixture();
            fixture.setUp();
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        if (fixture != null) {
            fixture.getProject().getService(PracticeRunner.class).stop();
            EdtTestUtil.runInEdtAndWait(() -> {
                var editors = com.intellij.openapi.fileEditor.FileEditorManager.getInstance(fixture.getProject());
                for (var openFile : editors.getOpenFiles()) {
                    editors.closeFile(openFile);
                }
                PracticeModuleWorkspace.get(fixture.getProject()).dispose();
                fixture.tearDown();
            });
        }
    }

    @Test
    void registersPublicActionsInActionManagerAndPreservesNewerPanelOnOutOfOrderDispose() {
        EdtTestUtil.runInEdtAndWait(() -> {
            AnAction group = ActionManager.getInstance().getAction("AlgorithmPractice.Actions");
            assertThat(group).isInstanceOf(DefaultActionGroup.class);
            for (String id : ACTION_IDS) {
                AnAction registered = ActionManager.getInstance().getAction(id);
                assertThat(registered).as(id).isNotNull();
            }

            assertThat(new PracticeIdeActions.StartOrOpenAttempt()).isNotNull();
            assertThat(new PracticeIdeActions.RunExamples()).isNotNull();
            assertThat(new PracticeIdeActions.DebugExamples()).isNotNull();
            assertThat(new PracticeIdeActions.CheckSolution()).isNotNull();
            assertThat(new PracticeIdeActions.StopRun()).isNotNull();

            PracticePanel first = new PracticePanel(fixture.getProject());
            PracticePanel second = new PracticePanel(fixture.getProject());
            assertThat(PracticeIdeActions.panel(fixture.getProject())).isSameAs(second);

            Disposer.dispose(first);
            assertThat(PracticeIdeActions.panel(fixture.getProject()))
                    .as("disposing an older panel must not clear the newer live panel")
                    .isSameAs(second);

            Disposer.dispose(second);
            assertThat(PracticeIdeActions.panel(fixture.getProject())).isNull();
        });
    }

    @Test
    void updatesEnablementBeforeAndAfterToolWindowActivationAndDelegatesAttemptCreation() throws Exception {
        var exercise = ExerciseCatalog.find("pair-sum");
        Path pairSumAttempts = ManagedPracticeWorkspace.get().attemptsDirectory().resolve(exercise.id());
        com.intellij.openapi.util.io.NioFiles.deleteRecursively(pairSumAttempts);
        ManagedPracticeProgress.get().getState().selectedAttempts.remove(exercise.id());

        AnAction startAction = ActionManager.getInstance().getAction("AlgorithmPractice.StartOrOpenAttempt");
        AnAction runAction = ActionManager.getInstance().getAction("AlgorithmPractice.RunExamples");
        AnAction debugAction = ActionManager.getInstance().getAction("AlgorithmPractice.DebugExamples");
        AnAction checkAction = ActionManager.getInstance().getAction("AlgorithmPractice.CheckSolution");
        AnAction stopAction = ActionManager.getInstance().getAction("AlgorithmPractice.StopRun");
        DataContext context = SimpleDataContext.getSimpleContext(CommonDataKeys.PROJECT, fixture.getProject());
        var editors = com.intellij.openapi.fileEditor.FileEditorManager.getInstance(fixture.getProject());

        EdtTestUtil.runInEdtAndWait(() -> {
            assertThat(enabled(startAction, context))
                    .as("without a Practice tool window or panel, StartOrOpenAttempt is disabled")
                    .isFalse();
            assertThat(enabled(runAction, context)).isFalse();
            assertThat(enabled(stopAction, context)).isFalse();
        });

        var manager = ToolWindowManager.getInstance(fixture.getProject());
        var window = EdtTestUtil.runInEdtAndGet(
                () -> manager.registerToolWindow(PracticeIdeActions.TOOL_WINDOW_ID, true, ToolWindowAnchor.RIGHT));
        try {
            EdtTestUtil.runInEdtAndWait(() -> {
                assertThat(enabled(startAction, context))
                        .as("StartOrOpenAttempt is enabled before the Practice tool window is first opened")
                        .isTrue();
                assertThat(enabled(runAction, context)).isFalse();
                assertThat(editors.getOpenFiles()).isEmpty();

                perform(startAction, context);
            });

            awaitEdtCondition(() -> PracticeIdeActions.panel(fixture.getProject()) != null
                    && enabled(runAction, context)
                    && editors.getOpenFiles().length > 0);

            EdtTestUtil.runInEdtAndWait(() -> {
                PracticePanel panel = PracticeIdeActions.panel(fixture.getProject());
                assertThat(panel).isNotNull();

                @SuppressWarnings("unchecked")
                JComboBox<ManagedPracticeWorkspace.Attempt> combo =
                        com.intellij.util.ui.UIUtil.findComponentsOfType(panel, JComboBox.class).stream()
                                .filter(box -> "Saved attempts".equals(box.getAccessibleContext().getAccessibleName()))
                                .findFirst()
                                .orElseThrow();
                ManagedPracticeWorkspace.Attempt created =
                        (ManagedPracticeWorkspace.Attempt) combo.getSelectedItem();
                assertThat(created)
                        .as("StartOrOpenAttempt must create and select an attempt on cold activation")
                        .isNotNull();
                assertThat(created.exerciseId()).isEqualTo(exercise.id());
                assertThat(editors.getOpenFiles())
                        .anyMatch(file -> Path.of(file.getPath()).toAbsolutePath().normalize()
                                .equals(created.solution()));

                assertThat(enabled(startAction, context)).isTrue();
                assertThat(enabled(runAction, context)).isTrue();
                assertThat(enabled(debugAction, context)).isTrue();
                assertThat(enabled(checkAction, context)).isTrue();
                assertThat(enabled(stopAction, context)).isFalse();

                perform(stopAction, context);
            });
        } finally {
            EdtTestUtil.runInEdtAndWait(() -> {
                PracticePanel panel = PracticeIdeActions.panel(fixture.getProject());
                if (panel != null) {
                    Disposer.dispose(panel);
                }
                manager.unregisterToolWindow(PracticeIdeActions.TOOL_WINDOW_ID);
            });
        }
    }

    @Test
    void registeredPanelActionsDelegateStartRunDebugCheckAndStopLifecycle() throws Exception {
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        AnAction startAction = ActionManager.getInstance().getAction("AlgorithmPractice.StartOrOpenAttempt");
        AnAction runAction = ActionManager.getInstance().getAction("AlgorithmPractice.RunExamples");
        AnAction debugAction = ActionManager.getInstance().getAction("AlgorithmPractice.DebugExamples");
        AnAction checkAction = ActionManager.getInstance().getAction("AlgorithmPractice.CheckSolution");
        AnAction stopAction = ActionManager.getInstance().getAction("AlgorithmPractice.StopRun");
        DataContext context = SimpleDataContext.getSimpleContext(CommonDataKeys.PROJECT, fixture.getProject());
        var editors = com.intellij.openapi.fileEditor.FileEditorManager.getInstance(fixture.getProject());

        var freshExercise = ExerciseCatalog.all().stream()
                .filter(candidate -> {
                    try {
                        return ManagedPracticeWorkspace.get().attempts(candidate.id()).isEmpty();
                    } catch (java.io.IOException exception) {
                        throw new java.io.UncheckedIOException(exception);
                    }
                })
                .findFirst()
                .orElseThrow();
        PracticePanel panel = EdtTestUtil.runInEdtAndGet(() -> new PracticePanel(fixture.getProject()));
        try {
            EdtTestUtil.runInEdtAndWait(() -> {
                @SuppressWarnings("unchecked")
                com.intellij.ui.components.JBList<ExerciseCatalog.Exercise> list =
                        com.intellij.util.ui.UIUtil.findComponentOfType(panel, com.intellij.ui.components.JBList.class);
                list.setSelectedValue(freshExercise, true);
                PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue();

                assertThat(enabled(startAction, context)).isTrue();
                assertThat(enabled(runAction, context)).isFalse();
                assertThat(enabled(debugAction, context)).isFalse();
                assertThat(enabled(checkAction, context)).isFalse();
                assertThat(enabled(stopAction, context)).isFalse();

                perform(startAction, context);
            });

            awaitEdtCondition(() -> enabled(runAction, context) && editors.getOpenFiles().length > 0);

            EdtTestUtil.runInEdtAndWait(() -> {
                assertThat(enabled(runAction, context)).isTrue();
                assertThat(enabled(debugAction, context)).isTrue();
                assertThat(enabled(checkAction, context)).isTrue();
                assertThat(enabled(stopAction, context)).isFalse();
            });

            assertRunOrDebugCanBeStoppedViaIdeAction(
                    runner, runAction, false, debugAction, checkAction, stopAction, context);
            assertRunOrDebugCanBeStoppedViaIdeAction(
                    runner, debugAction, true, runAction, checkAction, stopAction, context);

            String beforeBanner = EdtTestUtil.runInEdtAndGet(() -> panel.resultsView().bannerText());
            assertThat(beforeBanner).isEqualTo("Examples could not run; see details below");

            java.util.concurrent.atomic.AtomicReference<com.jinloes.practice_plugin.run.RunEvent> terminalEvent =
                    new java.util.concurrent.atomic.AtomicReference<>();
            java.util.concurrent.CountDownLatch checkEvent = new java.util.concurrent.CountDownLatch(1);
            java.util.function.Consumer<com.jinloes.practice_plugin.run.RunEvent> checkListener = event -> {
                if (event instanceof com.jinloes.practice_plugin.run.RunEvent.FailedToStart
                        || event instanceof com.jinloes.practice_plugin.run.RunEvent.CheckFinished) {
                    terminalEvent.compareAndSet(null, event);
                    checkEvent.countDown();
                }
            };
            runner.addEventListener(checkListener);
            try {
                EdtTestUtil.runInEdtAndWait(() -> perform(checkAction, context));
                assertThat(checkEvent.await(30, java.util.concurrent.TimeUnit.SECONDS)).isTrue();
                EdtTestUtil.runInEdtAndWait(() -> {
                    PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue();
                    var observed = terminalEvent.get();
                    assertThat(observed).isNotNull();
                    assertThat(panel.resultsView().bannerText()).isNotEqualTo(beforeBanner);
                    if (observed instanceof com.jinloes.practice_plugin.run.RunEvent.CheckFinished) {
                        assertThat(panel.statusText()).isEqualTo("Run finished; see Results.");
                        assertThat(panel.resultsView().bannerText()).isEqualTo("Failed: runtime error");
                    } else {
                        assertThat(panel.statusText()).isEqualTo("Action failed; see Results.");
                        assertThat(panel.resultsView().bannerText()).isEqualTo("Action failed");
                    }
                    assertThat(runner.isRunning()).isFalse();
                    assertThat(enabled(checkAction, context)).isTrue();
                    assertThat(enabled(stopAction, context)).isFalse();
                });
            } finally {
                runner.removeEventListener(checkListener);
            }
        } finally {
            runner.stop();
            EdtTestUtil.runInEdtAndWait(() -> {
                for (var openFile : editors.getOpenFiles()) {
                    editors.closeFile(openFile);
                }
                Disposer.dispose(panel);
            });
            com.intellij.openapi.util.io.NioFiles.deleteRecursively(
                    ManagedPracticeWorkspace.get().attemptsDirectory().resolve(freshExercise.id()));
            ManagedPracticeProgress.get().getState().selectedAttempts.remove(freshExercise.id());
        }
    }

    private static void assertRunOrDebugCanBeStoppedViaIdeAction(
            PracticeRunner runner,
            AnAction triggerAction,
            boolean expectedDebug,
            AnAction otherRunAction,
            AnAction checkAction,
            AnAction stopAction,
            DataContext context
    ) throws Exception {
        java.util.concurrent.CountDownLatch finished = new java.util.concurrent.CountDownLatch(1);
        java.util.function.Consumer<com.jinloes.practice_plugin.run.RunEvent> listener = event -> {
            if (event instanceof com.jinloes.practice_plugin.run.RunEvent.ExampleFinished) {
                finished.countDown();
            }
        };
        runner.addEventListener(listener);
        try {
            EdtTestUtil.runInEdtAndWait(() -> {
                perform(triggerAction, context);
                assertThat(runner.isRunning()).isTrue();
                assertThat(activeExampleDebug(runner)).isEqualTo(expectedDebug);
                assertThat(enabled(triggerAction, context)).isFalse();
                assertThat(enabled(otherRunAction, context)).isFalse();
                assertThat(enabled(checkAction, context)).isFalse();
                assertThat(enabled(stopAction, context)).isTrue();

                perform(stopAction, context);
            });
            assertThat(finished.await(30, java.util.concurrent.TimeUnit.SECONDS)).isTrue();
            EdtTestUtil.runInEdtAndWait(() -> {
                PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue();
                assertThat(runner.isRunning()).isFalse();
                assertThat(enabled(stopAction, context)).isFalse();
                assertThat(enabled(triggerAction, context)).isTrue();
            });
        } finally {
            runner.removeEventListener(listener);
        }
    }

    private static Boolean activeExampleDebug(PracticeRunner runner) {
        try {
            var sessionField = PracticeRunner.class.getDeclaredField("session");
            sessionField.setAccessible(true);
            Object current = ((java.util.concurrent.atomic.AtomicReference<?>) sessionField.get(runner)).get();
            if (!"ExampleSession".equals(current.getClass().getSimpleName())) {
                return null;
            }
            var debugMethod = current.getClass().getDeclaredMethod("debug");
            debugMethod.setAccessible(true);
            return (Boolean) debugMethod.invoke(current);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }

    private static void awaitEdtCondition(java.util.function.BooleanSupplier condition)
            throws InterruptedException {
        long deadline = System.nanoTime() + java.util.concurrent.TimeUnit.SECONDS.toNanos(15);
        while (System.nanoTime() < deadline) {
            boolean met = EdtTestUtil.runInEdtAndGet(() -> {
                PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue();
                return condition.getAsBoolean();
            });
            if (met) {
                return;
            }
            Thread.sleep(10);
        }
        EdtTestUtil.runInEdtAndWait(PlatformTestUtil::dispatchAllInvocationEventsInIdeEventQueue);
    }

    private static boolean enabled(AnAction action, DataContext context) {
        AnActionEvent event = AnActionEvent.createEvent(
                action, context, null, "test", ActionUiKind.NONE, null);
        action.update(event);
        return event.getPresentation().isEnabled();
    }

    private static void perform(AnAction action, DataContext context) {
        AnActionEvent event = AnActionEvent.createEvent(
                action, context, null, "test", ActionUiKind.NONE, null);
        action.actionPerformed(event);
    }
}
