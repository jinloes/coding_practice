package com.jinloes.design.most_popular;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MostPopularImpl implements MostPopular {
    private class PopularCountNode {
        PopularCountNode previous;
        PopularCountNode next;
        int count;
        Set<Integer> contentIds;

        public PopularCountNode(int count) {
            this.count = count;
            this.contentIds = new HashSet<>();
        }
    }
    private Map<Integer, PopularCountNode> contentToNode;
    private PopularCountNode head;
    private PopularCountNode tail;

    public MostPopularImpl() {
        this.contentToNode = new HashMap<>();
        this.head = new PopularCountNode(-1);
        this.tail = new PopularCountNode(-1);
        head.previous = tail;
        tail.next = head;
    }

    @Override
    public void increasePopularity(Integer contentId) {
        if(contentToNode.containsKey(contentId)) {
            PopularCountNode node = contentToNode.get(contentId);
            node.contentIds.remove(contentId);

            if(node.next.count == node.count + 1) {
                node.next.contentIds.add(contentId);
                contentToNode.put(contentId, node.next);
            } else {
                PopularCountNode newNode = new PopularCountNode(node.count + 1);
                newNode.contentIds.add(contentId);

                PopularCountNode next = node.next;
                node.next = newNode;
                newNode.previous = node;

                next.previous = newNode;
                newNode.next = next;

                contentToNode.put(contentId, newNode);
            }
        } else {
            if(tail.next.count == 1) {
                tail.next.contentIds.add(contentId);
                contentToNode.put(contentId, tail.next);
            } else {
                PopularCountNode newNode = new PopularCountNode(1);
                newNode.contentIds.add(contentId);
                PopularCountNode next = tail.next;

                tail.next = newNode;
                newNode.next = next;

                next.previous = newNode;
                newNode.previous = tail;

                contentToNode.put(contentId, newNode);
            }
        }

    }

    @Override
    public Integer mostPopular() {
        if(head.previous != tail) {
            return head.previous.contentIds.iterator().next();
        } else {
            return -1;
        }
    }

    @Override
    public void decreasePopularity(Integer contentId) {
        if(!contentToNode.containsKey(contentId)) {
            return;
        }

        PopularCountNode node = contentToNode.get(contentId);
        node.contentIds.remove((contentId));

        if(node.count > 1) {
            if (node.count - 1 == node.previous.count) {
                PopularCountNode previous = node.previous;
                previous.contentIds.add(contentId);

                contentToNode.put(contentId, previous);
            } else {
                PopularCountNode newNode = new PopularCountNode(node.count - 1);
                PopularCountNode tmp = node.previous;

                node.previous = newNode;
                newNode.next = node;

                newNode.previous = tmp;
                node.previous.next = newNode;
                contentToNode.put(contentId, newNode);
            }
        }

        if (node.contentIds.isEmpty()) {
            node.previous.next = node.next;
            node.next.previous = node.previous;
        }
    }
}
