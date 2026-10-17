package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class Solution {
    private final Node root = new Node();
    public Solution() {}
    public List<String> ls(String path) {
        Node node = find(path, false, false);
        if (node == null) return Collections.emptyList();
        if (node.file) return List.of(lastName(path));
        return new ArrayList<>(node.children.keySet());
    }
    public void mkdir(String path) { find(path, true, false); }
    public void addContentToFile(String filePath, String content) {
        Node node = find(filePath, true, true);
        node.file = true;
        node.content.append(content == null ? "" : content);
    }
    public String readContentFromFile(String filePath) { return find(filePath, false, true).content.toString(); }
    private Node find(String path, boolean create, boolean filePath) {
        String[] parts = parts(path);
        Node node = root;
        int limit = filePath ? parts.length : parts.length;
        for (int i = 0; i < limit; i++) {
            if (node.file) return null;
            Node next = node.children.get(parts[i]);
            if (next == null) {
                if (!create) return null;
                next = new Node();
                node.children.put(parts[i], next);
            }
            node = next;
        }
        return node;
    }
    private static String[] parts(String path) {
        if (path == null || path.isBlank() || path.equals("/")) return new String[0];
        String p = path.startsWith("/") ? path.substring(1) : path;
        return p.isEmpty() ? new String[0] : p.split("/");
    }
    private static String lastName(String path) { String[] p = parts(path); return p.length == 0 ? "" : p[p.length - 1]; }
    private static final class Node { final TreeMap<String, Node> children = new TreeMap<>(); final StringBuilder content = new StringBuilder(); boolean file; }
}
