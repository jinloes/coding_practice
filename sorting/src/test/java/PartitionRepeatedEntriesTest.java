import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PartitionRepeatedEntriesTest {

    @Test
    void rearrange() {
        List<Integer> entries = Lists.newArrayList(
                2, 1, 3, 2, 4, 1, 2, 4
        );

        PartitionRepeatedEntries.rearrange(entries);

        assertThat(entries).containsExactlyInAnyOrder(1);
    }
}