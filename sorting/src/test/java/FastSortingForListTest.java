import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FastSortingForListTest {

    @Test
    void sort() {
        ListNode<Integer> root = new ListNode<>(50, new ListNode<>(4, new ListNode<>(0, new ListNode<>(20, new ListNode<>(15, null)))));

        ListNode<Integer> result = FastSortingForList.sort(root);


        List<Integer> finalResult = new ArrayList<>();
        while (result != null) {
            finalResult.add(result.data);
            result = result.next;
        }

        assertThat(finalResult).containsExactly(0, 4, 15, 20, 50);
    }
}