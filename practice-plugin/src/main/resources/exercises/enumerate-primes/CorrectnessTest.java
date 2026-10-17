package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesNegativeBounds(){ assertThat(Solution.enumeratePrimes(-10)).isEmpty(); }
    @Test
    void handlesTheFirstPrimeExactly(){ assertThat(Solution.enumeratePrimes(2)).containsExactly(2); }
    @Test
    void doesNotIncludeSquareComposites(){ assertThat(Solution.enumeratePrimes(49)).doesNotContain(1,4,9,25,49); }
    @Test
    void listsPrimesThroughThirty(){ assertThat(Solution.enumeratePrimes(30)).containsExactly(2,3,5,7,11,13,17,19,23,29); }
    @Test
    void producesTheKnownCountThroughOneThousand(){ int[] primes=Solution.enumeratePrimes(1000); assertThat(primes).hasSize(168); assertThat(primes[0]).isEqualTo(2); assertThat(primes[primes.length-1]).isEqualTo(997); } }

