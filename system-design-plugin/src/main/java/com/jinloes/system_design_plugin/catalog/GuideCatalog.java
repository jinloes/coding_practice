package com.jinloes.system_design_plugin.catalog;

import com.google.gson.JsonObject;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Bundled architectural concept and interview delivery guides rendered inside the tool window.
 */
public final class GuideCatalog {
    private static final List<Guide> GUIDES = load(GuideCatalog.class::getResourceAsStream, DesignCatalog::exists);

    private static final Map<String, Guide> BY_ID = GUIDES.stream()
            .collect(Collectors.toUnmodifiableMap(Guide::id, guide -> guide));

    private GuideCatalog() {
    }

    public record Guide(String id, String title, String summary, String body, List<String> relatedProblemIds) {
        public Guide {
            id = Objects.requireNonNull(id, "id");
            title = Objects.requireNonNull(title, "title");
            summary = Objects.requireNonNull(summary, "summary");
            body = Objects.requireNonNull(body, "body");
            relatedProblemIds = List.copyOf(relatedProblemIds);
        }
    }

    public static List<Guide> all() {
        return GUIDES;
    }

    public static Guide find(String id) {
        Guide guide = BY_ID.get(id);
        if (guide == null) {
            throw new IllegalArgumentException("Unknown guide ID: " + id);
        }
        return guide;
    }

    static List<Guide> load(Function<String, InputStream> opener, Predicate<String> problemExists) {
        return DesignCatalog.readIndex("/guides/index.txt", opener).stream()
                .map(id -> parse(id, DesignCatalog.read("/guides/" + id + ".json", opener), problemExists))
                .toList();
    }

    static Guide parse(String id, String json, Predicate<String> problemExists) {
        JsonObject manifest = DesignCatalog.parseObject("guide", id, json);
        if (!id.equals(DesignCatalog.text(manifest, id, "id"))) {
            throw new IllegalStateException("Guide manifest ID does not match its file name: " + id);
        }
        List<String> related = TrackCatalog.validateProblemRefs("Guide " + id,
                DesignCatalog.texts(manifest, id, "relatedProblemIds"), problemExists);
        return new Guide(id, DesignCatalog.text(manifest, id, "title"),
                DesignCatalog.text(manifest, id, "summary"),
                DesignCatalog.text(manifest, id, "body"), related);
    }
}
