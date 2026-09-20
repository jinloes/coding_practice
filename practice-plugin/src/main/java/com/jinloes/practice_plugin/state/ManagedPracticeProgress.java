package com.jinloes.practice_plugin.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.jinloes.practice_plugin.run.CheckResult;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service(Service.Level.APP)
@State(name = "AlgorithmPracticeManaged", storages = @Storage(
        value = "algorithm-practice-managed.xml", roamingType = RoamingType.DISABLED))
public final class ManagedPracticeProgress implements PersistentStateComponent<ManagedPracticeProgress.Data> {
    public static final class Data {
        public Map<String, Entry> attempts = new LinkedHashMap<>();
        public Map<String, String> selectedAttempts = new LinkedHashMap<>();
        public Map<String, String> legacyMappings = new LinkedHashMap<>();
        public int testSeconds = 5;
        public int suiteSeconds = 60;
        public int heapMb = 256;
    }

    public static final class Entry {
        public String exerciseId = "";
        public String revision = "1";
        public String status = CheckResult.Status.NOT_RUN.name();
        public String checkedFingerprint = "";
        public String checkedAt = "";
        public String lastPassedAt = "";
        public String details = "";
        public String complexity = "";
        public int hintsRevealed;
        public int tests;
        public int failures;
    }

    private Data data = new Data();

    public static ManagedPracticeProgress get() {
        return ApplicationManager.getApplication().getService(ManagedPracticeProgress.class);
    }

    @Override
    public @NotNull Data getState() {
        return data;
    }

    @Override
    public void loadState(@NotNull Data state) {
        if (state.attempts == null) {
            state.attempts = new LinkedHashMap<>();
        }
        if (state.selectedAttempts == null) {
            state.selectedAttempts = new LinkedHashMap<>();
        }
        if (state.legacyMappings == null) {
            state.legacyMappings = new LinkedHashMap<>();
        }
        data = state;
    }

    public Entry entry(String attemptId, String exerciseId) {
        Entry entry = data.attempts.computeIfAbsent(attemptId, ignored -> {
            Entry created = new Entry();
            created.exerciseId = exerciseId;
            return created;
        });
        if (!entry.exerciseId.equals(exerciseId)) {
            throw new IllegalArgumentException("Attempt is already associated with another exercise.");
        }
        return entry;
    }

    public void record(String attemptId, String exerciseId, String fingerprint, CheckResult result) {
        Entry entry = entry(attemptId, exerciseId);
        entry.checkedFingerprint = fingerprint;
        entry.checkedAt = Instant.now().toString();
        entry.status = result.status().name();
        entry.details = result.details();
        if (!result.complexity().isBlank()) {
            entry.complexity = result.complexity();
        }
        entry.tests = result.tests();
        entry.failures = result.failures();
        if (result.status() == CheckResult.Status.PASSED) {
            entry.lastPassedAt = entry.checkedAt;
        }
    }

    public String importedAttempt(String legacyRoot, String legacyAttemptId) {
        return data.legacyMappings.get(legacyKey(legacyRoot, legacyAttemptId));
    }

    public void recordImport(String legacyRoot, String legacyAttemptId, String managedAttemptId) {
        data.legacyMappings.putIfAbsent(legacyKey(legacyRoot, legacyAttemptId), managedAttemptId);
    }

    public void validateLimits() {
        if (data.testSeconds < 1 || data.testSeconds > 300
                || data.suiteSeconds < data.testSeconds || data.suiteSeconds > 1800
                || data.heapMb < 64 || data.heapMb > 2048) {
            throw new IllegalArgumentException(
                    "Use 1-300 seconds per test, at least that long and at most 1800 seconds per suite, and 64-2048 MiB.");
        }
    }

    private static String legacyKey(String legacyRoot, String legacyAttemptId) {
        return legacyRoot + "\n" + legacyAttemptId;
    }
}
