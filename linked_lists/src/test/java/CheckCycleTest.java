import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CheckCycleTest {

    private static Stream<Arguments> lists() {
        ListNode<Integer> n1 = new ListNode<>(1);
        ListNode<Integer> n2 = new ListNode<>(2);
        ListNode<Integer> n3 = new ListNode<>(3);
        ListNode<Integer> n4 = new ListNode<>(4);
        ListNode<Integer> n5 = new ListNode<>(5);

        n1.next = n2;
        n2.next = n3;
        n3.next = n4;
        n4.next = n5;
        n5.next = n1;

        return Stream.of(
                Arguments.of(n1, n1),
                Arguments.of(new ListNode<>(1, new ListNode<>(2, new ListNode<>(3, new ListNode<>(4, new ListNode<>(5))))),
                        null)

        );
    }

    @ParameterizedTest
    @MethodSource("lists")
    void check(ListNode<Integer> head, ListNode<Integer> expected) {
        assertThat(CheckCycle.check(head)).isSameAs(expected);
    }
}