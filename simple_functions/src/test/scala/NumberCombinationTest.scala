import com.jinloes.simple_functions.NumberCombination
import org.scalatest.{Matchers, FlatSpec}

/**
 * Tests for {@link NumberCombination}
 */
class NumberCombinationTest extends FlatSpec with Matchers {
  "A number combination detector" should "detect if a sum exists in an array " in {
    NumberCombination.findCombo(Array(15, 5, 3, 1), 8) should be(true)
    NumberCombination.findCombo(Array(15, 5, 3, 1), 9) should be(true)
  }

  "A number combination detector" should "return false if a number does not exist in the array" in {
    NumberCombination.findCombo(Array(15, 5, 3, 1), 10) should be(false)
    NumberCombination.findCombo(Array(15, 5, 3, 2), 1) should be(false)
    NumberCombination.findCombo(Array(15, 5, 3, 1), 17) should be(false)
  }

}
