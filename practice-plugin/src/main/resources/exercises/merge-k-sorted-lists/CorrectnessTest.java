package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CorrectnessTest {
    @Test
    void emptyArrayReturnsNull() {
        assertThat(merge()).as("mergeKLists of an empty array").isEmpty();
    }

    @Test
    void arrayOfOnlyEmptyListsReturnsNull() {
        assertThat(merge(null, null, null)).as("mergeKLists of three null lists").isEmpty();
    }

    @Test
    void singleListIsReturnedUnchanged() {
        assertThat(merge(chain(1, 2, 3))).as("mergeKLists of the single list [1, 2, 3]").containsExactly(1, 2, 3);
    }

    @Test
    void mixesEmptyAndNonEmptyLists() {
        assertThat(merge(null, chain(5), null, chain(1, 9), null))
                .as("mergeKLists of null, [5], null, [1, 9], null").containsExactly(1, 5, 9);
    }

    @Test
    void keepsEveryDuplicate() {
        assertThat(merge(chain(2, 2, 2), chain(2, 2), chain(2)))
                .as("mergeKLists of lists that contain only 2").containsExactly(2, 2, 2, 2, 2, 2);
    }

    @Test
    void ordersNegativeValues() {
        assertThat(merge(chain(-9, -3, 0), chain(-8, -8, 5), chain(-10, 2)))
                .as("mergeKLists with negative values").containsExactly(-10, -9, -8, -8, -3, 0, 2, 5);
    }

    @Test
    void comparesExtremeValuesWithoutOverflow() {
        assertThat(merge(chain(Integer.MIN_VALUE, Integer.MAX_VALUE), chain(-1, 0, Integer.MAX_VALUE), chain(Integer.MIN_VALUE, 1)))
                .as("mergeKLists with Integer.MIN_VALUE and Integer.MAX_VALUE")
                .containsExactly(Integer.MIN_VALUE, Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Test
    void handlesListsThatDoNotInterleave() {
        assertThat(merge(chain(10, 11, 12), chain(1, 2, 3), chain(5, 6)))
                .as("mergeKLists of lists that occupy disjoint value ranges")
                .containsExactly(1, 2, 3, 5, 6, 10, 11, 12);
    }

    @Test
    void handlesOneLongListAndManyShortOnes() {
        int[] longValues = new int[500];
        for (int i = 0; i < longValues.length; i++) {
            longValues[i] = i * 2;
        }
        List<Solution.Node> lists = new ArrayList<>();
        lists.add(chain(longValues));
        for (int i = 0; i < 20; i++) {
            lists.add(chain(i * 2 + 1));
        }
        List<Integer> merged = merge(lists.toArray(new Solution.Node[0]));
        assertThat(merged).as("size of the merge of one 500-node list and twenty single nodes").hasSize(520);
        assertThat(merged).as("merged values must be in ascending order").isSorted();
    }

    @Test
    void mergesManySingleNodeLists() {
        Solution.Node[] lists = new Solution.Node[1000];
        for (int i = 0; i < lists.length; i++) {
            lists[i] = chain((i * 37) % 1000);
        }
        List<Integer> merged = merge(lists);
        assertThat(merged).as("size of the merge of 1000 single-node lists").hasSize(1000);
        assertThat(merged).as("merged values must be in ascending order").isSorted();
        assertThat(merged.get(0)).as("smallest merged value").isEqualTo(0);
        assertThat(merged.get(999)).as("largest merged value").isEqualTo(999);
    }

    @Test
    void terminatesTheMergedList() {
        Solution.Node result = Solution.mergeKLists(new Solution.Node[]{chain(1, 3), chain(2, 4)});
        Solution.Node tail = result;
        for (int steps = 0; tail != null && tail.next != null && steps < 10; steps++) {
            tail = tail.next;
        }
        assertThat(tail.value).as("last value of the merge of [1, 3] and [2, 4]").isEqualTo(4);
        assertThat(tail.next).as("the merged tail must not link onward").isNull();
    }

    private static List<Integer> merge(Solution.Node... lists) {
        String call = "mergeKLists with %d lists".formatted(lists.length);
        try {
            List<Integer> values = new ArrayList<>();
            for (Solution.Node node = Solution.mergeKLists(lists); node != null; node = node.next) {
                values.add(node.value);
                if (values.size() > 100000) {
                    return fail("%s returned a list that never ends".formatted(call));
                }
            }
            return values;
        } catch (Throwable thrown) {
            return fail("%s threw %s".formatted(call, thrown), thrown);
        }
    }

    private static Solution.Node chain(int... values) {
        Solution.Node head = null;
        for (int i = values.length - 1; i >= 0; i--) {
            head = new Solution.Node(values[i], head);
        }
        return head;
    }
}
