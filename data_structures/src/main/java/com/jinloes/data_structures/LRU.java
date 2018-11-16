package com.jinloes.data_structures;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LRU<K, V> {
  private int maxSize;
  private final Map<K, V> delegate;

  public LRU(int maxSize) {
    this.maxSize = maxSize;
    this.delegate = new LinkedHashMap<K, V>() {
      @Override protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return delegate.size() > maxSize;
      }
    };
  }

  public void add(K key, V value) {
    delegate.put(key, value);
  }

  public Set<Map.Entry<K, V>> getEntries() {
    return delegate.entrySet();
  }
}
