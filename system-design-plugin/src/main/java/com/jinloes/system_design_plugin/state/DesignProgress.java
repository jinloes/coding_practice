package com.jinloes.system_design_plugin.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.jinloes.system_design_plugin.catalog.DesignCatalog;
import com.jinloes.system_design_plugin.interview.CopilotInterviewer;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service(Service.Level.APP)
@State(name = "SystemDesignPractice", storages = @Storage(
        value = "system-design-practice.xml", roamingType = RoamingType.DISABLED))
public final class DesignProgress implements PersistentStateComponent<DesignProgress.Data> {
    public static final int MIN_TIMEOUT_SECONDS = 30;
    public static final int MAX_TIMEOUT_SECONDS = 1800;
    public static final int DEFAULT_TIMEOUT_SECONDS = 300;

    public static final class Data {
        public Map<String, Entry> attempts = new LinkedHashMap<>();
        public Map<String, String> selectedAttempts = new LinkedHashMap<>();
        public String cliPath = "";
        public String model = "";
        public int timeoutSeconds = DEFAULT_TIMEOUT_SECONDS;
    }

    public static final class Entry {
        public String problemId = "";
        public int hintsRevealed;
        public String lastVerdict = "";
        public String lastReviewedAt = "";
    }

    private Data data = new Data();

    public static DesignProgress get() {
        return ApplicationManager.getApplication().getService(DesignProgress.class);
    }

    @Override
    public synchronized @NotNull Data getState() {
        return copy(data);
    }

    @Override
    public synchronized void loadState(@NotNull Data state) {
        data = copy(state);
    }

    public synchronized Entry entry(String attemptId, String problemId) {
        return copy(mutableEntry(attemptId, problemId));
    }

    public synchronized String selectedAttempt(String problemId) {
        return data.selectedAttempts.get(problemId);
    }

    public synchronized void select(String problemId, String attemptId) {
        data.selectedAttempts.put(problemId, attemptId);
    }

    /** Reveals the next hint for an attempt and returns how many are now revealed. */
    public synchronized int revealHint(String attemptId, String problemId) {
        Entry entry = mutableEntry(attemptId, problemId);
        entry.hintsRevealed = Math.min(DesignCatalog.HINT_COUNT, entry.hintsRevealed + 1);
        return entry.hintsRevealed;
    }

    public synchronized int hintsRevealed(String attemptId) {
        Entry entry = data.attempts.get(attemptId);
        return entry == null ? 0 : entry.hintsRevealed;
    }

    public synchronized void recordReview(String attemptId, String problemId, String verdict) {
        Entry entry = mutableEntry(attemptId, problemId);
        entry.lastVerdict = verdict == null ? "" : verdict;
        entry.lastReviewedAt = Instant.now().toString();
    }

    /** Problems with at least one attempt that received interviewer feedback. Derived, not stored. */
    public synchronized Set<String> reviewedProblemIds() {
        return data.attempts.values().stream()
                .filter(entry -> !entry.lastReviewedAt.isEmpty())
                .map(entry -> entry.problemId)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static void validateTimeout(int seconds) {
        if (seconds < MIN_TIMEOUT_SECONDS || seconds > MAX_TIMEOUT_SECONDS) {
            throw new IllegalArgumentException("Use a timeout of " + MIN_TIMEOUT_SECONDS + "-"
                    + MAX_TIMEOUT_SECONDS + " seconds.");
        }
    }

    /** The configured timeout, falling back to the default when persisted state is out of range. */
    public synchronized int timeoutSeconds() {
        int seconds = data.timeoutSeconds;
        return seconds < MIN_TIMEOUT_SECONDS || seconds > MAX_TIMEOUT_SECONDS ? DEFAULT_TIMEOUT_SECONDS : seconds;
    }

    public synchronized CopilotInterviewer.Settings settings() {
        return new CopilotInterviewer.Settings(data.cliPath, data.model, timeoutSeconds());
    }

    public synchronized void updateSettings(String cliPath, String model, int timeoutSeconds) {
        validateTimeout(timeoutSeconds);
        data.cliPath = cliPath == null ? "" : cliPath;
        data.model = model == null ? "" : model;
        data.timeoutSeconds = timeoutSeconds;
    }

    private Entry mutableEntry(String attemptId, String problemId) {
        Entry entry = data.attempts.computeIfAbsent(attemptId, ignored -> {
            Entry created = new Entry();
            created.problemId = problemId;
            return created;
        });
        if (!entry.problemId.equals(problemId)) {
            throw new IllegalArgumentException("Attempt is already associated with another problem.");
        }
        return entry;
    }

    private static Data copy(Data source) {
        Data copy = new Data();
        if (source.attempts != null) {
            source.attempts.forEach((id, entry) -> {
                if (entry != null) {
                    copy.attempts.put(id, copy(entry));
                }
            });
        }
        if (source.selectedAttempts != null) {
            copy.selectedAttempts.putAll(source.selectedAttempts);
        }
        copy.cliPath = source.cliPath == null ? "" : source.cliPath;
        copy.model = source.model == null ? "" : source.model;
        copy.timeoutSeconds = source.timeoutSeconds;
        return copy;
    }

    private static Entry copy(Entry source) {
        Entry copy = new Entry();
        copy.problemId = source.problemId == null ? "" : source.problemId;
        copy.hintsRevealed = source.hintsRevealed;
        copy.lastVerdict = source.lastVerdict == null ? "" : source.lastVerdict;
        copy.lastReviewedAt = source.lastReviewedAt == null ? "" : source.lastReviewedAt;
        return copy;
    }
}
