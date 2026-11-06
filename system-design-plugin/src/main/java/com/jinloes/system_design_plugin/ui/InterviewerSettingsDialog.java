package com.jinloes.system_design_plugin.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.Alarm;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UIUtil;
import com.jinloes.system_design_plugin.interview.CopilotInterviewer;
import com.jinloes.system_design_plugin.interview.CopilotModels;
import com.jinloes.system_design_plugin.state.DesignProgress;
import org.jetbrains.annotations.NotNull;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** Edits the Copilot CLI path, model (a dropdown of the account's models), and review timeout. */
final class InterviewerSettingsDialog extends DialogWrapper {
    private static final int RELOAD_DELAY_MILLIS = 500;

    private final TextFieldWithBrowseButton cliPath = new TextFieldWithBrowseButton();
    private final ComboBox<String> model = new ComboBox<>();
    private final JButton refresh = new JButton("Refresh");
    private final JBLabel status = new JBLabel(" ");
    private final JSpinner timeout;
    private final Map<String, String> names = new HashMap<>();
    private final AtomicInteger generation = new AtomicInteger();
    private final Alarm reload;
    private volatile boolean closed;
    private JComponent content;

    private InterviewerSettingsDialog(Project project, DesignProgress.Data data, int timeoutSeconds) {
        super(project);
        cliPath.setText(data.cliPath);
        cliPath.addBrowseFolderListener(project, FileChooserDescriptorFactory.singleFile()
                .withTitle("Copilot CLI Executable"));
        model.setEditable(true);
        model.setModel(new DefaultComboBoxModel<>(new String[]{""}));
        model.setSelectedItem(data.model == null ? "" : data.model);
        model.setRenderer(new ColoredListCellRenderer<>() {
            @Override
            protected void customizeCellRenderer(@NotNull JList<? extends String> list, String id, int index,
                                                 boolean selected, boolean focused) {
                if (id == null || id.isEmpty()) {
                    append("CLI default", SimpleTextAttributes.GRAYED_ATTRIBUTES);
                } else {
                    String name = names.get(id);
                    append(name == null || name.equals(id) ? id : name + " (" + id + ")");
                }
            }
        });
        status.setForeground(UIUtil.getContextHelpForeground());
        timeout = new JSpinner(new SpinnerNumberModel(timeoutSeconds, DesignProgress.MIN_TIMEOUT_SECONDS,
                DesignProgress.MAX_TIMEOUT_SECONDS, 30));
        reload = new Alarm(Alarm.ThreadToUse.SWING_THREAD, getDisposable());
        setTitle("System Design Interviewer");
        init();
        refresh.addActionListener(event -> loadModels());
        cliPath.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent event) {
                reload.cancelAllRequests();
                reload.addRequest(InterviewerSettingsDialog.this::loadModels, RELOAD_DELAY_MILLIS);
            }
        });
        loadModels();
    }

    static void edit(Project project, DesignProgress progress) {
        DesignProgress.Data data = progress.getState();
        InterviewerSettingsDialog dialog = new InterviewerSettingsDialog(project, data, progress.timeoutSeconds());
        if (dialog.showAndGet()) {
            data.cliPath = dialog.cliPath.getText().strip();
            data.model = dialog.modelText();
            data.timeoutSeconds = (int) dialog.timeout.getValue();
        }
    }

    private String modelText() {
        return ((JTextComponent) model.getEditor().getEditorComponent()).getText().strip();
    }

    /** Lists models off the EDT; a newer load or closing the dialog cancels this one and stops its CLI. */
    private void loadModels() {
        if (closed) {
            return;
        }
        int load = generation.incrementAndGet();
        String path = cliPath.getText();
        status.setText("Loading models…");
        ModalityState modality = ModalityState.stateForComponent(content);
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            String message;
            List<CopilotModels.Model> models = null;
            try {
                models = new CopilotInterviewer().listModels(path, () -> closed || generation.get() != load);
                message = models.size() + (models.size() == 1 ? " model available" : " models available");
            } catch (CopilotInterviewer.InterviewException | RuntimeException failure) {
                message = failure instanceof CopilotInterviewer.InterviewException ? failure.getMessage()
                        : "Could not list models: " + failure;
            }
            List<CopilotModels.Model> result = models;
            String text = message;
            ApplicationManager.getApplication().invokeLater(() -> {
                if (closed || generation.get() != load) {
                    return;
                }
                if (result != null) {
                    showModels(result);
                }
                status.setText(text);
            }, modality);
        });
    }

    private void showModels(List<CopilotModels.Model> models) {
        String typed = modelText();
        names.clear();
        DefaultComboBoxModel<String> items = new DefaultComboBoxModel<>();
        items.addElement("");
        for (CopilotModels.Model entry : models) {
            names.put(entry.id(), entry.name());
            items.addElement(entry.id());
        }
        model.setModel(items);
        model.setSelectedItem(typed);
    }

    @Override
    protected void dispose() {
        closed = true;
        super.dispose();
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel modelRow = new JPanel(new BorderLayout(4, 0));
        modelRow.add(model, BorderLayout.CENTER);
        modelRow.add(refresh, BorderLayout.EAST);
        content = FormBuilder.createFormBuilder()
                .addLabeledComponent("Copilot CLI path:", cliPath)
                .addTooltip("Leave empty to find 'copilot' on PATH.")
                .addLabeledComponent("Model:", modelRow)
                .addComponentToRightColumn(status)
                .addTooltip("Optional; passed as --model. Leave empty for the CLI default.")
                .addLabeledComponent("Timeout (seconds):", timeout)
                .addTooltip("The interviewer runs with every tool disabled and only sees your problem, notes, "
                        + "and a text summary of your diagram. Listing models sends no learner content.")
                .getPanel();
        return content;
    }

    @Override
    protected ValidationInfo doValidate() {
        String path = cliPath.getText().strip();
        if (!path.isEmpty()) {
            try {
                if (!Files.isRegularFile(Path.of(path))) {
                    return new ValidationInfo("No file exists at this path.", cliPath);
                }
            } catch (InvalidPathException invalid) {
                return new ValidationInfo("This is not a valid path.", cliPath);
            }
        }
        if (modelText().contains(" ")) {
            return new ValidationInfo("Model names do not contain spaces.", model);
        }
        try {
            DesignProgress.validateTimeout((int) timeout.getValue());
        } catch (IllegalArgumentException invalid) {
            return new ValidationInfo(invalid.getMessage(), timeout);
        }
        return null;
    }
}
