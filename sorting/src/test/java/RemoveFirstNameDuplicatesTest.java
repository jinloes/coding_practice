import com.google.common.collect.Lists;
import io.vavr.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RemoveFirstNameDuplicatesTest {

    @Test
    void eliminateDuplicate() {
        List<Tuple2<String, String>> names = Lists.newArrayList(
                new Tuple2<>("Ian", "Botham"),
                new Tuple2<>("David", "Gower"),
                new Tuple2<>("Ian", "Bell"),
                new Tuple2<>("Ian", "Chappell")
        );

        RemoveFirstNameDuplicates.eliminateDuplicate(names);

        assertThat(names).containsExactly(new Tuple2<>("David", "Gower"), new Tuple2<>("Ian", "Botham"));

    }
}