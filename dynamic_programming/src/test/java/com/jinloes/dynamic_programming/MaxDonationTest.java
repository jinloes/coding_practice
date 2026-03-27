package com.jinloes.dynamic_programming;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaxDonationTest {

    @Test
    void simpleNeighborhood() {
        int[] arr = {11, 15};
        assertThat(MaxDonation.getMax(arr)).isEqualTo(15);
        assertThat(MaxDonation.getMaxAlternative(arr)).isEqualTo(15);
    }

    @Test
    void complexNeighborhood() {
        int[] arr = {10, 3, 2, 5, 7, 8};
        assertThat(MaxDonation.getMax(arr)).isEqualTo(19);
        assertThat(MaxDonation.getMaxAlternative(arr)).isEqualTo(19);
    }

    @Test
    void uniformNeighborhood() {
        int[] arr = {7, 7, 7, 7, 7, 7, 7};
        assertThat(MaxDonation.getMax(arr)).isEqualTo(21);
        assertThat(MaxDonation.getMaxAlternative(arr)).isEqualTo(21);
    }

    @Test
    void largerNeighborhood() {
        int[] arr = {1, 2, 3, 4, 5, 1, 2, 3, 4, 5};
        assertThat(MaxDonation.getMax(arr)).isEqualTo(16);
        assertThat(MaxDonation.getMaxAlternative(arr)).isEqualTo(16);
    }
}