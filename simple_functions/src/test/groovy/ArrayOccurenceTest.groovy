import com.jinloes.simple_functions.ArrayOccurence
import spock.lang.Specification

/**
 * Spock unit tests for {@link ArrayOccurence}
 */
class ArrayOccurenceTest extends Specification {
    def "Get the number of occurrences for an empty array"() {
        expect:
        ArrayOccurence.findOccurrences([] as int[], 5) == 0
    }

    def "Get the number of occurrences for a null array"() {
        expect:
        ArrayOccurence.findOccurrences(null, 5) == 0
    }

    def "#num should occur #times in #arr"() {
        expect:
        ArrayOccurence.findOccurrences(arr as int[], num) == times
        where:
        arr                                                      || num || times
        [5]                                                      || 5   || 1
        [1, 1, 4, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 12, 13, 13] || 6   || 3
        [1, 1, 4, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 12, 13, 13] || 1   || 2
        [1, 1, 4, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 12, 13, 13] || 13  || 2
        [1, 1, 4, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 12, 13, 13] || 4   || 4
        [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]            || 1   || 15
        [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]            || 2   || 0
    }
}
