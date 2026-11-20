package com.jinloes.system_design_plugin.catalog;

import com.google.gson.JsonObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Curated practice tracks grouping bundled system design problems by architectural domain, with a
 * reverse index from problem ID to containing track IDs.
 */
public final class TrackCatalog {
    private static final List<Track> TRACKS = load(TrackCatalog.class::getResourceAsStream, DesignCatalog::exists);

    private static final Map<String, Track> BY_ID = TRACKS.stream()
            .collect(Collectors.toUnmodifiableMap(Track::id, track -> track));

    private static final Map<String, List<String>> BY_PROBLEM = indexByProblem(TRACKS);

    private TrackCatalog() {
    }

    public record Track(String id, String title, String summary, List<String> problemIds) {
        public Track {
            id = Objects.requireNonNull(id, "id");
            title = Objects.requireNonNull(title, "title");
            summary = Objects.requireNonNull(summary, "summary");
            problemIds = List.copyOf(problemIds);
        }
    }

    public static List<Track> all() {
        return TRACKS;
    }

    public static Track find(String id) {
        Track track = BY_ID.get(id);
        if (track == null) {
            throw new IllegalArgumentException("Unknown track ID: " + id);
        }
        return track;
    }

    /** Track IDs that include {@code problemId}, in track catalog order. */
    public static List<String> containing(String problemId) {
        return BY_PROBLEM.getOrDefault(problemId, List.of());
    }

    static List<Track> load(Function<String, InputStream> opener, Predicate<String> problemExists) {
        return DesignCatalog.readIndex("/tracks/index.txt", opener).stream()
                .map(id -> parse(id, DesignCatalog.read("/tracks/" + id + ".json", opener), problemExists))
                .toList();
    }

    static Track parse(String id, String json, Predicate<String> problemExists) {
        JsonObject manifest = DesignCatalog.parseObject("track", id, json);
        if (!id.equals(DesignCatalog.text(manifest, id, "id"))) {
            throw new IllegalStateException("Track manifest ID does not match its file name: " + id);
        }
        List<String> problems = validateProblemRefs("Track " + id,
                DesignCatalog.texts(manifest, id, "problemIds"), problemExists);
        return new Track(id, DesignCatalog.text(manifest, id, "title"),
                DesignCatalog.text(manifest, id, "summary"), problems);
    }

    static List<String> validateProblemRefs(String owner, List<String> problemIds, Predicate<String> problemExists) {
        if (problemIds.isEmpty()) {
            throw new IllegalStateException(owner + " must reference at least one problem");
        }
        if (problemIds.stream().distinct().count() != problemIds.size()) {
            throw new IllegalStateException(owner + " lists duplicate problem IDs");
        }
        for (String problemId : problemIds) {
            if (!problemExists.test(problemId)) {
                throw new IllegalStateException(owner + " references unknown problem " + problemId);
            }
        }
        return problemIds;
    }

    private static Map<String, List<String>> indexByProblem(List<Track> tracks) {
        Map<String, List<String>> mutable = new HashMap<>();
        for (Track track : tracks) {
            for (String problemId : track.problemIds()) {
                mutable.computeIfAbsent(problemId, ignored -> new ArrayList<>()).add(track.id());
            }
        }
        Map<String, List<String>> immutable = new HashMap<>();
        mutable.forEach((problemId, ids) -> immutable.put(problemId, List.copyOf(ids)));
        return Map.copyOf(immutable);
    }
}
