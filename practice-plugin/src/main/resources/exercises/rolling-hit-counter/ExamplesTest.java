package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void countsHitsInsideTheCurrentWindow(){ Solution c=new Solution(); c.hit(1); c.hit(2); c.hit(300); assertThat(c.hitsInLastWindow(300)).isEqualTo(3); }
    @Test
    void excludesTheExactCutoffTimestamp(){ Solution c=new Solution(); c.hit(1); c.hit(301); assertThat(c.hitsInLastWindow(301)).isEqualTo(1); }
    @Test
    void keepsMultipleHitsAtOneSecond(){ Solution c=new Solution(); c.hit(5); c.hit(5); c.hit(304); assertThat(c.hitsInLastWindow(304)).isEqualTo(3); } }
