package com.jinloes.practice_plugin.run;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiJavaFile;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;

/** Builds the temporary native Application run configuration for a visible-example or input run. */
final class ExampleRunConfigurations {
    private ExampleRunConfigurations() {
    }

    /**
     * Creates, registers as temporary, and selects the configuration. Requires the caller to hold
     * read access and the write-intent lock.
     */
    static RunnerAndConfigurationSettings install(
            Project project,
            ManagedPracticeWorkspace.Attempt attempt,
            String attemptId,
            PsiJavaFile runner,
            Module module,
            String input,
            boolean debug
    ) {
        RunManager manager = RunManager.getInstance(project);
        RunnerAndConfigurationSettings settings = manager.createConfiguration(
                (debug ? "Debug " : "Run ") + (input.isEmpty() ? "" : "input ") + attemptId,
                ApplicationConfigurationType.getInstance().getConfigurationFactories()[0]);
        var configuration = (ApplicationConfiguration) settings.getConfiguration();
        configuration.setMainClass(runner.getClasses()[0]);
        configuration.setModule(module);
        configuration.setWorkingDirectory(attempt.directory().toString());
        configuration.setProgramParameters(input.isEmpty() ? null : quoteArgument(input));
        manager.setTemporaryConfiguration(settings);
        manager.setSelectedConfiguration(settings);
        return settings;
    }

    private static String quoteArgument(String input) {
        return '"' + input.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
    }
}
