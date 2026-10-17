package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void separatesMinuteBoundaryIntoTwoBuckets(){ Solution c=new Solution(); c.recordTweet("a",0); c.recordTweet("a",60); assertThat(c.getTweetCountsPerFrequency("minute","a",0,60)).containsExactly(1,1); }
    @Test
    void groupsRecordsInsideOneHour(){ Solution c=new Solution(); c.recordTweet("a",10); c.recordTweet("a",70); assertThat(c.getTweetCountsPerFrequency("hour","a",0,3599)).containsExactly(2); }
    @Test
    void missingTweetNameReturnsZeroBuckets(){ assertThat(new Solution().getTweetCountsPerFrequency("minute","missing",0,120)).containsExactly(0,0,0); } }
