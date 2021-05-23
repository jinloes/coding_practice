import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class MergeSortedArraysTest {

    @Test
    void merge() {
        List<Integer> list1 = Lists.newArrayList(5, 13, 17, null, null, null, null, null);
        List<Integer> list2 = Lists.newArrayList(3, 7, 11, 19);

        MergeSortedArrays.merge(list1, 3, list2, 4);

        assertThat(list1).containsExactly(3, 5, 7, 11, 13, 17, 19, null);
    }
}