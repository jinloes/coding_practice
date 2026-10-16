package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void showsFolloweesPostsNewestFirst() {
        Solution feed = new Solution();
        feed.post(1, 5);
        feed.post(2, 6);
        feed.follow(1, 2);
        assertThat(feed.feed(1)).as("feed(1)").containsExactly(6, 5);
        assertThat(feed.feed(2)).as("feed(2)").containsExactly(6);
    }

    @Test
    void unfollowHidesThePosts() {
        Solution feed = new Solution();
        feed.follow(1, 2);
        feed.post(2, 8);
        feed.post(1, 9);
        feed.unfollow(1, 2);
        assertThat(feed.feed(1)).containsExactly(9);
    }

    @Test
    void keepsOnlyTheTenNewestPosts() {
        Solution feed = new Solution();
        for (int id = 1; id <= 12; id++) {
            feed.post(3, id);
        }
        assertThat(feed.feed(3)).containsExactly(12, 11, 10, 9, 8, 7, 6, 5, 4, 3);
    }
}
