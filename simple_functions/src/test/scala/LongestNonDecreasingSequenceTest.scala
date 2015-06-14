import com.jinloes.simple_functions.LongestNonDecreasingSequence
import org.scalatest.{Matchers, FlatSpec}

/**
 * Tests for {@link LongestNonDecreasingSequence}.
 */
class LongestNonDecreasingSequenceTest extends FlatSpec with Matchers {

  "Find the length of the longest non decreasing sequence" should "return the longest non decreasing sequence" in {
    LongestNonDecreasingSequence.findLongestNonDecreasingSequence(Array()) should be(0)
    LongestNonDecreasingSequence.findLongestNonDecreasingSequence(Array(1)) should be(1)
    LongestNonDecreasingSequence.findLongestNonDecreasingSequence(Array(1, 2, 3)) should be(3)
    LongestNonDecreasingSequence.findLongestNonDecreasingSequence(Array(5, 3, 4, 8, 6, 7)) should be(3)
    LongestNonDecreasingSequence.findLongestNonDecreasingSequence(
      Array(5, 6, 7, 8, 3, 4, 8, 9, 10, 11, 12, 6, 7, 8, 9)) should be(7)
    LongestNonDecreasingSequence.findLongestNonDecreasingSequence(
      Array(1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 1, 2, 3, 4, 5, 1, 2, 3)) should be(10)
  }
}
