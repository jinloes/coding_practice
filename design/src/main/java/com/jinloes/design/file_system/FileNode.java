package com.jinloes.design.file_system;

/**
 * File node storing content as a String.
 */
class FileNode extends Node {
  private String content;

  public FileNode(String name, DirectoryNode parent) {
    super(name, parent);
    this.content = "";
  }

  @Override
  public boolean isFile() {
    return true;
  }

  public String getContent() {
    return content;
  }

  public void appendContent(String more) {
    if (more == null || more.isEmpty()) {
      return;
    }
    this.content = this.content + more;
  }

  public void setContent(String newContent) {
    this.content = (newContent == null) ? "" : newContent;
  }
}
