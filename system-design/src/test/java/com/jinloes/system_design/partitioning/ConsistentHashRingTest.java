package com.jinloes.system_design.partitioning;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsistentHashRingTest {
    @Test
    void movesOnlyFractionOfKeysWhenAddingOrRemovingNode() {
        List<String> initialNodes = List.of("shard-a", "shard-b", "shard-c", "shard-d");
        ConsistentHashRing<String> ring = new ConsistentHashRing<>(128, initialNodes);

        int totalKeys = 4_000;
        Map<String, String> before = new HashMap<>(totalKeys);
        for (int i = 0; i < totalKeys; i++) {
            String key = "tenant-key-" + i;
            before.put(key, ring.route(key).orElseThrow());
        }

        assertThat(ring.addNode("shard-e")).isTrue();
        assertThat(ring.addNode("shard-e")).isFalse();

        int moved = 0;
        for (int i = 0; i < totalKeys; i++) {
            String key = "tenant-key-" + i;
            if (!before.get(key).equals(ring.route(key).orElseThrow())) {
                moved++;
            }
        }
        double movedRatio = (double) moved / totalKeys;
        // Ideal movement from 4 -> 5 nodes is ~20%; virtual nodes keep it tightly bounded.
        assertThat(movedRatio).isBetween(0.12, 0.28);

        // Removing the added node restores exact original routing.
        assertThat(ring.removeNode("shard-e")).isTrue();
        for (int i = 0; i < totalKeys; i++) {
            String key = "tenant-key-" + i;
            assertThat(ring.route(key)).contains(before.get(key));
        }
    }

    @Test
    void routesDistinctPhysicalReplicasAndHandlesEmptyRing() {
        ConsistentHashRing<String> empty = new ConsistentHashRing<>(32);
        assertThat(empty.route("k")).isEmpty();
        assertThat(empty.routeReplicas("k", 3)).isEmpty();

        ConsistentHashRing<String> ring = new ConsistentHashRing<>(64, List.of("n1", "n2", "n3"));
        List<String> replicas = ring.routeReplicas("order-42", 2);
        assertThat(replicas).hasSize(2).doesNotHaveDuplicates().isSubsetOf("n1", "n2", "n3");
        assertThat(replicas.get(0)).isEqualTo(ring.route("order-42").orElseThrow());

        // Requesting more replicas than physical nodes caps at physicalNodeCount().
        assertThat(ring.physicalNodeCount()).isEqualTo(3);
        assertThat(ring.removeNode("missing-node")).isFalse();
        assertThat(ring.routeReplicas("order-42", 5))
            .hasSize(3)
            .containsExactlyInAnyOrder("n1", "n2", "n3");

        // Exercise clockwise wrap-around across ring boundary in routeReplicas.
        ConsistentHashRing<String> smallRing = new ConsistentHashRing<>(3, List.of("n1", "n2"));
        for (int i = 0; i < 64; i++) {
            assertThat(smallRing.routeReplicas("wrap-key-" + i, 2))
                .hasSize(2)
                .containsExactlyInAnyOrder("n1", "n2");
        }

        // Exercise full ring scan when distinct physical nodes share virtual-node hashes.
        record SameLabelNode(int id) {
            @Override
            public String toString() {
                return "same";
            }
        }
        ConsistentHashRing<SameLabelNode> colliding = new ConsistentHashRing<>(
            2,
            List.of(new SameLabelNode(1), new SameLabelNode(2))
        );
        for (int i = 0; i < 32; i++) {
            assertThat(colliding.routeReplicas("probe-" + i, 2)).containsExactly(new SameLabelNode(2));
        }

        assertThatThrownBy(() -> new ConsistentHashRing<>(0))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ring.routeReplicas("order-42", 0))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ConsistentHashRing.digest("NO-SUCH-ALGO"))
            .isInstanceOf(IllegalStateException.class);
    }
}
