package com.jinloes.simple_functions;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MeetingRooms2Test {
    private MeetingRooms2 meetingRooms2;

    @Before
    public void setUp() throws Exception {
        meetingRooms2 = new MeetingRooms2();
    }

    @Test
    public void minMeetingRooms() {
        int[][] internals = new int[][]{{2, 15}, {36, 45}, {9, 29}, {16, 23}, {4, 9}};
        assertThat(meetingRooms2.minMeetingRooms(internals)).isEqualTo(2);
    }
}