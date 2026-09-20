package com.jinloes.practice_plugin.state;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
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
}
