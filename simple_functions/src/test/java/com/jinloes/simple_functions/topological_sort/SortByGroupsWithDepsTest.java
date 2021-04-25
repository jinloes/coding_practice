package com.jinloes.simple_functions.topological_sort;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SortByGroupsWithDepsTest {
    private SortByGroupsWithDeps sortByGroupsWithDeps;

    @Before
    public void setUp() throws Exception {
        sortByGroupsWithDeps = new SortByGroupsWithDeps();
    }

    @Test
    public void sortItems() {
        assertThat(sortByGroupsWithDeps.sortItems(8, 2, new int[]{-1, -1, 1, 0, 0, 1, 0, -1},
                Lists.newArrayList(
                        Lists.newArrayList(),
                        Lists.newArrayList(6),
                        Lists.newArrayList(5),
                        Lists.newArrayList(6),
                        Lists.newArrayList(3, 6),
                        Lists.newArrayList(),
                        Lists.newArrayList(),
                        Lists.newArrayList()
                )
        ))
                .containsExactly(0, 6, 3, 4, 5, 2, 7, 1);
    }

    @Test
    public void sortItemsNoAnswer() {
        assertThat(sortByGroupsWithDeps.sortItems(8, 2, new int[]{-1, -1, 1, 0, 0, 1, 0, -1},
                Lists.newArrayList(
                        Lists.newArrayList(),
                        Lists.newArrayList(6),
                        Lists.newArrayList(5),
                        Lists.newArrayList(6),
                        Lists.newArrayList(3),
                        Lists.newArrayList(),
                        Lists.newArrayList(4),
                        Lists.newArrayList()
                )
        ))
                .isEmpty();
    }


    @Test
    public void sortItemsMore() {
        assertThat(sortByGroupsWithDeps.sortItems(5, 3, new int[]{0, 0, 2, 1, 0},
                Lists.newArrayList(
                        Lists.newArrayList(3),
                        Lists.newArrayList(),
                        Lists.newArrayList(),
                        Lists.newArrayList(),
                        Lists.newArrayList(1, 3, 2)
                )
        ))
                .containsExactly(3, 2, 0, 1, 4);
    }


    @Test
    public void sortItemsShort() {
        assertThat(sortByGroupsWithDeps.sortItems(3, 1, new int[]{-1, 0, -1},
                Lists.newArrayList(
                        Lists.newArrayList(),
                        Lists.newArrayList(0),
                        Lists.newArrayList(1)
                )
        ))
                .containsExactly(0, 1, 2);
    }

    @Test
    public void sortItemsLonger() {
        assertThat(sortByGroupsWithDeps.sortItems(8, 2, new int[]{-1, -1, 1, 0, 0, 1, 0, -1},
                Lists.newArrayList(
                        Lists.newArrayList(3),
                        Lists.newArrayList(6, 0),
                        Lists.newArrayList(5),
                        Lists.newArrayList(6),
                        Lists.newArrayList(3, 6, 7),
                        Lists.newArrayList(),
                        Lists.newArrayList(),
                        Lists.newArrayList()
                )
        ))
                .containsExactly(5, 2, 7, 6, 3, 4, 0, 1);

    }
}