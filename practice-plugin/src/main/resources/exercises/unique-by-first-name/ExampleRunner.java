package com.jinloes.practice;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
public final class ExampleRunner {
    private static final String USAGE = "Input: first:last pairs separated by spaces, for example: Ada:Lovelace Bob:Stone Ada:Byron";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args).trim()); return; } assertThat(Solution.uniqueByFirstName(List.of(p("Ada","Lovelace"),p("Bob","Stone"),p("Ada","Byron")))).containsExactly(p("Ada","Lovelace"),p("Bob","Stone")); assertThat(Solution.uniqueByFirstName(List.of(p("Zoe","Zed"),p("Amy","Able"),p("Zoe","Young")))).containsExactly(p("Amy","Able"),p("Zoe","Zed")); assertThat(Solution.uniqueByFirstName(List.of())).isEmpty(); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { try { System.out.println(Solution.uniqueByFirstName(parse(input))); } catch (IllegalArgumentException exception) { System.out.println(USAGE); } }
    private static List<Solution.Person> parse(String input) { if (input.isBlank()) return List.of(); List<Solution.Person> people=new ArrayList<>(); for(String token:input.split("\\s+")){ String[] parts=token.split(":",-1); if(parts.length!=2||parts[0].isBlank()||parts[1].isBlank()) throw new IllegalArgumentException(); people.add(p(parts[0],parts[1])); } return people; }
    private static Solution.Person p(String first,String last){ return new Solution.Person(first,last); }
}
