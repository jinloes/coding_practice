package com.jinloes.simple_functions;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchInsertTest {

    private SearchInsert searchInsert;

    @BeforeEach
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