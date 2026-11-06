package com.jinloes.system_design_plugin.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Maps requests on the plugin's private origin to bundled {@code /web/} resources. Everything else (other hosts,
 * other schemes, traversal, unknown types, and missing files) is blocked, so the pages never reach the network.
 */
public final class WebAssets {
    public static final String HOST = "system-design.invalid";
    public static final String ORIGIN = "https://" + HOST;
    public static final String INDEX = ORIGIN + "/index.html";
    public static final String EDITOR = ORIGIN + "/editor.html";
    private static final Pattern SAFE_PATH = Pattern.compile("(/[A-Za-z0-9_][A-Za-z0-9._-]*)+");
    private static final Map<String, String> MIME_TYPES = Map.ofEntries(
            Map.entry("html", "text/html"),
            Map.entry("js", "text/javascript"),
            Map.entry("mjs", "text/javascript"),
            Map.entry("css", "text/css"),
            Map.entry("json", "application/json"),
            Map.entry("map", "application/json"),
            Map.entry("woff2", "font/woff2"),
            Map.entry("woff", "font/woff"),
            Map.entry("ttf", "font/ttf"),
            Map.entry("svg", "image/svg+xml"),
            Map.entry("png", "image/png"),
            Map.entry("ico", "image/x-icon"),
            Map.entry("wasm", "application/wasm"),
            Map.entry("txt", "text/plain"));

    private WebAssets() {
    }

    /** A bundled classpath resource and the MIME type to serve it with. */
    public record Asset(String resourcePath, String mimeType) {
    }

    public static Optional<Asset> resolve(String url) {
        return resolve(url, path -> WebAssets.class.getResource(path) != null);
    }

    static Optional<Asset> resolve(String url, Predicate<String> exists) {
        if (url == null) {
            return Optional.empty();
        }
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException malformed) {
            return Optional.empty();
        }
        String path = uri.getRawPath();
        if (!"https".equals(uri.getScheme()) || !HOST.equals(uri.getRawAuthority()) || path == null
                || !SAFE_PATH.matcher(path).matches()) {
            return Optional.empty();
        }
        int dot = path.lastIndexOf('.');
        String mime = dot < 0 ? null : MIME_TYPES.get(path.substring(dot + 1).toLowerCase(Locale.ROOT));
        String resource = "/web" + path;
        if (mime == null || !exists.test(resource)) {
            return Optional.empty();
        }
        return Optional.of(new Asset(resource, mime));
    }

    /** Whether a navigation target stays on the plugin's private origin. */
    public static boolean isOwnOrigin(String url) {
        try {
            URI uri = new URI(url);
            return "https".equals(uri.getScheme()) && HOST.equals(uri.getRawAuthority());
        } catch (URISyntaxException | NullPointerException malformed) {
            return false;
        }
    }
}
