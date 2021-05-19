import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ReverseSublistTest {

    private static Stream<Arguments> lists() {
        return Stream.of(
                Arguments.of(
                        new ListNode<>(1, new ListNode<>(2, new ListNode<>(3, new ListNode<>(4, new ListNode<>(5))))),
                        new ListNode<>(1, new ListNode<>(4, new ListNode<>(3, new ListNode<>(2, new ListNode<>(5))))))

        );
    }

    @ParameterizedTest
    @MethodSource("lists")
    void reverse(ListNode<Integer> head, ListNode<Integer> expected) {
        assertThat(ReverseSublist.reverse(head, 2, 4)).isEqualTo(expected);
    }
}