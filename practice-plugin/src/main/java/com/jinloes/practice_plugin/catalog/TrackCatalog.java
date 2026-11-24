package com.jinloes.practice_plugin.catalog;

import com.jinloes.practice_plugin.catalog.GuideCatalog.Guide;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Practice tracks: curated lists of bundled exercises, such as original versions of a widely shared
 * interview list or the patterns commonly reported in one company's interviews, plus notes on what to
 * expect. Tracks are hand curated, not official or verified question lists. A track uses the study
 * guide manifest schema and validation, so it is represented as a {@link Guide} whose lesson holds the
 * track notes.
 */
public final class TrackCatalog {
    /** The resource listing which tracks exist, in the order the UI lists them. */
    static final String INDEX = "/tracks/index.txt";

    private static final List<Guide> TRACKS = indexedIds().stream()
            .map(id -> GuideCatalog.parse(id, read("/tracks/" + id + ".json"), ExerciseCatalog::find))
            .toList();

    private static final Map<String, Guide> BY_ID = TRACKS.stream()
            .collect(Collectors.toUnmodifiableMap(Guide::id, track -> track));

    private static final Map<String, List<Guide>> BY_EXERCISE = GuideCatalog.indexByExercise(TRACKS);

    private TrackCatalog() {
    }

    static List<String> indexedIds() {
        return ExerciseCatalog.indexedIds(read(INDEX));
    }

    public static List<Guide> all() {
        return TRACKS;
    }

    public static boolean isTrack(String id) {
        return BY_ID.containsKey(id);
     }

    public static Guide find(String id) {
        Guide track = BY_ID.get(id);
        if (track == null) {
            throw new IllegalArgumentException("Unknown track ID: " + id);
        }
        return track;
    }

    /** The tracks that include {@code exerciseId}, in track order. */
    public static List<Guide> containing(String exerciseId) {
        return BY_EXERCISE.getOrDefault(exerciseId, List.of());
    }

    static String read(String path) {
        return read(path, TrackCatalog.class::getResourceAsStream);
    }

    static String read(String path, ExerciseCatalog.ResourceOpener opener) {
        try (InputStream input = opener.open(path)) {
            if (input == null) {
                throw new IllegalStateException("Missing track resource: " + path);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read track resource: " + path, exception);
        }
    }
}
