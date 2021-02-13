package com.jinloes.simple_functions;


import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchInsertTest {

    private SearchInsert searchInsert;

    @Before
    public void setUp() {
        searchInsert = new SearchInsert();
    }

    @Test
    public void testSearchInsert() {
        assertThat(searchInsert.searchInsert(new int[]{1, 3, 5}, 4))
                .isEqualTo(2);

        assertThat(searchInsert.searchInsert(new int[]{1, 3, 5, 6}, 7))
                .isEqualTo(4);
    }
}