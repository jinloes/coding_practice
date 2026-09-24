package com.jinloes.practice_plugin.ui;

import com.intellij.openapi.project.Project;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.GridLayout;

/** Edits the per-test, per-suite, and heap limits applied to full checks. */
final class LimitsDialog {
    private LimitsDialog() {
    }

    static void edit(Project project, ManagedPracticeProgress progress) {
        JSpinner perTest = new JSpinner(new SpinnerNumberModel(progress.getState().testSeconds, 1, 300, 1));
        JSpinner perSuite = new JSpinner(new SpinnerNumberModel(progress.getState().suiteSeconds, 1, 1800, 5));
        JSpinner heap = new JSpinner(new SpinnerNumberModel(progress.getState().heapMb, 64, 2048, 64));
        JPanel fields = new JPanel(new GridLayout(0, 2, 8, 8));
        fields.add(new JLabel("Seconds per test")); fields.add(perTest);
        fields.add(new JLabel("Seconds per suite")); fields.add(perSuite);
        fields.add(new JLabel("Test heap (MiB)")); fields.add(heap);
        var dialog = new com.intellij.openapi.ui.DialogWrapper(project) {
            { setTitle("Practice Execution Limits"); init(); }
            @Override protected javax.swing.JComponent createCenterPanel() { return fields; }
            @Override protected com.intellij.openapi.ui.ValidationInfo doValidate() {
                return (int) perSuite.getValue() < (int) perTest.getValue()
                        ? new com.intellij.openapi.ui.ValidationInfo(
                        "Suite limit must be at least the per-test limit.", perSuite) : null;
            }
        };
        if (dialog.showAndGet()) {
            progress.getState().testSeconds = (int) perTest.getValue();
            progress.getState().suiteSeconds = (int) perSuite.getValue();
            progress.getState().heapMb = (int) heap.getValue();
        }
    }
}
