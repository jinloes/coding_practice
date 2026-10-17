package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void countsMultipleTweetsAtTheSameSecond(){ Solution c=new Solution(); c.recordTweet("x",5); c.recordTweet("x",5); assertThat(c.getTweetCountsPerFrequency("minute","x",0,59)).containsExactly(2); }
    @Test
    void ignoresOtherTweetNames(){ Solution c=new Solution(); c.recordTweet("x",10); c.recordTweet("y",10); assertThat(c.getTweetCountsPerFrequency("minute","x",0,59)).containsExactly(1); }
    @Test
    void handlesPartialFinalBucket(){ Solution c=new Solution(); c.recordTweet("x",0); c.recordTweet("x",119); c.recordTweet("x",120); assertThat(c.getTweetCountsPerFrequency("minute","x",0,120)).containsExactly(1,1,1); }
    @Test
    void dayBucketsUseInclusiveEndTime(){ Solution c=new Solution(); c.recordTweet("x",86399); c.recordTweet("x",86400); assertThat(c.getTweetCountsPerFrequency("day","x",0,86400)).containsExactly(1,1); } }
