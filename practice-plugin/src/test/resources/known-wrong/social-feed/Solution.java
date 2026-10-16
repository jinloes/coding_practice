package com.jinloes.practice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Solution {
    private static final int LIMIT = 10;

    private final Map<Integer, List<int[]>> posts = new HashMap<>();
    private final Map<Integer, Set<Integer>> following = new HashMap<>();
    private int clock;

    public void post(int user, int postId) {
        posts.computeIfAbsent(user, key -> new ArrayList<>()).add(new int[]{clock++, postId});
    }

    public void follow(int follower, int followee) {
        if (follower != followee) {
            following.computeIfAbsent(follower, key -> new HashSet<>()).add(followee);
        }
    }

    public void unfollow(int follower, int followee) {
        Set<Integer> followees = following.get(follower);
        if (followees != null) {
            followees.remove(followee);
        }
    }

    public List<Integer> feed(int user) {
        int[] times = new int[LIMIT];
        int[] ids = new int[LIMIT];
        int count = collect(Integer.valueOf(user), times, ids, 0);
        Set<Integer> followees = following.get(user);
        if (followees != null) {
            for (Integer followee : followees) {
                count = collect(followee, times, ids, count);
            }
        }
        List<Integer> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(ids[i]);
        }
        return result;
    }

    private int collect(Integer author, int[] times, int[] ids, int count) {
        List<int[]> written = posts.get(author);
        if (written == null) {
            return count;
        }
        for (int i = written.size() - 1; i >= 0; i--) {
            int time = written.get(i)[0];
            if (count == LIMIT && written.get(i)[1] < ids[LIMIT - 1]) {
                break;
            }
            int slot = count == LIMIT ? LIMIT - 1 : count++;
            while (slot > 0 && ids[slot - 1] < written.get(i)[1]) {
                times[slot] = times[slot - 1];
                ids[slot] = ids[slot - 1];
                slot--;
            }
            times[slot] = time;
            ids[slot] = written.get(i)[1];
        }
        return count;
    }
}
