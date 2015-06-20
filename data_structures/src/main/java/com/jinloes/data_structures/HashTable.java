package com.jinloes.data_structures;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.TreeMap;

/**
 * Models a hash table.
 */
public class HashTable<K extends Object, V extends Object> {
    public static final int DEFAULT_CAPACITY = 16;
    private int capacity;
    private int size;
    private TreeMap<K,V>[] arr;

    public HashTable() {
        this(DEFAULT_CAPACITY);

    }

    @SuppressWarnings("unchecked")
    public HashTable(int capacity) {
        this.capacity = capacity;
        this.arr = (TreeMap<K, V>[])Array.newInstance(TreeMap.class, capacity);
        Arrays.fill(arr, new TreeMap<K,V>());
    }

    public int getSize() {
        return size;
    }

    private int getHashIndex(K key) {
        return key.hashCode() % capacity;
    }

    public void put(K key, V val) {
        int hashIndex = getHashIndex(key);
        arr[hashIndex].put(key, val);
        size++;
    }


    public boolean containsKey(K key) {
        return arr[getHashIndex(key)].containsKey(key);
    }

    public boolean containsValue(V val) {
        for (TreeMap<K,V> hashValues : arr) {
            if (hashValues.containsValue(val)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public V get(K key) {
        return (V) arr[getHashIndex(key)].get(key);
    }

    public V remove(K key) {
        return (V) arr[getHashIndex(key)].remove(key);
    }
}
