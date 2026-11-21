package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Solution {
    private record FileRecord(long sizeBytes, Set<String> collections) {
    }

    private static final class Bucket {
        long bytes;
        int files;
    }

    private static final Comparator<Map.Entry<String, Bucket>> WORST_FIRST = (left, right) -> {
        int byBytes = Long.compare(left.getValue().bytes, right.getValue().bytes);
        if (byBytes != 0) {
            return byBytes;
        }
        return right.getKey().compareTo(left.getKey());
    };

    private final Map<String, FileRecord> files = new HashMap<>();
    private final Map<String, Bucket> collections = new HashMap<>();
    private long totalBytes;

    public Solution() {
    }

    public void putFile(String fileName, long sizeBytes, List<String> collectionNames) {
        if (fileName == null || fileName.isBlank() || sizeBytes < 0 || collectionNames == null) {
            throw new IllegalArgumentException("Invalid file metadata.");
        }
        Set<String> unique = new LinkedHashSet<>();
        for (String name : collectionNames) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Invalid collection name.");
            }
            unique.add(name);
        }
        FileRecord previous = files.put(fileName, new FileRecord(sizeBytes, Set.copyOf(unique)));
        if (previous != null) {
            totalBytes -= previous.sizeBytes();
            for (String oldName : previous.collections()) {
                Bucket bucket = collections.get(oldName);
                bucket.bytes -= previous.sizeBytes();
                bucket.files--;
                if (bucket.files == 0) {
                    collections.remove(oldName);
                }
            }
        }
        totalBytes += sizeBytes;
        for (String name : unique) {
            Bucket bucket = collections.computeIfAbsent(name, ignored -> new Bucket());
            bucket.bytes += sizeBytes;
            bucket.files++;
        }
    }

    public long totalBytes() {
        return totalBytes;
    }

    public List<String> topCollections(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("limit must be non-negative.");
        }
        if (limit == 0 || collections.isEmpty()) {
            return List.of();
        }
        PriorityQueue<Map.Entry<String, Bucket>> heap = new PriorityQueue<>(limit + 1, WORST_FIRST);
        for (Map.Entry<String, Bucket> entry : collections.entrySet()) {
            heap.offer(entry);
            if (heap.size() > limit) {
                heap.poll();
            }
        }
        List<String> result = new ArrayList<>(heap.size());
        while (!heap.isEmpty()) {
            result.add(heap.poll().getKey());
        }
        Collections.reverse(result);
        return result;
    }
}
