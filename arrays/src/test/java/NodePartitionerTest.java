import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NodePartitionerTest {
    @Test
    public void testCompute() {
        List<LinkedList<Integer>> buckets = NodePartitioner.compute(Lists.newLinkedList(
                Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8)));
        assertThat(buckets.get(0)).contains(1, 3, 5, 7);
        assertThat(buckets.get(1)).contains(2, 4, 6, 8);
    }
}
