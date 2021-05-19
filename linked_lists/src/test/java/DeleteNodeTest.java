import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteNodeTest {

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

        return Stream.of(
                Arguments.of(n1, n3)
        );
    }

    @ParameterizedTest
    @MethodSource("lists")
    void delete(ListNode<Integer> head, ListNode<Integer> toDelete) {
        assertThat(DeleteNode.delete(head, toDelete))
                .satisfies(current -> {
                    while (current != null) {
                        assertThat(current).isNotEqualTo(toDelete);
                        current = current.next;
                    }
                });
    }
}