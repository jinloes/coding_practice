package com.jinloes.design.file_system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Directory node holding children nodes.
 */
class DirectoryNode extends Node {
  private final Map<String, Node> children = new HashMap<>();

  public DirectoryNode(String name, DirectoryNode parent) {
    super(name, parent);
  }

  @Override
  public boolean isFile() {
    return false;
  }

  public Node getChild(String name) {
    return children.get(name);
  }

  public Collection<Node> getChildren() {
    return children.values();
  }

  public List<String> listNames() {
    List<String> names = new ArrayList<>(children.keySet());
    Collections.sort(names);
    return names;
  }

  public void addChild(Node node) {
    children.put(node.getName(), node);
    node.setParent(this);
  }

  public Node removeChild(String name) {
    Node removed = children.remove(name);
    if (removed != null) {
      removed.setParent(null);
    }
    return removed;
  }

  public boolean hasChild(String name) {
    return children.containsKey(name);
  }
}
