package com.jinloes.linked_list;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {
    public class Node {
        int key;
        int value;
        Node next;
        Node previous;

        public Node(int key, int value) {
            this.key = key;
            this.value = value;
            this.next = null;
            this.previous = null;
        }
    }

    private int capacity;
    private Map<Integer, Node> cache;
    private Node head = new Node(0, 0);
    private Node tail = new Node(0, 0);

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.head.next = tail;
        this.tail.previous = head;
    }

    public int get(int key) {
        Node node = cache.get(key);
        if (node != null) {
            delete(node);
            insert(node);
        } else {
            return -1;
        }

        return node.value;
    }

    public void put(int key, int value) {
        if (!cache.containsKey(key)) {
            Node node = new Node(key, value);
            cache.put(key, node);
            insert(node);
            if (cache.size() > capacity) {
                cache.remove(head.next.key);
                delete(head.next);
            }

        } else {
            Node node = cache.get(key);
            node.value = value;

            delete(node);
            insert(node);
        }
    }

    private void delete(Node node) {
        node.previous.next = node.next;
        node.next.previous = node.previous;
    }


    private void insert(Node node) {
        node.next = tail;
        node.previous = tail.previous;

        tail.previous.next = node;

        tail.previous = node;
    }
}

