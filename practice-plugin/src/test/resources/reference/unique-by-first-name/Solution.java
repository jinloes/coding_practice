package com.jinloes.practice;
import java.util.ArrayList; import java.util.Comparator; import java.util.LinkedHashMap; import java.util.List; import java.util.Map;
public class Solution { public record Person(String first,String last) {} public static List<Person> uniqueByFirstName(List<Person> people){ Map<String,Person> firstSeen=new LinkedHashMap<>(); for(Person p:people) firstSeen.putIfAbsent(p.first(),p); List<Person> result=new ArrayList<>(firstSeen.values()); result.sort(Comparator.comparing(Person::first)); return result; } }
