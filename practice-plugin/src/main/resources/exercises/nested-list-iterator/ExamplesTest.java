package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void flattensBalancedNestedLists() {
        List<Solution.NestedValue> items = List.of(
                Solution.NestedValue.ofList(List.of(
                        Solution.NestedValue.ofInt(1), Solution.NestedValue.ofInt(1))),
                Solution.NestedValue.ofInt(2),
                Solution.NestedValue.ofList(List.of(
                        Solution.NestedValue.ofInt(1), Solution.NestedValue.ofInt(1))));
        assertThat(drain(new Solution(items))).containsExactly(1, 1, 2, 1, 1);
    }

    @Test
    void flattensDeeplyNestedLists() {
        List<Solution.NestedValue> items = List.of(
                Solution.NestedValue.ofInt(1),
                Solution.NestedValue.ofList(List.of(
                        Solution.NestedValue.ofInt(4),
                        Solution.NestedValue.ofList(List.of(Solution.NestedValue.ofInt(6))))));
        assertThat(drain(new Solution(items))).containsExactly(1, 4, 6);
    }

    @Test
    void skipsEmptyNestedLists() {
        List<Solution.NestedValue> items = List.of(
                Solution.NestedValue.ofList(List.of()),
                Solution.NestedValue.ofList(List.of(Solution.NestedValue.ofList(List.of()))),
                Solution.NestedValue.ofList(List.of(
                        Solution.NestedValue.ofInt(3),
                        Solution.NestedValue.ofList(List.of()))));
        assertThat(drain(new Solution(items))).containsExactly(3);
    }

    private static List<Integer> drain(Solution iterator) {
        List<Integer> out = new ArrayList<>();
        while (iterator.hasNext()) {
            out.add(iterator.next());
        }
        return out;
    }
}
