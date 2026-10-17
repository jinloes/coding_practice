package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void emptyStringIsValid(){ assertThat(Solution.reorganize("")).isEmpty(); }
    @Test
    void singleCharacterReturnsItself(){ assertThat(Solution.reorganize("z")).isEqualTo("z"); }
    @Test
    void balancesSeveralCharacters(){ assertValidReorganization("aaabbc",Solution.reorganize("aaabbc")); } private static void assertValidReorganization(String input,String output){assertThat(output).hasSize(input.length());assertThat(sorted(output)).isEqualTo(sorted(input));for(int i=1;i<output.length();i++)assertThat(output.charAt(i)).as("adjacent characters at "+i).isNotEqualTo(output.charAt(i-1));} private static String sorted(String text){return text.chars().sorted().collect(StringBuilder::new,StringBuilder::appendCodePoint,StringBuilder::append).toString();} }


