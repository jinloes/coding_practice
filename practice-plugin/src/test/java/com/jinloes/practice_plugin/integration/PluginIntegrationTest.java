package com.jinloes.practice_plugin.integration;

import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.run.PracticeConfigurationType;
import com.jinloes.practice_plugin.run.PracticeRunConfiguration;
import com.jinloes.practice_plugin.run.PracticeRunner;
import com.jinloes.practice_plugin.state.PracticeProgress;
import com.jinloes.practice_plugin.ui.PracticeToolWindowFactory;
import org.jdom.Element;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PluginIntegrationTest {
    private IdeaProjectTestFixture fixture;

    @BeforeEach
    void setUp() throws Exception {
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createLightFixtureBuilder("algorithm-practice").getFixture();
            fixture.setUp();
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        if (fixture != null) {
            EdtTestUtil.runInEdtAndWait(fixture::tearDown);
        }
    }

    @Test
    void pluginLoadsAndProjectServicesResolve() throws Exception {
        EdtTestUtil.runInEdtAndWait(() -> {
            assertThat(PluginManagerCore.getPlugin(PluginId.getId("com.jinloes.practice"))).isNotNull();
            assertThat(PracticeProgress.get(fixture.getProject())).isNotNull();
            assertThat(fixture.getProject().getService(PracticeRunner.class).isRunning()).isFalse();
        });
    }

    @Test
    void nativeRunConfigurationRoundTripsAndFindsItsExecutor() throws Exception {
        EdtTestUtil.runInEdtAndWait(() -> {
            var type = ConfigurationTypeUtil.findConfigurationType(PracticeConfigurationType.class);
            var settings = RunManager.getInstance(fixture.getProject())
                    .createConfiguration("Check attempt", type.getConfigurationFactories()[0]);
            var configuration = (PracticeRunConfiguration) settings.getConfiguration();
            configuration.attemptId = "pair-sum-abc123";
            configuration.full = false;
            Element serialized = new Element("configuration");
            configuration.writeExternal(serialized);
            var restored = (PracticeRunConfiguration) type.getConfigurationFactories()[0]
                    .createTemplateConfiguration(fixture.getProject());
            restored.readExternal(serialized);
            assertThat(restored.attemptId).isEqualTo(configuration.attemptId);
            assertThat(restored.full).isFalse();
            assertThat(ProgramRunner.getRunner(DefaultRunExecutor.EXECUTOR_ID, restored)).isNotNull();
            restored.attemptId = "../outside";
            assertThatThrownBy(restored::checkConfiguration).isInstanceOf(RuntimeConfigurationError.class);
        });
    }

    @Test
    void toolWindowCreatesAndDisposesNativeContent() throws Exception {
        EdtTestUtil.runInEdtAndWait(() -> {
            var manager = ToolWindowManager.getInstance(fixture.getProject());
            var window = manager.registerToolWindow("Practice integration", true, ToolWindowAnchor.RIGHT);
            try {
                new PracticeToolWindowFactory().createToolWindowContent(fixture.getProject(), window);
                assertThat(window.getContentManager().getContentCount()).isEqualTo(1);
                assertThat(window.getContentManager().getContent(0).getComponent())
                        .isInstanceOf(javax.swing.JPanel.class);
            } finally {
                manager.unregisterToolWindow("Practice integration");
            }
        });
    }
}
