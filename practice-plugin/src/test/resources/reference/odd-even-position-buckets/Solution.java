package com.jinloes.practice;
import java.util.ArrayList; import java.util.List;
public class Solution { public static class Node { public int value; public Node next; public Node(int value, Node next){ this.value=value; this.next=next; } } public static List<List<Integer>> bucketByPosition(Node head){ List<Integer> even=new ArrayList<>(); List<Integer> odd=new ArrayList<>(); int index=0; for(Node n=head;n!=null;n=n.next,index++) (index%2==0?even:odd).add(n.value); return List.of(even, odd); } }
