package com.jinloes.simple_functions;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Given an array of meeting time intervals consisting of start and end times [[s1,e1],[s2,e2],...] find the
 * minimum number of conference rooms required.
 */
public class MeetingRooms2 {

    public int minMeetingRooms(int[][] intervals) {
        Arrays.sort(intervals, Comparator.comparingInt(o -> o[0]));
        int roomsRequired = 1;

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i - 1][1] > intervals[i][0]) {
                roomsRequired++;
            } else if (intervals[i - 1][1] < intervals[i][0]) {
                roomsRequired--;
            }
        }

        return roomsRequired;

    }
}
