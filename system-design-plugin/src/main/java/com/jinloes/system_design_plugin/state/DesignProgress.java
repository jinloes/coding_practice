package com.jinloes.system_design_plugin.state;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.jinloes.system_design_plugin.catalog.DesignCatalog;
import com.jinloes.system_design_plugin.interview.CopilotInterviewer;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.LongSupplier;

@Service(Service.Level.APP)
@State(name = "SystemDesignPractice", storages = @Storage(
        value = "system-design-practice.xml", roamingType = RoamingType.DISABLED))
public final class DesignProgress implements PersistentStateComponent<DesignProgress.Data> {
    public static final int MIN_TIMEOUT_SECONDS = 30;
    public static final int MAX_TIMEOUT_SECONDS = 1800;
    public static final int DEFAULT_TIMEOUT_SECONDS = 300;
    public static final int MAX_LABEL_CHARS = 80;

    @FunctionalInterface
    public interface Subscription extends AutoCloseable {
        @Override
        void close();
    }

    public static final class Data {
        public Map<String, Entry> attempts = new LinkedHashMap<>();
        public Map<String, String> selectedAttempts = new LinkedHashMap<>();
        public String targetLevel = DesignCatalog.DEFAULT_LEVEL;
        public String cliPath = "";
        public String model = "";
        public int timeoutSeconds = DEFAULT_TIMEOUT_SECONDS;
    }

    public static final class Entry {
        public String problemId = "";
        public String label = "";
        public int hintsRevealed;
        public String lastVerdict = "";
        public String bestVerdict = "";
        public String lastReviewedAt = "";
        public long createdAtMillis;
        public Map<String, Integer> dimensionScores = new LinkedHashMap<>();
        public String scoresVerdict = "";
        public boolean scoresBackfilled;
    }

    private final LongSupplier clock;
    private final CopyOnWriteArrayList<Runnable> listeners = new CopyOnWriteArrayList<>();
    private Data data = new Data();

    public DesignProgress() {
        this(System::currentTimeMillis);
    }

    public DesignProgress(LongSupplier clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public Subscription subscribe(Runnable listener) {
        Objects.requireNonNull(listener, "listener");
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    private void notifyListeners() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    @Override
    public synchronized Data getState() {
        return copy(data);
    }

    @Override
    public void loadState(Data state) {
        Data next = copy(Objects.requireNonNull(state, "state"));
        synchronized (this) {
            data = next;
        }
        notifyListeners();
    }

    public Entry entry(String attemptId, String problemId) {
        boolean created;
        Entry snapshot;
        synchronized (this) {
            created = !data.attempts.containsKey(attemptId);
            snapshot = copy(mutableEntry(attemptId, problemId));
        }
        if (created) {
            notifyListeners();
        }
        return snapshot;
    }

    public synchronized Optional<Entry> findEntry(String attemptId) {
        Entry entry = data.attempts.get(attemptId);
        return entry == null ? Optional.empty() : Optional.of(copy(entry));
    }

    public synchronized long createdAtMillis(String attemptId) {
        Entry entry = data.attempts.get(attemptId);
        return entry == null ? 0L : entry.createdAtMillis;
    }

    public synchronized String selectedAttempt(String problemId) {
        return data.selectedAttempts.get(problemId);
    }

    public void select(String problemId, String attemptId) {
        boolean changed;
        synchronized (this) {
            changed = !Objects.equals(data.selectedAttempts.put(problemId, attemptId), attemptId);
        }
        if (changed) {
            notifyListeners();
        }
    }

    /** Reveals the next hint for an attempt and returns how many are now revealed. */
    public int revealHint(String attemptId, String problemId) {
        int updated;
        boolean changed;
        synchronized (this) {
            Entry entry = mutableEntry(attemptId, problemId);
            int previous = entry.hintsRevealed;
            updated = Math.min(DesignCatalog.HINT_COUNT, previous + 1);
            entry.hintsRevealed = updated;
            changed = updated != previous;
        }
        if (changed) {
            notifyListeners();
        }
        return updated;
    }

    public synchronized int hintsRevealed(String attemptId) {
        Entry entry = data.attempts.get(attemptId);
        return entry == null ? 0 : entry.hintsRevealed;
    }

    public synchronized String attemptLabel(String attemptId) {
        Entry entry = data.attempts.get(attemptId);
        return entry == null ? "" : entry.label;
    }

    /** Sets or clears ({@code ""} or {@code null}) an attempt's custom label and returns the normalized label. */
    public String renameAttempt(String attemptId, String problemId, String label) {
        String sanitized = sanitizeLabel(label);
        boolean changed;
        synchronized (this) {
            Entry entry = mutableEntry(attemptId, problemId);
            changed = !sanitized.equals(entry.label);
            entry.label = sanitized;
        }
        if (changed) {
            notifyListeners();
        }
        return sanitized;
    }

    /** Removes persisted metadata and selection pointers for a deleted attempt. */
    public void removeAttempt(String attemptId) {
        boolean changed;
        synchronized (this) {
            boolean removedEntry = data.attempts.remove(attemptId) != null;
            boolean removedSelection = data.selectedAttempts.values().removeIf(selected -> selected.equals(attemptId));
            changed = removedEntry || removedSelection;
        }
        if (changed) {
            notifyListeners();
        }
    }

    public synchronized String targetLevel() {
        return DesignCatalog.normalizeLevel(data.targetLevel);
    }

    public void setTargetLevel(String level) {
        String normalized = DesignCatalog.normalizeLevel(level);
        boolean changed;
        synchronized (this) {
            changed = !normalized.equals(data.targetLevel);
            data.targetLevel = normalized;
        }
        if (changed) {
            notifyListeners();
        }
    }

    public void recordReview(String attemptId, String problemId, String verdict, Map<String, Integer> scores) {
        LinkedHashMap<String, Integer> sanitized = sanitizeScores(scores);
        String parsedVerdict = validVerdict(verdict);
        synchronized (this) {
            Entry entry = mutableEntry(attemptId, problemId);
            if (!parsedVerdict.isEmpty()
                    && !sanitized.isEmpty()
                    && shouldReplaceScores(entry.dimensionScores, entry.scoresVerdict, parsedVerdict, sanitized)) {
                entry.dimensionScores = sanitized;
                entry.scoresVerdict = parsedVerdict;
            }
            entry.scoresBackfilled = true;
            entry.lastVerdict = verdict == null ? "" : verdict;
            entry.bestVerdict = strongerVerdict(effectiveBestVerdict(entry), parsedVerdict);
            entry.lastReviewedAt = Instant.now().toString();
        }
        notifyListeners();
    }

    public synchronized boolean needsDimensionBackfill(String attemptId) {
        Entry entry = data.attempts.get(attemptId);
        return entry != null
                && !entry.lastReviewedAt.isEmpty()
                && entry.dimensionScores.isEmpty()
                && !entry.scoresBackfilled;
    }

    public synchronized void markDimensionBackfillChecked(String attemptId) {
        Entry entry = data.attempts.get(attemptId);
        if (entry != null) {
            entry.scoresBackfilled = true;
        }
    }

    public boolean backfillDimensionScores(
            String attemptId, String problemId, String verdict, Map<String, Integer> scores) {
        String parsedVerdict = validVerdict(verdict);
        LinkedHashMap<String, Integer> sanitized = sanitizeScores(scores);
        if (parsedVerdict.isEmpty() || sanitized.isEmpty()) {
            return false;
        }
        boolean updated = false;
        synchronized (this) {
            Entry entry = data.attempts.get(attemptId);
            if (entry != null
                    && entry.problemId.equals(problemId)
                    && !entry.lastReviewedAt.isEmpty()
                    && entry.dimensionScores.isEmpty()) {
                entry.dimensionScores = sanitized;
                entry.scoresVerdict = parsedVerdict;
                entry.scoresBackfilled = true;
                entry.bestVerdict = strongerVerdict(effectiveBestVerdict(entry), parsedVerdict);
                updated = true;
            }
        }
        if (updated) {
            notifyListeners();
        }
        return updated;
    }

    /** Problems with at least one attempt that received interviewer feedback. Derived, not stored. */
    public synchronized Set<String> reviewedProblemIds() {
        return bestVerdicts().keySet();
    }

    /**
     * Strongest verdict recorded across reviewed attempts for each reviewed problem ID (using
     * {@link DesignCatalog#VERDICTS} order, or {@code ""} when only unparsed reviews exist).
     */
    public synchronized Map<String, String> bestVerdicts() {
        Map<String, String> verdicts = new LinkedHashMap<>();
        for (Entry entry : data.attempts.values()) {
            if (entry.lastReviewedAt.isEmpty()) {
                continue;
            }
            String candidate = effectiveBestVerdict(entry);
            verdicts.merge(entry.problemId, candidate, DesignProgress::strongerVerdict);
        }
        return Map.copyOf(verdicts);
    }

    /**
     * Best rubric dimension scores ({@code 1..4}) per reviewed problem across attempts that carry parsed
     * scores, picking the strongest {@link Entry#scoresVerdict} and breaking ties by higher average score,
     * then newer {@link Entry#lastReviewedAt}.
     */
    public synchronized Map<String, Map<String, Integer>> problemDimensionScores() {
        Map<String, Entry> bestEntries = new LinkedHashMap<>();
        for (Entry entry : data.attempts.values()) {
            if (entry.lastReviewedAt.isEmpty() || entry.dimensionScores.isEmpty()) {
                continue;
            }
            Entry current = bestEntries.get(entry.problemId);
            if (current == null || isStrongerScoredEntry(entry, current)) {
                bestEntries.put(entry.problemId, entry);
            }
        }
        Map<String, Map<String, Integer>> result = new LinkedHashMap<>();
        bestEntries.forEach((problemId, entry) -> result.put(problemId, Map.copyOf(entry.dimensionScores)));
        return Map.copyOf(result);
    }

    private static boolean isStrongerScoredEntry(Entry candidate, Entry incumbent) {
        int candidateRank = DesignCatalog.VERDICTS.indexOf(candidate.scoresVerdict);
        int incumbentRank = DesignCatalog.VERDICTS.indexOf(incumbent.scoresVerdict);
        if (candidateRank != incumbentRank) {
            return candidateRank < incumbentRank;
        }
        double candidateAvg = averageScore(candidate.dimensionScores);
        double incumbentAvg = averageScore(incumbent.dimensionScores);
        if (Double.compare(candidateAvg, incumbentAvg) != 0) {
            return candidateAvg > incumbentAvg;
        }
        return candidate.lastReviewedAt.compareTo(incumbent.lastReviewedAt) >= 0;
    }

    private static boolean shouldReplaceScores(
            Map<String, Integer> existing,
            String existingVerdict,
            String candidateVerdict,
            Map<String, Integer> candidateScores) {
        if (existing.isEmpty()) {
            return true;
        }
        int candidateRank = DesignCatalog.VERDICTS.indexOf(candidateVerdict);
        int existingRank = DesignCatalog.VERDICTS.indexOf(existingVerdict);
        if (candidateRank != existingRank) {
            return candidateRank < existingRank;
        }
        return averageScore(candidateScores) >= averageScore(existing);
    }

    private static String validVerdict(String verdict) {
        return verdict != null && DesignCatalog.VERDICTS.contains(verdict) ? verdict : "";
    }

    private static String effectiveBestVerdict(Entry entry) {
        return strongerVerdict(validVerdict(entry.bestVerdict), validVerdict(entry.lastVerdict));
    }

    private static int verdictRank(String validVerdict) {
        return validVerdict.isEmpty() ? Integer.MAX_VALUE : DesignCatalog.VERDICTS.indexOf(validVerdict);
    }

    private static String strongerVerdict(String first, String second) {
        return verdictRank(second) < verdictRank(first) ? second : first;
    }

    private static LinkedHashMap<String, Integer> sanitizeScores(Map<String, Integer> scores) {
        LinkedHashMap<String, Integer> clean = new LinkedHashMap<>();
        if (scores == null) {
            return clean;
        }
        for (String dimension : DesignCatalog.DIMENSIONS) {
            Integer score = scores.get(dimension);
            if (score != null && score >= 1 && score <= 4) {
                clean.put(dimension, score);
            }
        }
        return clean;
    }

    private static double averageScore(Map<String, Integer> scores) {
        int sum = 0;
        for (int score : scores.values()) {
            sum += score;
        }
        return (double) sum / scores.size();
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

    public void updateSettings(String cliPath, String model, int timeoutSeconds) {
        validateTimeout(timeoutSeconds);
        synchronized (this) {
            data.cliPath = cliPath == null ? "" : cliPath;
            data.model = model == null ? "" : model;
            data.timeoutSeconds = timeoutSeconds;
        }
        notifyListeners();
    }

    private Entry mutableEntry(String attemptId, String problemId) {
        Entry entry = data.attempts.computeIfAbsent(attemptId, ignored -> {
            Entry created = new Entry();
            created.problemId = problemId;
            created.createdAtMillis = Math.max(0L, clock.getAsLong());
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
        copy.targetLevel = DesignCatalog.normalizeLevel(source.targetLevel);
        copy.cliPath = source.cliPath == null ? "" : source.cliPath;
        copy.model = source.model == null ? "" : source.model;
        copy.timeoutSeconds = source.timeoutSeconds;
        return copy;
    }

    private static Entry copy(Entry source) {
        Entry copy = new Entry();
        copy.problemId = source.problemId == null ? "" : source.problemId;
        copy.label = sanitizeLabel(source.label);
        copy.hintsRevealed = source.hintsRevealed;
        copy.lastVerdict = source.lastVerdict == null ? "" : source.lastVerdict;
        copy.bestVerdict = effectiveBestVerdict(source);
        copy.lastReviewedAt = source.lastReviewedAt == null ? "" : source.lastReviewedAt;
        copy.createdAtMillis = Math.max(0L, source.createdAtMillis);
        LinkedHashMap<String, Integer> cleanScores = sanitizeScores(source.dimensionScores);
        String resolvedScoresVerdict = validVerdict(source.scoresVerdict);
        if (resolvedScoresVerdict.isEmpty()) {
            resolvedScoresVerdict = validVerdict(copy.lastVerdict);
        }
        if (cleanScores.isEmpty() || resolvedScoresVerdict.isEmpty()) {
            copy.dimensionScores = new LinkedHashMap<>();
            copy.scoresVerdict = "";
        } else {
            copy.dimensionScores = cleanScores;
            copy.scoresVerdict = resolvedScoresVerdict;
        }
        copy.scoresBackfilled = source.scoresBackfilled || !copy.dimensionScores.isEmpty();
        return copy;
    }

    private static String sanitizeLabel(String label) {
        if (label == null) {
            return "";
        }
        String cleaned = label.replaceAll("[\\p{Cntrl}]+", " ").strip();
        return cleaned.length() <= MAX_LABEL_CHARS ? cleaned : cleaned.substring(0, MAX_LABEL_CHARS).stripTrailing();
    }
}
