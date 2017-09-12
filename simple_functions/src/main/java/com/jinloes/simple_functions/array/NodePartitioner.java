package com.jinloes.simple_functions.array;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Given a linked list, group the odd numbered nodes ( not value) and even numbered nodes together.
 */
public class NodePartitioner {
    public static List<LinkedList<Integer>> compute(LinkedList<Integer> list) {
        List<LinkedList<Integer>> buckets = new ArrayList<>(2);
        buckets.add(new LinkedList<>());
        buckets.add(new LinkedList<>());
        for(int i = 0; i < list.size(); i++) {
            if(i % 2 == 0) {
                buckets.get(0).add(list.get(i));
            } else {
                buckets.get(1).add(list.get(i));
            }
        }
        return buckets;
    }
}
