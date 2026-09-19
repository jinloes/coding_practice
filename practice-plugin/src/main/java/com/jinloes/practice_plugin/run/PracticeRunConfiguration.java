package com.jinloes.practice_plugin.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import javax.swing.JLabel;

public final class PracticeRunConfiguration extends RunConfigurationBase<PracticeRunConfiguration.Options> {
    public static final class Options extends com.intellij.execution.configurations.RunConfigurationOptions {}

    public String attemptId = "";
    public boolean full = true;

    public PracticeRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @Override
    public @NotNull SettingsEditor<PracticeRunConfiguration> getConfigurationEditor() {
        return new SettingsEditor<>() {
            @Override protected void resetEditorFrom(@NotNull PracticeRunConfiguration configuration) {}
            @Override protected void applyEditorTo(@NotNull PracticeRunConfiguration configuration) {}
            @Override protected @NotNull JComponent createEditor() {
                return new JLabel("Select an attempt and launch examples or a full check from the Practice tool window.");
            }
        };
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (!attemptId.matches("[a-z][a-z0-9-]*")) {
            throw new RuntimeConfigurationError("Select an attempt in the Practice tool window.");
        }
        try {
            PracticeRunner.javaHome(getProject());
        } catch (ExecutionException e) {
            throw new RuntimeConfigurationError(e.getMessage());
        }
    }

    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment)
            throws ExecutionException {
        if (!com.intellij.execution.executors.DefaultRunExecutor.EXECUTOR_ID.equals(executor.getId())) {
            throw new ExecutionException("Use Debug Examples in the Practice tool window for native Java debugging.");
        }
        return new CommandLineState(environment) {
            @Override
            protected @NotNull ProcessHandler startProcess() throws ExecutionException {
                return getProject().getService(PracticeRunner.class).start(attemptId, full);
            }
        };
    }

    @Override
    public void readExternal(@NotNull Element element) throws com.intellij.openapi.util.InvalidDataException {
        super.readExternal(element);
        attemptId = element.getAttributeValue("attempt", "");
        full = Boolean.parseBoolean(element.getAttributeValue("full", "true"));
    }

    @Override
    public void writeExternal(@NotNull Element element) throws com.intellij.openapi.util.WriteExternalException {
        super.writeExternal(element);
        element.setAttribute("attempt", attemptId);
        element.setAttribute("full", Boolean.toString(full));
    }
}
