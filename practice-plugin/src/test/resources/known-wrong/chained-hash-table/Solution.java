package com.jinloes.practice;

public class Solution {
    private String key;
    private String value;
    public Solution(int capacity) { if (capacity < 1) throw new IllegalArgumentException(); }
    public void put(String key, String value) { this.key = key; this.value = value; }
    public String get(String key) { return key != null && key.equals(this.key) ? value : null; }
    public boolean containsKey(String key) { return get(key) != null; }
    public boolean remove(String key) { if (containsKey(key)) { this.key = null; value = null; return true; } return false; }
    public int size() { return key == null ? 0 : 1; }
}
