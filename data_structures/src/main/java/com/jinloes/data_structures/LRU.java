package com.jinloes.data_structures;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * LRU implementation using a linked hash map. Linked hash map provides quick access to values and the ability to
 * quickly order nodes to so that oldest ones are at the front.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class LRU<K, V> {
    private final Map<K, V> delegate;

    public LRU(int capacity) {
        this.delegate = new LinkedHashMap<K, V>(capacity, 1f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        };
    }

    public LRU<K, V> add(K key, V value) {
        delegate.put(key, value);
        return this;
    }

    public V get(K key) {
        return delegate.get(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LRU<?, ?> lru = (LRU<?, ?>) o;
        return Objects.equals(delegate, lru.delegate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
