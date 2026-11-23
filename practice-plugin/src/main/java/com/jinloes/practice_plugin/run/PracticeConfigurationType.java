package com.jinloes.practice_plugin.run;

import com.intellij.icons.AllIcons;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;

public final class PracticeConfigurationType extends ConfigurationTypeBase {
    public PracticeConfigurationType() {
        super("AlgorithmPractice", "Algorithm Practice", "Run local exercise correctness checks",
                AllIcons.Actions.Execute);
        addFactory(new ConfigurationFactory(this) {
            @Override
            public String getId() {
                return "AlgorithmPractice";
            }

            @Override
            public RunConfiguration createTemplateConfiguration(Project project) {
                return new PracticeRunConfiguration(project, this, "Practice");
            }
        });
    }
}
