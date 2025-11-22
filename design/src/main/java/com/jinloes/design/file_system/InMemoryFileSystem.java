package com.jinloes.design.file_system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Simple in-memory file system storing file content in strings.
 */
public class InMemoryFileSystem {
  private final DirectoryNode root;

  public InMemoryFileSystem() {
    this.root = new DirectoryNode("", null);
  }

  public List<String> ls(String path) {
    Optional<Node> opt = resolve(path, /* createDirs= */ false, /* createFile= */ false);
    if (opt.isEmpty()) {
      return Collections.emptyList();
    }
    Node node = opt.get();
    if (node.isFile()) {
      return Collections.singletonList(node.getName());
    }
    return ((DirectoryNode) node).listNames();
  }

  public void mkdir(String path) {
    if (path == null || path.isEmpty() || "/".equals(path)) {
      return;
    }
    resolve(path, /* createDirs= */ true, /* createFile= */ false);
  }

  public void createFile(String filePath) {
    if (filePath == null || filePath.isEmpty()) {
      throw new IllegalArgumentException("Invalid path");
    }
    String[] parts = split(filePath);
    DirectoryNode dir = createParentDirs(parts);
    String fname = parts[parts.length - 1];
    Node existing = dir.getChild(fname);
    if (existing != null && !existing.isFile()) {
      throw new IllegalArgumentException("A directory exists at " + filePath);
    }
    if (existing == null) {
      dir.addChild(new FileNode(fname, dir));
    }
  }

  public void addContentToFile(String filePath, String content) {
    String[] parts = split(filePath);
    DirectoryNode dir = createParentDirs(parts);
    String fname = parts[parts.length - 1];
    Node node = dir.getChild(fname);
    if (node == null) {
      FileNode fn = new FileNode(fname, dir);
      fn.appendContent(content);
      dir.addChild(fn);
    } else if (node.isFile()) {
      ((FileNode) node).appendContent(content);
    } else {
      throw new IllegalArgumentException("Path points to a directory: " + filePath);
    }
  }

  public String readContentFromFile(String filePath) {
    Optional<Node> opt = resolve(filePath, /* createDirs= */ false, /* createFile= */ false);
    if (opt.isEmpty() || !opt.get().isFile()) {
      throw new IllegalArgumentException("File not found: " + filePath);
    }
    return ((FileNode) opt.get()).getContent();
  }

  public void delete(String path) {
    if (path == null || path.isEmpty() || "/".equals(path)) {
      throw new IllegalArgumentException("Cannot delete root");
    }
    Node node = resolve(path, /* createDirs= */ false, /* createFile= */ false)
        .orElseThrow(() -> new IllegalArgumentException("Path not found: " + path));
    DirectoryNode parent = node.getParent();
    if (parent == null) {
      throw new IllegalArgumentException("Cannot delete root");
    }
    parent.removeChild(node.getName());
  }

  public void move(String srcPath, String destPath) {
    Objects.requireNonNull(srcPath, "srcPath");
    Objects.requireNonNull(destPath, "destPath");

    Node src = resolve(srcPath, /* createDirs= */ false, /* createFile= */ false)
        .orElseThrow(() -> new IllegalArgumentException("Source not found: " + srcPath));
    DirectoryNode srcParent = src.getParent();
    if (srcParent == null) {
      throw new IllegalArgumentException("Cannot move root");
    }

    Optional<Node> destOpt = resolve(destPath, /* createDirs= */ false, /* createFile= */ false);
    if (destOpt.isPresent()) {
      Node destNode = destOpt.get();
      if (!destNode.isFile()) {
        DirectoryNode destDir = (DirectoryNode) destNode;
        srcParent.removeChild(src.getName());
        if (destDir.hasChild(src.getName())) {
          destDir.removeChild(src.getName());
        }
        destDir.addChild(src);
        return;
      } else {
        DirectoryNode destParent = destNode.getParent();
        srcParent.removeChild(src.getName());
        destParent.removeChild(destNode.getName());
        src.setParent(destParent);
        renameNode(src, destNode.getName());
        destParent.addChild(src);
        return;
      }
    }

    // Destination does not exist: treat as new path (rename or move)
    String[] parts = split(destPath);
    DirectoryNode destParent = createParentDirs(parts);
    String newName = parts[parts.length - 1];
    srcParent.removeChild(src.getName());
    if (destParent.hasChild(newName)) {
      destParent.removeChild(newName);
    }
    renameNode(src, newName);
    destParent.addChild(src);
  }

  private Optional<Node> resolve(String path, boolean createDirs, boolean createFile) {
    if (path == null || path.isEmpty() || "/".equals(path)) {
      return Optional.of(root);
    }
    String[] parts = split(path);
    Node cur = root;
    for (int i = 0; i < parts.length; i++) {
      String p = parts[i];
      if (!(cur instanceof DirectoryNode)) {
        return Optional.empty();
      }
      DirectoryNode dir = (DirectoryNode) cur;
      Node child = dir.getChild(p);
      boolean isLast = (i == parts.length - 1);
      if (child == null) {
        if (isLast && createFile) {
          FileNode fn = new FileNode(p, dir);
          dir.addChild(fn);
          return Optional.of(fn);
        } else if (createDirs || !isLast) {
          DirectoryNode nd = new DirectoryNode(p, dir);
          dir.addChild(nd);
          cur = nd;
        } else {
          return Optional.empty();
        }
      } else {
        cur = child;
      }
    }
    return Optional.of(cur);
  }

  private DirectoryNode createParentDirs(String[] parts) {
    if (parts.length == 1) {
      return root;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < parts.length - 1; i++) {
      sb.append("/").append(parts[i]);
    }
    Node node = resolve(sb.toString(), /* createDirs= */ true, /* createFile= */ false)
        .orElseThrow(() -> new IllegalArgumentException("Invalid parent path"));
    if (node.isFile()) {
      throw new IllegalArgumentException("Invalid parent path");
    }
    return (DirectoryNode) node;
  }

  private String[] split(String path) {
    String p = path.trim();
    if (p.equals("/")) {
      return new String[0];
    }
    if (p.startsWith("/")) {
      p = p.substring(1);
    }
    // handle consecutive slashes gracefully
    String[] raw = p.split("/");
    List<String> parts = new ArrayList<>();
    for (String s : raw) {
      if (!s.isEmpty()) {
        parts.add(s);
      }
    }
    return parts.toArray(new String[0]);
  }

  private void renameNode(Node node, String newName) {
    node.setName(newName);
  }
}
