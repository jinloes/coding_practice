package com.jinloes.data_structures;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;
import java.util.TreeMap;

/**
 * A hash table implementation that uses chaining with TreeMaps to handle collisions.
 * This implementation provides O(1) average time complexity for basic operations
 * (get, put, remove) when the hash function distributes keys uniformly.
 *
 * @param <K> the type of keys maintained by this hash table
 * @param <V> the type of mapped values
 * @author Jinloes
 * @version 1.0
 */
public class HashTable<K, V> {

    /** Default initial capacity of the hash table */
    public static final int DEFAULT_CAPACITY = 16;

    /** Current capacity of the hash table */
    private int capacity;

    /** Number of key-value pairs in the hash table */
    private int size;

    /** Array of TreeMaps to handle collisions using separate chaining */
    private TreeMap<K, V>[] buckets;

    /**
     * Constructs a new hash table with default capacity.
     */
    public HashTable() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Constructs a new hash table with specified capacity.
     *
     * @param capacity the initial capacity of the hash table
     * @throws IllegalArgumentException if capacity is less than or equal to 0
     */
    @SuppressWarnings("unchecked")
    public HashTable(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }

        this.capacity = capacity;
        this.buckets = (TreeMap<K, V>[]) Array.newInstance(TreeMap.class, capacity);

        // Initialize each bucket with a new TreeMap
        for (int i = 0; i < capacity; i++) {
            buckets[i] = new TreeMap<>();
        }
    }

    /**
     * Returns the number of key-value mappings in this hash table.
     *
     * @return the number of key-value mappings
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns true if this hash table contains no key-value mappings.
     *
     * @return true if this hash table is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Computes the hash index for a given key.
     * Uses modulo operation to ensure the index is within bucket range.
     *
     * @param key the key to hash
     * @return the hash index
     * @throws NullPointerException if the key is null
     */
    private int getHashIndex(K key) {
        Objects.requireNonNull(key, "Key cannot be null");
        return Math.abs(key.hashCode() % capacity);
    }

    /**
     * Associates the specified value with the specified key in this hash table.
     * If the hash table previously contained a mapping for the key, the old value
     * is replaced.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @throws NullPointerException if the key is null
     */
    public void put(K key, V value) {
        Objects.requireNonNull(key, "Key cannot be null");

        int bucketIndex = getHashIndex(key);
        TreeMap<K, V> bucket = buckets[bucketIndex];

        // Check if key already exists to update size correctly
        boolean keyExists = bucket.containsKey(key);
        bucket.put(key, value);

        if (!keyExists) {
            size++;
        }
    }

    /**
     * Returns true if this hash table contains a mapping for the specified key.
     *
     * @param key the key whose presence in this hash table is to be tested
     * @return true if this hash table contains a mapping for the specified key
     * @throws NullPointerException if the key is null
     */
    public boolean containsKey(K key) {
        Objects.requireNonNull(key, "Key cannot be null");
        return buckets[getHashIndex(key)].containsKey(key);
    }

    /**
     * Returns true if this hash table maps one or more keys to the specified value.
     *
     * @param value value whose presence in this hash table is to be tested
     * @return true if this hash table maps one or more keys to the specified value
     */
    public boolean containsValue(V value) {
        for (TreeMap<K, V> bucket : buckets) {
            if (bucket.containsValue(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * hash table contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if no mapping exists
     * @throws NullPointerException if the key is null
     */
    public V get(K key) {
        Objects.requireNonNull(key, "Key cannot be null");
        return buckets[getHashIndex(key)].get(key);
    }

    /**
     * Removes the mapping for the specified key from this hash table if present.
     *
     * @param key the key whose mapping is to be removed from the hash table
     * @return the previous value associated with the key, or null if no mapping existed
     * @throws NullPointerException if the key is null
     */
    public V remove(K key) {
        Objects.requireNonNull(key, "Key cannot be null");

        int bucketIndex = getHashIndex(key);
        TreeMap<K, V> bucket = buckets[bucketIndex];

        V removedValue = bucket.remove(key);
        if (removedValue != null) {
            size--;
        }
        return removedValue;
    }
}
