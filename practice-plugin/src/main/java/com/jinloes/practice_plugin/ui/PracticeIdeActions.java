package com.jinloes.practice_plugin.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolderEx;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Global IDE actions registered under Tools -> Algorithm Practice so learners can invoke core
 * practice workflows from Find Action or custom keymap shortcuts while focused in the editor.
 */
public final class PracticeIdeActions {
    static final String TOOL_WINDOW_ID = "Practice";
    private static final Key<PracticePanel> PANEL_KEY = Key.create("com.jinloes.practice.panel");

    private PracticeIdeActions() {
    }

    static void register(Project project, PracticePanel panel) {
        project.putUserData(PANEL_KEY, panel);
    }

    static void unregister(Project project, PracticePanel panel) {
        if (project instanceof UserDataHolderEx holder) {
            holder.replace(PANEL_KEY, panel, null);
        } else if (project.getUserData(PANEL_KEY) == panel) {
            project.putUserData(PANEL_KEY, null);
        }
    }

    static PracticePanel panel(Project project) {
        return project == null || project.isDisposed() ? null : project.getUserData(PANEL_KEY);
    }

    public static final class StartOrOpenAttempt extends DumbAwareAction {
        public StartOrOpenAttempt() {
            super("Start or Open Attempt", "Create or open Solution.java for the selected exercise",
                    AllIcons.Actions.EditSource);
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.EDT;
        }

        @Override
        public void update(@NotNull AnActionEvent event) {
            Project project = event.getProject();
            PracticePanel active = panel(project);
            boolean enabled = active != null
                    ? active.hasSelectedExercise()
                    : project != null && !project.isDisposed()
                            && ToolWindowManager.getInstance(project).getToolWindow(TOOL_WINDOW_ID) != null;
            event.getPresentation().setEnabled(enabled);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent event) {
            Project project = event.getProject();
            if (project == null || project.isDisposed()) {
                return;
            }
            PracticePanel active = panel(project);
            if (active != null) {
                if (active.hasSelectedExercise()) {
                    active.startOrOpenAttempt();
                }
                return;
            }
            ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow(TOOL_WINDOW_ID);
            if (window == null) {
                return;
            }
            window.activate(() -> {
                PracticePanel opened = panel(project);
                if (opened == null && window.getContentManager().getContentCount() == 0) {
                    new PracticeToolWindowFactory().createToolWindowContent(project, window);
                    opened = panel(project);
                }
                if (opened != null && opened.hasSelectedExercise()) {
                    opened.startOrOpenAttempt();
                }
            });
        }
    }

    public static final class RunExamples extends PanelAction {
        public RunExamples() {
            super("Run Examples", "Run the visible examples for the selected attempt",
                    AllIcons.Actions.Execute, PracticePanel::canRun, PracticePanel::runExamples);
        }
    }

    public static final class DebugExamples extends PanelAction {
        public DebugExamples() {
            super("Debug Examples", "Debug the visible examples for the selected attempt",
                    AllIcons.Actions.StartDebugger, PracticePanel::canRun, PracticePanel::debugExamples);
        }
    }

    public static final class CheckSolution extends PanelAction {
        public CheckSolution() {
            super("Check Solution", "Run the full test suite and complexity probe for the selected attempt",
                    AllIcons.Actions.Checked, PracticePanel::canRun, PracticePanel::check);
        }
    }

    public static final class StopRun extends PanelAction {
        public StopRun() {
            super("Stop Practice Run", "Stop the active practice run or full check",
                    AllIcons.Actions.Suspend, PracticePanel::isRunning, PracticePanel::stop);
        }
    }

    private abstract static class PanelAction extends DumbAwareAction {
        private final Predicate<PracticePanel> enabled;
        private final Consumer<PracticePanel> handler;

        PanelAction(
                String text,
                String description,
                Icon icon,
                Predicate<PracticePanel> enabled,
                Consumer<PracticePanel> handler
        ) {
            super(text, description, icon);
            this.enabled = enabled;
            this.handler = handler;
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.EDT;
        }

        @Override
        public void update(@NotNull AnActionEvent event) {
            PracticePanel active = panel(event.getProject());
            event.getPresentation().setEnabled(active != null && enabled.test(active));
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent event) {
            PracticePanel active = panel(event.getProject());
            if (active != null && enabled.test(active)) {
                handler.accept(active);
            }
        }
    }
}
