package com.jinloes.practice_plugin.run;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.util.execution.ParametersListUtil;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExampleRunConfigurationsTest {
    @TempDir Path temporary;
    private IdeaProjectTestFixture fixture;

    @BeforeEach
    void setUp() throws Exception {
        Path host = temporary.resolve("host");
        Files.createDirectories(host);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("example-run-configurations", host, false).getFixture();
            fixture.setUp();
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        if (fixture != null) {
            EdtTestUtil.runInEdtAndWait(() -> {
                PracticeModuleWorkspace.get(fixture.getProject()).dispose();
                fixture.tearDown();
            });
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "[2, 7, 11, 15] 9",
            "racecar",
            "[1,2]|3",
            "\"ab\"",
            "\"hello world\"",
            "a\\nb",
            "\\\\",
            "a\\\"b",
            "a\\",
            "a b\\"
    })
    void installEncodesCustomInputSoParametersListUtilRoundTripsSingleArgument(String input) throws Exception {
        var exercise = ExerciseCatalog.find("pair-sum");
        var attempt = ManagedPracticeWorkspace.get().create(exercise);
        PracticeModuleWorkspace modules = PracticeModuleWorkspace.get(fixture.getProject());

        RunnerAndConfigurationSettings settings = EdtTestUtil.runInEdtAndGet(() -> {
            var module = modules.open(attempt);
            Path runnerPath = modules.prepareExampleHarness(attempt, exercise);
            var runnerFile = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(runnerPath);
            var javaFile = (PsiJavaFile) PsiManager.getInstance(fixture.getProject()).findFile(runnerFile);
            return ExampleRunConfigurations.install(
                    fixture.getProject(), attempt, attempt.id(), javaFile, module, input, false);
        });

        var configuration = (ApplicationConfiguration) settings.getConfiguration();
        assertThat(ParametersListUtil.parse(configuration.getProgramParameters()))
                .as("parsed argv for input <%s>", input)
                .isEqualTo(List.of(input));
        if ("[2, 7, 11, 15] 9".equals(input)) {
            assertThat(configuration.getProgramParameters()).isEqualTo("\"[2, 7, 11, 15] 9\"");
        }
    }

    @Test
    void installOmitsProgramParametersWhenInputIsEmpty() throws Exception {
        var exercise = ExerciseCatalog.find("pair-sum");
        var attempt = ManagedPracticeWorkspace.get().create(exercise);
        PracticeModuleWorkspace modules = PracticeModuleWorkspace.get(fixture.getProject());

        RunnerAndConfigurationSettings settings = EdtTestUtil.runInEdtAndGet(() -> {
            var module = modules.open(attempt);
            Path runnerPath = modules.prepareExampleHarness(attempt, exercise);
            var runnerFile = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(runnerPath);
            var javaFile = (PsiJavaFile) PsiManager.getInstance(fixture.getProject()).findFile(runnerFile);
            return ExampleRunConfigurations.install(
                    fixture.getProject(), attempt, attempt.id(), javaFile, module, "", true);
        });

        var configuration = (ApplicationConfiguration) settings.getConfiguration();
        assertThat(configuration.getProgramParameters()).isNull();
    }
}
