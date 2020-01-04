package com.jinloes.data_structures;

import com.codepoetics.protonpack.Indexed;
import com.codepoetics.protonpack.StreamUtils;
import com.google.common.base.MoreObjects;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class ArrayOnlyHashTable<K, V> {
    private static final int INITIAL_CAPACITY = 10;
    private Object[][] arr;
    private int capacity;

    public ArrayOnlyHashTable() {
        this(INITIAL_CAPACITY, INITIAL_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    ArrayOnlyHashTable(int initialCapacity, int bucketCapacity) {
        this.capacity = initialCapacity;
        this.arr = new Object[initialCapacity][];
        Arrays.fill(this.arr, new Object[bucketCapacity]);
    }


    public void put(K key, V value) {
        int keyIdx = getHashKey(key);
        Object[] objArr = arr[keyIdx];
        HashNode<K, V> hashNode = new HashNode<>(key, value);
        Optional<Indexed<Object>> valueIdx = findByKey(key, objArr);
        if (!valueIdx.isPresent()) {
            if (objArr[objArr.length - 1] != null) {
                objArr = Arrays.copyOf(objArr, objArr.length + 50);
                arr[keyIdx] = objArr;
            }
            // insert into the first null space
            for (int i = 0; i < objArr.length; i++) {
                if (objArr[i] == null) {
                    objArr[i] = hashNode;
                    break;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Optional<V> get(K key) {
        int keyIdx = getHashKey(key);
        Object[] objArr = arr[keyIdx];
        Optional<Indexed<Object>> valueIdxOpt = findByKey(key, objArr);
        return valueIdxOpt.map(Indexed::getValue)
                .map(val -> (HashNode<K, V>) val)
                .map(HashNode::getValue);
    }

    public void delete(K key) {
        int keyIdx = getHashKey(key);
        Object[] objArr = arr[keyIdx];
        Optional<Integer> objIdx = findByKey(key, objArr)
                .map(Indexed::getIndex)
                .map(Long::intValue);
        objIdx.ifPresent(idx -> objArr[idx] = null);

    }

    public boolean containsKey(K key) {
        int keyIdx = getHashKey(key);
        Object[] objArr = arr[keyIdx];
        return findByKey(key, objArr).isPresent();
    }

    private int getHashKey(K key) {
        return key.hashCode() % capacity;
    }

    private Optional<Indexed<Object>> findByKey(K key, Object[] objArr) {
        HashNode<K, V> hashNode = new HashNode<>(key);
        return StreamUtils.zipWithIndex(Arrays.stream(objArr))
                .filter(val -> Objects.equals(val.getValue(), hashNode))
                .findFirst();

    }

    private static class HashNode<K, V> {
        private final K key;
        private final V value;


        public HashNode(K key) {
            this(key, null);
        }

        public HashNode(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HashNode<?, ?> hashNode = (HashNode<?, ?>) o;
            return Objects.equals(key, hashNode.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("key", key)
                    .add("value", value)
                    .toString();
        }
    }
}
