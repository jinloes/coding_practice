package com.jinloes.practice_plugin.workspace;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class PracticeModuleWorkspaceSdkTest {
    @TempDir Path temporary;
    private IdeaProjectTestFixture fixture;
    private ManagedPracticeWorkspace attempts;

    @BeforeEach
    void setUp() throws Exception {
        Path host = temporary.resolve("host");
        Files.createDirectories(host);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("managed-module-sdks", host, false).getFixture();
            fixture.setUp();
            WriteAction.run(() -> ProjectRootManager.getInstance(fixture.getProject()).setProjectSdk(null));
        });
        attempts = new ManagedPracticeWorkspace(temporary.resolve("config"));
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


    @Test
    void reopensADisposedCachedModuleAndReportsMissingSourceRoots() throws Exception {
        var attempt = attempts.create(ExerciseCatalog.find("pair-sum"));
        var support = PracticeModuleWorkspace.get(fixture.getProject());
        var first = EdtTestUtil.runInEdtAndGet(() -> support.open(attempt));
        EdtTestUtil.runInEdtAndWait(() -> WriteAction.run(() ->
                ModuleManager.getInstance(fixture.getProject()).disposeModule(first)));
        EdtTestUtil.runInEdtAndWait(() -> support.clearExampleHarness(attempt));
        new GeneratedDirectories(fixture.getProject()).deleteTree(generatedBase("module-output").resolve(attempt.id()));

        var reopened = EdtTestUtil.runInEdtAndGet(() -> support.open(attempt));

        assertThat(reopened).isNotSameAs(first);
        assertThat(reopened.isDisposed()).isFalse();

        var broken = attempts.create(ExerciseCatalog.find("binary-search"));
        deleteTree(broken.solution().getParent().getParent().getParent().getParent());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> EdtTestUtil.runInEdtAndGet(() -> support.open(broken)))
                .hasMessageContaining("Cannot prepare the managed Java module");
    }


    @Test
    void disposeToleratesAnOwnedFallbackSdkAlreadyRemovedFromTheProjectTable() throws Exception {
        var attempt = attempts.create(ExerciseCatalog.find("pair-sum"));
        var support = PracticeModuleWorkspace.get(fixture.getProject());
        var module = EdtTestUtil.runInEdtAndGet(() -> support.open(attempt));
        String fallbackName = ModuleRootManager.getInstance(module).getSdk().getName();
        Sdk fallback = ProjectJdkTable.getInstance().findJdk(fallbackName);
        EdtTestUtil.runInEdtAndWait(() -> WriteAction.run(() -> ProjectJdkTable.getInstance().removeJdk(fallback)));

        EdtTestUtil.runInEdtAndWait(support::dispose);

        assertThat(ProjectJdkTable.getInstance().findJdk(fallbackName)).isNull();
    }

    @Test
    void ignoresRegisteredJavaSdksWithoutAHomeOrAModernVersionBeforeFallingBackToTheRuntimeJdk() throws Exception {
        var attempt = attempts.create(ExerciseCatalog.find("pair-sum"));
        String noHomeName = "Managed practice no home JDK " + System.nanoTime();
        String oldVersionName = "Managed practice old JDK " + System.nanoTime();
        Sdk noHome = JavaSdk.getInstance().createJdk(noHomeName, System.getProperty("java.home"), false);
        Sdk oldVersion = JavaSdk.getInstance().createJdk(oldVersionName, System.getProperty("java.home"), false);
        try {
            EdtTestUtil.runInEdtAndWait(() -> WriteAction.run(() -> {
                var noHomeModel = noHome.getSdkModificator();
                noHomeModel.setHomePath(null);
                noHomeModel.commitChanges();
                var oldVersionModel = oldVersion.getSdkModificator();
                oldVersionModel.setVersionString("1.8");
                oldVersionModel.commitChanges();
                ProjectJdkTable.getInstance().addJdk(noHome);
                ProjectJdkTable.getInstance().addJdk(oldVersion);
            }));

            var module = EdtTestUtil.runInEdtAndGet(() -> PracticeModuleWorkspace.get(fixture.getProject()).open(attempt));

            assertThat(ModuleRootManager.getInstance(module).getSdk()).isNotIn(noHome, oldVersion);
            assertThat(Path.of(ModuleRootManager.getInstance(module).getSdk().getHomePath()))
                    .isEqualTo(Path.of(System.getProperty("java.home")));
        } finally {
            removeJdk(noHomeName);
            removeJdk(oldVersionName);
        }
    }

    private Path generatedBase(String kind) {
        return Path.of(PathManager.getSystemPath(), "algorithm-practice", kind,
                Integer.toUnsignedString(fixture.getProject().getLocationHash().hashCode()))
                .toAbsolutePath().normalize();
    }

    private static void deleteTree(Path root) throws Exception {
        if (!Files.exists(root)) {
            return;
        }
        try (var paths = Files.walk(root)) {
            for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    private static void removeJdk(String name) throws Exception {
        EdtTestUtil.runInEdtAndWait(() -> {
            Sdk remaining = ProjectJdkTable.getInstance().findJdk(name);
            if (remaining != null) {
                WriteAction.run(() -> ProjectJdkTable.getInstance().removeJdk(remaining));
            }
        });
    }
}
