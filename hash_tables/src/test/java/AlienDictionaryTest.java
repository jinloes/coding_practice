import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AlienDictionaryTest {

    public static Stream<Arguments> dictionaries() {
        return Stream.of(
                Arguments.of(new String[]{"hello", "leetcode"}, "hlabcdefgijkmnopqrstuvwxyz", true),
                Arguments.of(new String[]{"app", "apple"}, "abcdefghijklmnopqrstuvwxyz", true)
        );
    }

    @ParameterizedTest
    @MethodSource("dictionaries")
    void isAlienSorted(String[] words, String alphabet, boolean expected) {
        assertThat(AlienDictionary.isAlienSorted(words, alphabet))
                .isEqualTo(expected);
    }
}