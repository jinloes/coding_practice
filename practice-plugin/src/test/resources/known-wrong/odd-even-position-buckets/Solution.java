package com.jinloes.practice;
import java.util.ArrayList; import java.util.List;
public class Solution { public static class Node { public int value; public Node next; public Node(int value, Node next){ this.value=value; this.next=next; } } public static List<List<Integer>> bucketByPosition(Node head){ List<Integer> all=new ArrayList<>(); for(Node n=head;n!=null;n=n.next) all.add(n.value); return List.of(all, List.of()); } }
