import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ComputeMnemonicsTest {

    @Test
    void compute() {
        assertThat(ComputeMnemonics.compute("2276696"))
                .allMatch(s -> {
                    assertThat(s.charAt(0)).isBetween('A', 'C');
                    assertThat(s.charAt(1)).isBetween('A', 'C');
                    assertThat(s.charAt(2)).isBetween('P', 'S');
                    assertThat(s.charAt(3)).isBetween('M', 'O');
                    assertThat(s.charAt(4)).isBetween('M', 'O');
                    assertThat(s.charAt(5)).isBetween('W', 'Z');
                    assertThat(s.charAt(6)).isBetween('M', 'O');
                    return true;
                });


    }
}