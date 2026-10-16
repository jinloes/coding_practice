package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void unknownUserHasAnEmptyFeed() {
        Solution feed = new Solution();
        assertThat(feed.feed(42)).isEmpty();
        feed.follow(42, 7);
        assertThat(feed.feed(42)).isEmpty();
    }

    @Test
    void ordersByCallOrderNotByPostId() {
        Solution feed = new Solution();
        feed.post(1, 50);
        feed.post(1, 3);
        feed.post(1, 99);
        feed.post(1, 7);
        assertThat(feed.feed(1)).containsExactly(7, 99, 3, 50);
    }

    @Test
    void followingYourselfChangesNothing() {
        Solution feed = new Solution();
        feed.post(1, 10);
        feed.follow(1, 1);
        assertThat(feed.feed(1)).containsExactly(10);
        feed.unfollow(1, 1);
        assertThat(feed.feed(1)).containsExactly(10);
    }

    @Test
    void followingTwiceDoesNotDuplicatePosts() {
        Solution feed = new Solution();
        feed.post(2, 20);
        feed.follow(1, 2);
        feed.follow(1, 2);
        assertThat(feed.feed(1)).containsExactly(20);
        feed.unfollow(1, 2);
        assertThat(feed.feed(1)).isEmpty();
    }

    @Test
    void unfollowingSomeoneNotFollowedIsHarmless() {
        Solution feed = new Solution();
        feed.post(1, 1);
        feed.unfollow(1, 2);
        feed.unfollow(5, 6);
        assertThat(feed.feed(1)).containsExactly(1);
    }

    @Test
    void countsPostsMadeBeforeTheFollow() {
        Solution feed = new Solution();
        feed.post(2, 1);
        feed.post(1, 2);
        feed.post(2, 3);
        feed.follow(1, 2);
        assertThat(feed.feed(1)).containsExactly(3, 2, 1);
    }

    @Test
    void followingIsOneWayAndNotTransitive() {
        Solution feed = new Solution();
        feed.post(1, 11);
        feed.post(2, 22);
        feed.post(3, 33);
        feed.follow(1, 2);
        feed.follow(2, 3);
        assertThat(feed.feed(1)).containsExactly(22, 11);
        assertThat(feed.feed(2)).containsExactly(33, 22);
        assertThat(feed.feed(3)).containsExactly(33);
    }

    @Test
    void mergesManyFolloweesAndKeepsTenNewest() {
        Solution feed = new Solution();
        for (int user = 1; user <= 30; user++) {
            feed.follow(0, user);
        }
        int id = 100;
        for (int round = 0; round < 3; round++) {
            for (int user = 1; user <= 30; user++) {
                feed.post(user, id++);
            }
        }
        assertThat(feed.feed(0)).containsExactly(189, 188, 187, 186, 185, 184, 183, 182, 181, 180);
    }

    @Test
    void refollowingRestoresThePosts() {
        Solution feed = new Solution();
        feed.post(2, 5);
        feed.follow(1, 2);
        feed.unfollow(1, 2);
        feed.post(2, 6);
        feed.follow(1, 2);
        assertThat(feed.feed(1)).containsExactly(6, 5);
    }

    @Test
    void mixesOwnAndFollowedPostsByRecency() {
        Solution feed = new Solution();
        feed.post(1, 1);
        feed.post(2, 2);
        feed.post(3, 3);
        feed.post(1, 4);
        feed.follow(1, 2);
        feed.follow(1, 3);
        assertThat(feed.feed(1)).containsExactly(4, 3, 2, 1);
    }

    @Test
    void returnsAFreshListEachTime() {
        Solution feed = new Solution();
        feed.post(1, 1);
        List<Integer> first = feed.feed(1);
        first.add(99);
        assertThat(feed.feed(1)).containsExactly(1);
    }

    @Test
    void handlesNegativeIds() {
        Solution feed = new Solution();
        feed.post(-1, -5);
        feed.follow(-2, -1);
        assertThat(feed.feed(-2)).containsExactly(-5);
    }
}
