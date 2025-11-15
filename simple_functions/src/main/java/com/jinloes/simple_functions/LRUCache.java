package com.jinloes.simple_functions;

import java.util.HashMap;
import java.util.Map;

class LRUCache {
    class LRUNode {
        LRUNode next;
        LRUNode previous;
        int key;
        int value;

        public LRUNode(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final Map<Integer, LRUNode> map;
    private final int capacity;
    private final LRUNode oldest;
    private final LRUNode newest;

    public LRUCache(int capacity) {
        this.map = new HashMap<>();
        this.capacity = capacity;
        this.oldest = new LRUNode(-1, -1);
        this.newest = new LRUNode(-1, -1);
        oldest.previous = newest;
        newest.next = oldest;
    }

    public int get(int key) {
        LRUNode node = map.get(key);
        if (node == null) {
            return -1;
        }

        remove(node);
        add(node);

        return node.value;
    }

    public void put(int key, int value) {
        if(map.containsKey(key)) {
            LRUNode node = map.get(key);
            remove(node);
        }

        LRUNode node = new LRUNode(key, value);
        map.put(key, node);
        add(node);

        if (map.size() > capacity) {
            map.remove(oldest.previous.key);
            remove(oldest.previous);
        }
    }

    private void add(LRUNode node) {
        LRUNode tmp = newest.next;
        tmp.previous = node;
        newest.next = node;
        node.next = tmp;
        node.previous = newest;
    }

    private void remove(LRUNode node) {
        node.previous.next = node.next;
        node.next.previous = node.previous;
    }
}