package com.jinloes.practice_plugin.workspace;

import com.intellij.openapi.externalSystem.importing.ImportSpecBuilder;
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil;
import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.gradle.settings.DistributionType;
import org.jetbrains.plugins.gradle.settings.GradleProjectSettings;
import org.jetbrains.plugins.gradle.settings.GradleSettings;
import org.jetbrains.plugins.gradle.settings.TestRunner;
import org.jetbrains.plugins.gradle.util.GradleConstants;

import java.util.function.Consumer;

public final class GradleWorkspaceImport {
    private GradleWorkspaceImport() {}

    public static void refresh(Project project, Consumer<Boolean> completed) {
        String root = project.getBasePath();
        GradleSettings settings = GradleSettings.getInstance(project);
        GradleProjectSettings linked = settings.getLinkedProjectSettings(root);
        if (linked == null) {
            linked = new GradleProjectSettings();
            linked.setExternalProjectPath(root);
            linked.setDistributionType(DistributionType.DEFAULT_WRAPPED);
            linked.setDelegatedBuild(false);
            linked.setTestRunner(TestRunner.PLATFORM);
            settings.linkProject(linked);
        } else {
            linked.setDelegatedBuild(false);
            linked.setTestRunner(TestRunner.PLATFORM);
        }
        ExternalSystemUtil.refreshProject(root, new ImportSpecBuilder(project, GradleConstants.SYSTEM_ID)
                .withCallback(completed).withActivateToolWindowOnFailure(true));
    }
}
