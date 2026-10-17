package com.jinloes.practice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    private final Map<String, String> files = new HashMap<>();
    public Solution() {}
    public List<String> ls(String path) { return new ArrayList<>(files.keySet()); }
    public void mkdir(String path) {}
    public void addContentToFile(String filePath, String content) { files.put(filePath, content); }
    public String readContentFromFile(String filePath) { return files.getOrDefault(filePath, ""); }
}
