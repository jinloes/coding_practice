package com.jinloes.system_design.partitioning;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

/**
 * Consistent hash ring with SHA-256 virtual nodes and distinct-node replica routing.
 *
 * <p>See {@code docs/patterns/partitioning.md}.
 */
public final class ConsistentHashRing<N> {
    private final int virtualNodesPerNode;
    private final NavigableMap<Long, N> ring = new TreeMap<>();
    private final Set<N> physicalNodes = new LinkedHashSet<>();

    public ConsistentHashRing(int virtualNodesPerNode) {
        if (virtualNodesPerNode <= 0) {
            throw new IllegalArgumentException("virtualNodesPerNode must be positive");
        }
        this.virtualNodesPerNode = virtualNodesPerNode;
    }

    public ConsistentHashRing(int virtualNodesPerNode, Collection<? extends N> initialNodes) {
        this(virtualNodesPerNode);
        Objects.requireNonNull(initialNodes, "initialNodes must not be null");
        for (N node : initialNodes) {
            addNode(node);
        }
    }

    public synchronized boolean addNode(N node) {
        Objects.requireNonNull(node, "node must not be null");
        if (!physicalNodes.add(node)) {
            return false;
        }
        for (int i = 0; i < virtualNodesPerNode; i++) {
            ring.put(hash(node + "#vnode-" + i), node);
        }
        return true;
    }

    public synchronized boolean removeNode(N node) {
        Objects.requireNonNull(node, "node must not be null");
        if (!physicalNodes.remove(node)) {
            return false;
        }
        for (int i = 0; i < virtualNodesPerNode; i++) {
            ring.remove(hash(node + "#vnode-" + i), node);
        }
        return true;
    }

    public synchronized int physicalNodeCount() {
        return physicalNodes.size();
    }

    public synchronized Optional<N> route(String key) {
        Objects.requireNonNull(key, "key must not be null");
        if (ring.isEmpty()) {
            return Optional.empty();
        }
        var entry = ring.ceilingEntry(hash(key));
        if (entry == null) {
            entry = ring.firstEntry();
        }
        return Optional.of(entry.getValue());
    }

    /**
     * Walks clockwise around the ring to return up to {@code replicaCount} distinct physical nodes.
     */
    public synchronized List<N> routeReplicas(String key, int replicaCount) {
        Objects.requireNonNull(key, "key must not be null");
        if (replicaCount <= 0) {
            throw new IllegalArgumentException("replicaCount must be positive");
        }
        if (ring.isEmpty()) {
            return List.of();
        }
        int targetCount = Math.min(replicaCount, physicalNodes.size());
        Set<N> selected = new LinkedHashSet<>(targetCount);
        long token = hash(key);
        for (N node : ring.tailMap(token, true).values()) {
            if (selected.add(node) && selected.size() == targetCount) {
                return List.copyOf(selected);
            }
        }
        for (N node : ring.headMap(token, false).values()) {
            if (selected.add(node) && selected.size() == targetCount) {
                return List.copyOf(selected);
            }
        }
        return new ArrayList<>(selected);
    }

    static long hash(String input) {
        byte[] digest = digest("SHA-256").digest(input.getBytes(StandardCharsets.UTF_8));
        return ByteBuffer.wrap(digest, 0, Long.BYTES).getLong();
    }

    static MessageDigest digest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(algorithm + " is required by the Java platform", e);
        }
    }
}
