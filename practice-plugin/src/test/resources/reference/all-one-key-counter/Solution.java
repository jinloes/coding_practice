package com.jinloes.practice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Solution {
    private static final class Bucket {
        final int count;
        final Set<String> keys = new HashSet<>();
        Bucket prev;
        Bucket next;

        Bucket(int count) {
            this.count = count;
        }
    }

    private final Map<String, Bucket> byKey = new HashMap<>();
    private final Bucket head = new Bucket(0);
    private final Bucket tail = new Bucket(0);

    public Solution() {
        head.next = tail;
        tail.prev = head;
    }

    public void inc(String key) {
        Bucket current = byKey.get(key);
        if (current == null) {
            Bucket bucket = head.next.count == 1 ? head.next : insertAfter(head, new Bucket(1));
            bucket.keys.add(key);
            byKey.put(key, bucket);
            return;
        }
        move(key, current, current.count + 1, current.next, true);
    }

    public void dec(String key) {
        Bucket current = byKey.get(key);
        if (current == null) {
            return;
        }
        if (current.count == 1) {
            current.keys.remove(key);
            byKey.remove(key);
            removeIfEmpty(current);
            return;
        }
        move(key, current, current.count - 1, current.prev, false);
    }

    public String getMaxKey() {
        return tail.prev == head ? "" : tail.prev.keys.iterator().next();
    }

    public String getMinKey() {
        return head.next == tail ? "" : head.next.keys.iterator().next();
    }

    private void move(String key, Bucket current, int newCount, Bucket neighbor, boolean afterCurrent) {
        Bucket target = neighbor.count == newCount ? neighbor : (afterCurrent
                ? insertAfter(current, new Bucket(newCount)) : insertAfter(current.prev, new Bucket(newCount)));
        current.keys.remove(key);
        target.keys.add(key);
        byKey.put(key, target);
        removeIfEmpty(current);
    }

    private Bucket insertAfter(Bucket previous, Bucket bucket) {
        bucket.prev = previous;
        bucket.next = previous.next;
        previous.next.prev = bucket;
        previous.next = bucket;
        return bucket;
    }

    private void removeIfEmpty(Bucket bucket) {
        if (!bucket.keys.isEmpty()) {
            return;
        }
        bucket.prev.next = bucket.next;
        bucket.next.prev = bucket.prev;
    }
}
