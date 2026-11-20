package com.jinloes.system_design_plugin.catalog;

import com.google.gson.JsonParser;
import com.jinloes.system_design_plugin.catalog.DesignCatalog.Problem;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DesignCatalogTest {

    @Test
    void loadsEveryManifestAndOnlyListedOnes() throws IOException {
        String directory = System.getProperty("design.problems.dir");
        assertThat(directory).as("design.problems.dir is set by the Gradle test task").isNotBlank();
        List<String> manifests;
        try (var files = Files.list(Path.of(directory))) {
            manifests = files.map(path -> path.getFileName().toString())
                    .filter(name -> name.endsWith(".json"))
                    .map(name -> name.substring(0, name.length() - ".json".length()))
                    .toList();
        }
        assertThat(manifests).isNotEmpty();
        assertThat(DesignCatalog.all()).extracting(Problem::id).containsExactlyInAnyOrderElementsOf(manifests);
        assertThat(DesignCatalog.all()).extracting(Problem::id).doesNotHaveDuplicates();
        assertThat(DesignCatalog.all()).extracting(Problem::title).doesNotHaveDuplicates();
        for (Problem problem : DesignCatalog.all()) {
            var manifest = JsonParser.parseString(resource("/problems/" + problem.id() + ".json")).getAsJsonObject();
            assertThat(problem.title()).isEqualTo(manifest.get("title").getAsString());
            assertThat(problem.expectations()).hasSize(manifest.getAsJsonArray("expectations").size())
                    .hasSizeGreaterThanOrEqualTo(DesignCatalog.MIN_EXPECTATIONS);
            assertThat(problem.hints()).hasSize(DesignCatalog.HINT_COUNT);
            assertThat(DesignCatalog.DIFFICULTIES).contains(problem.difficulty());
            assertThat(problem.summary()).isNotBlank();
            assertThat(problem.statement()).isNotBlank();
            assertThat(DesignCatalog.find(problem.id())).isSameAs(problem);
            assertThat(DesignCatalog.exists(problem.id())).isTrue();
        }
    }

    @Test
    void exposesTheFixedRubricLevelsAndNotesTemplate() {
        assertThat(DesignCatalog.DIMENSIONS).hasSize(7).doesNotHaveDuplicates();
        assertThat(DesignCatalog.VERDICTS).hasSize(5).doesNotHaveDuplicates();
        assertThat(DesignCatalog.LEVELS).containsExactly("Mid-Level", "Senior", "Staff+");
        assertThat(DesignCatalog.normalizeLevel("Staff+")).isEqualTo("Staff+");
        assertThat(DesignCatalog.normalizeLevel(null)).isEqualTo("Senior");
        assertThat(DesignCatalog.normalizeLevel("Principal")).isEqualTo("Senior");
        assertThat(DesignCatalog.notesTemplate())
                .startsWith("# Design notes")
                .contains("## 1. Requirements and scope")
                .contains("## 2. Core entities")
                .contains("## 3. API and system interface")
                .contains("## 5. Deep dives");
    }

    @Test
    void rejectsUnknownIds() {
        assertThat(DesignCatalog.exists("missing")).isFalse();
        assertThatThrownBy(() -> DesignCatalog.find("missing"))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("missing");
    }

    @Test
    void rejectsInvalidManifestsNamingTheProblem() {
        assertThat(DesignCatalog.parse("demo", manifest("demo", "Easy", 3, 5)).id()).as("valid control").isEqualTo("demo");
        assertInvalid(manifest("other", "Easy", 3, 5));
        assertInvalid(manifest("demo", "Expert", 3, 5));
        assertInvalid(manifest("demo", "Easy", 2, 5));
        assertInvalid(manifest("demo", "Easy", 4, 5));
        assertInvalid(manifest("demo", "Easy", 3, 4));
        assertInvalid(manifest("demo", "Easy", 3, 5).replace("\"title\": \"T\"", "\"title\": \" \""));
        assertInvalid(manifest("demo", "Easy", 3, 5).replace("\"title\": \"T\"", "\"title\": 123"));
        assertInvalid(manifest("demo", "Easy", 3, 5).replace("\"title\": \"T\"", "\"title\": null"));
        assertInvalid(manifest("demo", "Easy", 3, 5).replace("\"hints\": [", "\"hints\": [1, "));
        assertInvalid(manifest("demo", "Easy", 3, 5).replace("\"hints\": [", "\"hints\": [null, "));
        assertInvalid(manifest("demo", "Easy", 3, 5).replace("\"hints\": [", "\"hints\": [\" \", "));
        assertInvalid(manifest("demo", "Easy", 3, 5).replace("\"expectations\": [", "\"other\": ["));
        assertInvalid("not json");
        assertInvalid("[]");
    }

    @Test
    void missingInvalidOrUnreadableResourcesNameThePath() {
        assertThatThrownBy(() -> DesignCatalog.load(path -> null))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("/problems/index.txt");
        assertThatThrownBy(() -> DesignCatalog.load(path -> "/problems/index.txt".equals(path)
                ? new ByteArrayInputStream("url-shortener\n".getBytes(StandardCharsets.UTF_8)) : null))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("url-shortener");
        assertThatThrownBy(() -> DesignCatalog.readIndex("/empty",
                path -> new ByteArrayInputStream("# comment only\n  \n".getBytes(StandardCharsets.UTF_8))))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("/empty");
        assertThatThrownBy(() -> DesignCatalog.readIndex("/dup",
                path -> new ByteArrayInputStream("a\na\n".getBytes(StandardCharsets.UTF_8))))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("/dup");
        assertThatThrownBy(() -> DesignCatalog.read("/problems/rate-limiter.json", path -> new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("boom");
            }
        })).isInstanceOf(IllegalStateException.class).hasMessageContaining("rate-limiter");
        assertThat(DesignCatalog.read("/x", path -> new ByteArrayInputStream("ok".getBytes(StandardCharsets.UTF_8))))
                .isEqualTo("ok");
    }

    private static void assertInvalid(String json) {
        assertThatThrownBy(() -> DesignCatalog.parse("demo", json))
                .as("manifest %s", json)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("demo");
    }

    private static String manifest(String id, String difficulty, int hints, int expectations) {
        return "{\"id\": \"" + id + "\", \"title\": \"T\", \"difficulty\": \"" + difficulty + "\", "
                + "\"summary\": \"S\", \"statement\": \"Body\", \"hints\": [" + strings("h", hints) + "], "
                + "\"expectations\": [" + strings("e", expectations) + "]}";
    }

    private static String strings(String prefix, int count) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < count; i++) {
            out.append(i == 0 ? "" : ", ").append('"').append(prefix).append(i).append('"');
        }
        return out.toString();
    }

    private static String resource(String path) throws IOException {
        try (var input = DesignCatalogTest.class.getResourceAsStream(path)) {
            assertThat(input).as(path).isNotNull();
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
