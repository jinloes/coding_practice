package com.jinloes.practice;
import java.util.ArrayList; import java.util.Comparator; import java.util.List;
public class Solution { public record Person(String first,String last) {} public static List<Person> uniqueByFirstName(List<Person> people){ List<Person> copy=new ArrayList<>(people); copy.sort(Comparator.comparing(Person::first)); return copy; } }
