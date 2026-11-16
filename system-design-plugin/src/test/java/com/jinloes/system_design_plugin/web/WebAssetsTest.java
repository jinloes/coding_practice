package com.jinloes.system_design_plugin.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class WebAssetsTest {
    @Test
    void resolvesBundledResourcesWithMimeTypes() {
        assertThat(WebAssets.resolve(WebAssets.INDEX, "/web/index.html"::equals))
                .contains(new WebAssets.Asset("/web/index.html", "text/html"));
        assertThat(WebAssets.resolve("https://system-design.invalid/assets/app-1.JS", path -> true))
                .contains(new WebAssets.Asset("/web/assets/app-1.JS", "text/javascript"));
        assertThat(WebAssets.resolve("https://system-design.invalid/fonts/Virgil/x.woff2?v=1", path -> true))
                .map(WebAssets.Asset::mimeType).contains("font/woff2");
        assertThat(WebAssets.resolve("https://system-design.invalid/a.css#top", path -> true))
                .map(WebAssets.Asset::resourcePath).contains("/web/a.css");
    }

    @Test
    void blocksEverythingElse() {
        for (String url : new String[]{
                null, "", "not a url", "http://system-design.invalid/index.html",
                "https://unpkg.com/@excalidraw/excalidraw/dist/fonts/x.woff2",
                "https://esm.sh/index.html", "https://system-design.invalid:443/index.html",
                "https://user@system-design.invalid/index.html", "data:text/html,hi", "file:///etc/passwd",
                "https://system-design.invalid/../secret.html", "https://system-design.invalid/a/%2e%2e/b.html",
                "https://system-design.invalid/.hidden.html", "https://system-design.invalid/",
                "https://system-design.invalid", "https://system-design.invalid/noextension",
                "https://system-design.invalid/x.exe", "https://system-design.invalid/a//b.js",
                "https://system-design.invalid/a\\b.js", "https:/opaque.html"}) {
            assertThat(WebAssets.resolve(url, path -> true)).as(String.valueOf(url)).isEmpty();
        }
    }

    @Test
    void blocksMissingResources() {
        assertThat(WebAssets.resolve("https://system-design.invalid/missing.html")).isEmpty();
        assertThat(WebAssets.resolve(WebAssets.EDITOR, path -> false)).isEqualTo(Optional.empty());
    }

    @Test
    void recognizesOwnOrigin() {
        assertThat(WebAssets.isOwnOrigin(WebAssets.EDITOR)).isTrue();
        assertThat(WebAssets.isOwnOrigin("https://example.com/")).isFalse();
        assertThat(WebAssets.isOwnOrigin("http://system-design.invalid/")).isFalse();
        assertThat(WebAssets.isOwnOrigin("bad url")).isFalse();
        assertThat(WebAssets.isOwnOrigin(null)).isFalse();
    }

    @Test
    void resolvesEveryBuiltWebAssetWhenPresent() throws IOException {
        String buildDir = System.getProperty("design.web.build.dir");
        if (buildDir == null || buildDir.isBlank()) {
            return;
        }
        Path root = Path.of(buildDir);
        if (!Files.isDirectory(root)) {
            return;
        }
        List<Path> builtFiles;
        try (var paths = Files.walk(root)) {
            builtFiles = paths.filter(Files::isRegularFile).toList();
        }
        assertThat(builtFiles).isNotEmpty();
        for (Path file : builtFiles) {
            String relative = "/" + root.relativize(file).toString().replace('\\', '/');
            assertThat(WebAssets.resolve(WebAssets.ORIGIN + relative))
                    .as("built web asset %s must match SAFE_PATH, MIME_TYPES, and /web classpath packaging", relative)
                    .isPresent();
        }
    }
}
