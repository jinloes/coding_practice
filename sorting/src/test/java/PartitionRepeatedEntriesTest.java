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

        // All elements preserved
        assertThat(entries).containsExactlyInAnyOrder(1, 1, 2, 2, 2, 3, 4, 4);
        // Equal elements are grouped together (no value appears non-consecutively)
        for (int i = 1; i < entries.size(); i++) {
            if (!entries.get(i).equals(entries.get(i - 1))) {
                for (int j = i + 1; j < entries.size(); j++) {
                    assertThat(entries.get(j)).isNotEqualTo(entries.get(i - 1));
                }
            }
        }
    }
}