package com.jinloes.design.file_system;

import java.util.Objects;

/**
 * Abstract node representing either a directory or a file.
 */
abstract class Node {
  protected String name;
  protected DirectoryNode parent;

  protected Node(String name, DirectoryNode parent) {
    this.name = Objects.requireNonNull(name, "name");
    this.parent = parent;
  }

  public String getName() {
    return name;
  }

  public DirectoryNode getParent() {
    return parent;
  }

  protected void setParent(DirectoryNode parent) {
    this.parent = parent;
  }

  /**
   * Protected setter to allow controlled renaming.
   */
  protected void setName(String newName) {
    this.name = Objects.requireNonNull(newName, "newName");
  }

  public String getPath() {
    if (parent == null) {
      return "/";
    }
    StringBuilder sb = new StringBuilder();
    Node cur = this;
    while (cur != null && cur.getParent() != null) {
      sb.insert(0, "/" + cur.getName());
      cur = cur.getParent();
    }
    if (sb.length() == 0) {
      sb.append("/");
    }
    return sb.toString();
  }

  public abstract boolean isFile();
}
