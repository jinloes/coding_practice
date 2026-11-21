package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    private static final class Node {
        final Map<String, Node> literals = new HashMap<>();
        Node wildcard;
        String handler;
    }

    private final Node root = new Node();

    public Solution() {
    }

    public void register(String pattern, String handler) {
        if (handler == null || handler.isBlank()) {
            throw new IllegalArgumentException("Handler must not be blank.");
        }
        String[] segments = parseSegments(pattern);
        Node current = root;
        for (String segment : segments) {
            if ("*".equals(segment)) {
                if (current.wildcard == null) {
                    current.wildcard = new Node();
                }
                current = current.wildcard;
            } else {
                current = current.literals.computeIfAbsent(segment, ignored -> new Node());
            }
        }
        current.handler = handler;
    }

    public String match(String path) {
        String[] segments = parseSegments(path);
        String resolved = find(root, segments, 0);
        return resolved == null ? "" : resolved;
    }

    private static String find(Node node, String[] segments, int index) {
        if (node == null) {
            return null;
        }
        if (index == segments.length) {
            return node.handler;
        }
        Node exact = node.literals.get(segments[index]);
        if (exact != null) {
            String fromLiteral = find(exact, segments, index + 1);
            if (fromLiteral != null) {
                return fromLiteral;
            }
        }
        if (node.wildcard != null) {
            return find(node.wildcard, segments, index + 1);
        }
        return null;
    }

    private static String[] parseSegments(String raw) {
        if (raw == null || !raw.startsWith("/") || raw.contains("//")) {
            throw new IllegalArgumentException("Invalid path: " + raw);
        }
        if ("/".equals(raw)) {
            return new String[0];
        }
        String trimmed = raw.endsWith("/") ? raw.substring(0, raw.length() - 1) : raw;
        return trimmed.substring(1).split("/", -1);
    }
}
