package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void keepsEarliestEntryWhenSeveralLastNamesShareAFirstName(){ assertThat(Solution.uniqueByFirstName(List.of(p("Mia","One"), p("Mia","Two"), p("Mia","Three")))).containsExactly(p("Mia","One")); }
    @Test
    void sortsLexicographicallyByFirstNameOnly(){ assertThat(Solution.uniqueByFirstName(List.of(p("Cam","B"), p("Ann","Z"), p("Ben","A")))).containsExactly(p("Ann","Z"), p("Ben","A"), p("Cam","B")); }
    @Test
    void doesNotMutateInputList(){ List<Solution.Person> people=new ArrayList<>(List.of(p("Lee","A"),p("Lee","B"),p("Ari","C"))); List<Solution.Person> before=List.copyOf(people); Solution.uniqueByFirstName(people); assertThat(people).containsExactlyElementsOf(before); }
    @Test
    void handlesAlreadyUniqueNames(){ assertThat(Solution.uniqueByFirstName(List.of(p("Noa","N"), p("Omar","O")))).containsExactly(p("Noa","N"), p("Omar","O")); }
    @Test
    void handlesSeededLargeDirectory(){ Random random=new Random(7); List<Solution.Person> people=new ArrayList<>(); for(int i=0;i<200;i++) people.add(p("Name"+random.nextInt(40),"Last"+i)); List<Solution.Person> result=Solution.uniqueByFirstName(people); assertThat(result).extracting(Solution.Person::first).doesNotHaveDuplicates().isSorted(); for(Solution.Person person:result) assertThat(people.indexOf(person)).isEqualTo(firstIndexOf(people, person.first())); }
    private static int firstIndexOf(List<Solution.Person> people,String first){ for(int i=0;i<people.size();i++) if(people.get(i).first().equals(first)) return i; return -1; }
    private static Solution.Person p(String first,String last){ return new Solution.Person(first,last); }
}
