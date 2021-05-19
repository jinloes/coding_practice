import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MergeSortedListsTest {


    private static Stream<Arguments> lists() {
        return Stream.of(
                Arguments.of(new ListNode<>(1, new ListNode<>(2, new ListNode<>(5))),
                        new ListNode<>(2, new ListNode<>(3)),
                        List.of(1, 2, 2, 3, 5))

        );
    }

    @ParameterizedTest
    @MethodSource("lists")
    void merge(ListNode<Integer> n1, ListNode<Integer> n2, List<Integer> expected) {

        ListNode<Integer> current = MergeSortedLists.merge(n1, n2);
        List<Integer> actual = new ArrayList<>();

        while (current != null) {
            actual.add(current.data);
            current = current.next;
        }
        assertThat(actual).isEqualTo(expected);
    }
}