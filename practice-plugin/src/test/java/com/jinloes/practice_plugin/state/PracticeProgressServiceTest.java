package com.jinloes.practice_plugin.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PracticeProgressServiceTest {
    @Test
    void projectServiceAccessorReturnsTheProjectScopedProgress() throws Exception {
        IdeaProjectTestFixture fixture = EdtTestUtil.runInEdtAndGet(() -> {
            IdeaProjectTestFixture created = IdeaTestFixtureFactory.getFixtureFactory()
                    .createLightFixtureBuilder("legacy-progress-service").getFixture();
            created.setUp();
            return created;
        });
        try {
            PracticeProgress progress = PracticeProgress.get(fixture.getProject());
            ManagedPracticeProgress managed = ManagedPracticeProgress.get();

            PracticeProgress.Entry entry = progress.entry("pair-sum-first", "pair-sum");

            assertThat(entry.exerciseId).isEqualTo("pair-sum");
            assertThat(PracticeProgress.get(fixture.getProject())).isSameAs(progress);
            assertThat(managed).isSameAs(ApplicationManager.getApplication().getService(ManagedPracticeProgress.class));
        } finally {
            EdtTestUtil.runInEdtAndWait(fixture::tearDown);
        }
    }
}
