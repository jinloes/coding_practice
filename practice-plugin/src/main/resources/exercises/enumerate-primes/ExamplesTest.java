package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void returnsEmptyBelowFirstPrime(){ assertThat(Solution.enumeratePrimes(1)).containsExactly(); }
    @Test
    void listsPrimesThroughTen(){ assertThat(Solution.enumeratePrimes(10)).containsExactly(2,3,5,7); }
    @Test
    void listsPrimesThroughTwenty(){ assertThat(Solution.enumeratePrimes(20)).containsExactly(2,3,5,7,11,13,17,19); } }

