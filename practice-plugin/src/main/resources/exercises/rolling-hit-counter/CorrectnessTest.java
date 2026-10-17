package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void emptyCounterHasNoHits(){ assertThat(new Solution().hitsInLastWindow(1000)).isZero(); }
    @Test
    void repeatedQueriesCanEvictOldEntries(){ Solution c=new Solution(); c.hit(10); c.hit(20); assertThat(c.hitsInLastWindow(309)).isEqualTo(2); assertThat(c.hitsInLastWindow(310)).isEqualTo(1); assertThat(c.hitsInLastWindow(320)).isZero(); }
    @Test
    void largeGroupedHistoryStaysAccurate(){ Solution c=new Solution(); for(int i=0;i<100;i++) c.hit(42); c.hit(341); c.hit(342); assertThat(c.hitsInLastWindow(341)).isEqualTo(2); assertThat(c.hitsInLastWindow(343)).isEqualTo(2); }
    @Test
    void interleavesHitsAndCountsChronologically(){ Solution c=new Solution(); c.hit(100); assertThat(c.hitsInLastWindow(100)).isEqualTo(1); c.hit(399); assertThat(c.hitsInLastWindow(399)).isEqualTo(2); c.hit(400); assertThat(c.hitsInLastWindow(400)).isEqualTo(2); } }
