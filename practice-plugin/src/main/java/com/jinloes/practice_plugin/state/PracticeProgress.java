package com.jinloes.practice_plugin.state;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import com.jinloes.practice_plugin.run.CheckResult;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

@Service(Service.Level.PROJECT)
@State(name = "AlgorithmPractice", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public final class PracticeProgress implements PersistentStateComponent<PracticeProgress.Data> {
    public static final class Data {
        public Map<String, Entry> attempts = new LinkedHashMap<>();
        public Map<String, String> selectedAttempts = new LinkedHashMap<>();
        public int testSeconds = 5;
        public int suiteSeconds = 60;
        public int heapMb = 256;
    }

    public static final class Entry {
        public String exerciseId = "";
        public String revision = "1";
        public String status = "NOT_RUN";
        public String checkedFingerprint = "";
        public String checkedAt = "";
        public String lastPassedAt = "";
        public String details = "";
        public int hintsRevealed;
        public int tests;
        public int failures;
    }

    private Data data = new Data();

    public static PracticeProgress get(Project project) {
        return project.getService(PracticeProgress.class);
    }

    @Override
    public @NotNull Data getState() {
        return data;
    }

    @Override
    public void loadState(@NotNull Data state) {
        data = state;
    }

    public Entry entry(String attemptId, String exerciseId) {
        return data.attempts.computeIfAbsent(attemptId, ignored -> {
            Entry entry = new Entry();
            entry.exerciseId = exerciseId;
            return entry;
        });
    }

    public void record(String attemptId, String exerciseId, String fingerprint, CheckResult result) {
        Entry entry = entry(attemptId, exerciseId);
        entry.checkedFingerprint = fingerprint;
        entry.checkedAt = java.time.Instant.now().toString();
        entry.status = result.status().name();
        entry.details = result.details();
        entry.tests = result.tests();
        entry.failures = result.failures();
        if (result.status() == CheckResult.Status.PASSED) {
            entry.lastPassedAt = entry.checkedAt;
        }
    }

    public void validateLimits() {
        if (data.testSeconds < 1 || data.testSeconds > 300
                || data.suiteSeconds < data.testSeconds || data.suiteSeconds > 1800
                || data.heapMb < 64 || data.heapMb > 2048) {
            throw new IllegalArgumentException(
                    "Use 1-300 seconds per test, at least that long and at most 1800 seconds per suite, and 64-2048 MiB.");
        }
    }
}
