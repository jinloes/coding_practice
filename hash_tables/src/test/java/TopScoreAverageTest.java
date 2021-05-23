import io.vavr.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TopScoreAverageTest {

    @Test
    void findStudentWithHighestBestOfThreeScores() {
        List<Tuple2<String, Integer>> data = List.of(
                new Tuple2<>("Adam", 97),
                new Tuple2<>("Adam", 91),
                new Tuple2<>("Adam", 96),
                new Tuple2<>("Adam", 88),
                new Tuple2<>("Bob", 91),
                new Tuple2<>("Bob", 92),
                new Tuple2<>("Bob", 93),
                new Tuple2<>("Sam", 100)
        );

        assertThat(TopScoreAverage.findStudentWithHighestBestOfThreeScores(data)).isEqualTo("Adam");
    }
}