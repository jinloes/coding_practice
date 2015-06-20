package com.jinloes.data_structures;

import java.util.Objects;

/**
 * Models a hash table.
 */
//TODO(jinloes) handle collisions
public class HashTable<K extends Object, V extends Object> {
    public static final int DEFAULT_CAPACITY = 16;
    private int capacity;
    private int size;
    private Object[] arr;

    public HashTable() {
        this(DEFAULT_CAPACITY);

    }

    public HashTable(int capacity) {
        this.capacity = capacity;
        this.arr = new Object[capacity];
    }

    public int getSize() {
        return size;
    }

    public void put(K key, V val) {
        arr[key.hashCode() % capacity] = val;
        size++;
    }

    public boolean containsKey(K key) {
        return arr[key.hashCode() % capacity] != null;
    }

    public boolean containsValue(V val) {
        for (Object anArr : arr) {
            if (Objects.equals(anArr, val)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public V get(K key) {
        return (V) arr[key.hashCode() % capacity];
    }
}
