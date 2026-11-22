package com.jinloes.practice_plugin.run;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.openapi.project.Project;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;

/** Builds the temporary run configuration for a managed full check. */
final class CheckRunConfigurations {
    private CheckRunConfigurations() {
    }

    /**
     * Creates, registers as temporary, tracks for project disposal, and selects the configuration.
     * Requires the caller to hold read access and the write-intent lock.
     */
    static RunnerAndConfigurationSettings install(Project project, String attemptId) {
        RunManager manager = RunManager.getInstance(project);
        var type = ConfigurationTypeUtil.findConfigurationType(PracticeConfigurationType.class);
        RunnerAndConfigurationSettings settings = manager.createConfiguration(
                "Check " + attemptId, type.getConfigurationFactories()[0]);
        var configuration = (PracticeRunConfiguration) settings.getConfiguration();
        configuration.attemptId = attemptId;
        manager.setTemporaryConfiguration(settings);
        PracticeModuleWorkspace.get(project).track(settings);
        manager.setSelectedConfiguration(settings);
        return settings;
    }
}
