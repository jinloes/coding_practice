package com.jinloes.system_design_plugin.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.jinloes.system_design_plugin.catalog.DesignCatalog;
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
        if (state.cliPath == null) {
            state.cliPath = "";
        }
        if (state.model == null) {
            state.model = "";
        }
        data = state;
    }

    public Entry entry(String attemptId, String problemId) {
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

    public String selectedAttempt(String problemId) {
        return data.selectedAttempts.get(problemId);
    }

    public void select(String problemId, String attemptId) {
        data.selectedAttempts.put(problemId, attemptId);
    }

    /** Reveals the next hint for an attempt and returns how many are now revealed. */
    public int revealHint(String attemptId, String problemId) {
        Entry entry = entry(attemptId, problemId);
        entry.hintsRevealed = Math.min(DesignCatalog.HINT_COUNT, entry.hintsRevealed + 1);
        return entry.hintsRevealed;
    }

    public void recordReview(String attemptId, String problemId, String verdict) {
        Entry entry = entry(attemptId, problemId);
        entry.lastVerdict = verdict == null ? "" : verdict;
        entry.lastReviewedAt = Instant.now().toString();
    }

    /** Problems with at least one attempt that received interviewer feedback. Derived, not stored. */
    public Set<String> reviewedProblemIds() {
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
    public int timeoutSeconds() {
        int seconds = data.timeoutSeconds;
        return seconds < MIN_TIMEOUT_SECONDS || seconds > MAX_TIMEOUT_SECONDS ? DEFAULT_TIMEOUT_SECONDS : seconds;
    }
}
