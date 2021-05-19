import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class OverlappingListsWithCycleTest {

    private static Stream<Arguments> lists() {
        ListNode<Integer> n3 = new ListNode<>(3, new ListNode<>(4, new ListNode<>(5)));

        ListNode<Integer> n1 = new ListNode<>(1, new ListNode<>(2, n3));
        ListNode<Integer> n2 = new ListNode<>(1, n3);


        n3.next.next.next = n2;


        return Stream.of(
                Arguments.of(n1, n2, n3)
        );
    }

    @ParameterizedTest
    @MethodSource("lists")
    void check(ListNode<Integer> n1, ListNode<Integer> n2, ListNode<Integer> expected) {
        assertThat(OverlappingListsWithCycle.check(n1, n2))
                .isEqualTo(expected);
    }
}