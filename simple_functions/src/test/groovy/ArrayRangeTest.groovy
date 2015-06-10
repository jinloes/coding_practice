import com.jinloes.simple_functions.ArrayRange
import spock.lang.Specification

/**
 * Spock unit tests for {@link ArrayRange}
 */
class ArrayRangeTest extends Specification {
    def "Calculating ranges of an empty array"() {
        expect: ArrayRange.calculateRanges([] as int[]) == []
    }

    def "Calculating ranges of a null array"() {
        expect: ArrayRange.calculateRanges(null) == []
    }

    def "Calculating ranges on a sorted array"() {
        expect: ArrayRange.calculateRanges([0, 1, 2, 7, 21, 22, 1098, 1099, 2000] as int[]) ==
                ["0-2", "7", "21-22", "1098-1099", "2000"]
    }
}
