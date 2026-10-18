package com.jinloes.practice_plugin.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import javax.swing.JComponent;
import java.util.List;
import java.util.function.BooleanSupplier;

/** Icon actions for the Practice tool window's toolbars and title bar. */
final class PracticeActions {
    private PracticeActions() {
    }

    /** An action that runs {@code perform} and is enabled while {@code enabled} holds. */
    static AnAction action(String text, String description, Icon icon, BooleanSupplier enabled, Runnable perform) {
        return new DumbAwareAction(text, description, icon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent event) {
                perform.run();
            }

            @Override
            public void update(@NotNull AnActionEvent event) {
                event.getPresentation().setEnabled(enabled.getAsBoolean());
            }

            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.EDT;
            }
        };
    }

    /** Shows the action's text beside its icon, for the primary actions a learner looks for. */
    static AnAction withText(AnAction action) {
        action.getTemplatePresentation().putClientProperty(ActionUtil.SHOW_TEXT_IN_TOOLBAR, true);
        return action;
    }

    static ActionToolbar toolbar(String place, List<AnAction> actions, JComponent target) {
        ActionToolbar toolbar = ActionManager.getInstance()
                .createActionToolbar(place, new DefaultActionGroup(actions), true);
        toolbar.setTargetComponent(target);
        return toolbar;
    }

    /** The execution toolbar: run examples, run or debug with input, check, and stop. */
    static ActionToolbar runToolbar(RunControls controls, JComponent target) {
        return toolbar("AlgorithmPracticeRun", List.of(
                withText(action("Run Examples", "Run the visible examples",
                        AllIcons.Actions.Execute, controls::canRun, controls::runExamples)),
                action("Run With Input...", "Run the solution on custom input",
                        AllIcons.Actions.RunAll, controls::canRun, () -> controls.runWithInput(false)),
                action("Debug With Input...", "Debug the solution on custom input",
                        AllIcons.Actions.StartDebugger, controls::canRun, () -> controls.runWithInput(true)),
                withText(action("Check Solution", "Run the full hidden test suite and complexity probe",
                        AllIcons.Actions.Checked, controls::canRun, controls::check)),
                action("Stop", "Stop the active run", AllIcons.Actions.Suspend,
                        controls::isRunning, controls::stop)), target);
    }

    /** What the execution toolbar drives. */
    interface RunControls {
        boolean canRun();

        boolean isRunning();

        void runExamples();

        void runWithInput(boolean debug);

        void check();

        void stop();
    }
}
