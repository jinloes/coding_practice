package com.jinloes.simple_functions;

import java.util.Arrays;

/**
 * Consider a big party where a log register for guest’s entry and exit times is maintained.
 * Find the time at which there are maximum guests in the party. Note that entries in register are not in any order.
 * Example :
 * <p>
 * Input: arrl[] = {1, 2, 9, 5, 5}
 * exit[] = {4, 5, 12, 9, 12}
 * First guest in array arrives at 1 and leaves at 4,
 * second guest arrives at 2 and leaves at 5, and so on.
 * <p>
 * Output: 5
 * There are maximum 3 guests at time 5.
 */
public class MaxGuestTime {

    public int maxGuests(int[] arrive, int[] exit) {
        Arrays.sort(arrive);
        Arrays.sort(exit);

        int i = 0;
        int j = 0;
        int guests = 0;
        int maxGuests = 0;
        int maxTime = 0;

        while (i < exit.length && j < exit.length) {

            if (arrive[i] <= exit[j]) {
                // A guest arrivd
                guests++;

                if (guests > maxGuests) {
                    maxGuests = guests;
                    maxTime = arrive[i];
                }
                i++;
            } else {
                // A guest left
                guests--;
                j++;
            }
        }

        return maxTime;
    }
}
