import com.jinloes.simple_functions.LongestNonDecreasingSequence
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

/**
 * Tests for {@link LongestNonDecreasingSequence}.
 */
class LongestNonDecreasingSequenceTest extends FeatureSpec with Matchers with GivenWhenThen {

  feature("Find the length of the longest increasing sequence") {
    scenario("simple array") {
      Given("a simple array")
      val arr = Array(5, 2)
      When("get longest increasing sequence")
      val sequence = LongestNonDecreasingSequence.findLongestIncreasingSequence(arr)
      Then("the longest sequence should be 1")
      sequence should be(1)
    }

    scenario("complex array") {
      Given("a complex array")
      val arr = Array(1, 3, 4, 1)
      When("get longest increasing sequence")
      val sequence = LongestNonDecreasingSequence.findLongestIncreasingSequence(arr)
      Then("the longest sequence should be 3")
      sequence should be(3)
    }

    scenario("more complex array") {
      Given("a more complex array")
      val arr = Array(7, 2, 3, 1, 5, 8, 9, 6)
      When("get longest increasing sequence")
      val sequence = LongestNonDecreasingSequence.findLongestIncreasingSequence(arr)
      Then("the longest sequence should be 5")
      sequence should be(5)
    }
  }
  feature("Find the length of the longest consecutive non decreasing sequence") {
    scenario("Find the longest consecutive sequence with simple arrays") {
      Given("an empty array")
      var arr: Array[Int] = Array()
      When("get longest sequence")
      var length = LongestNonDecreasingSequence.findLongestNonDecreasingSequence(arr)
      Then("the length should be 0")
      length should be(0)

      Given("an array of size 1")
      arr = Array(1)
      When("get longest sequence")
      length = LongestNonDecreasingSequence.findLongestNonDecreasingSequence(arr)
      Then("the length should be 1")
      length should be(1)

      Given("a simple array ")
      arr = Array(1, 2, 3)
      When("get longest sequence")
      length = LongestNonDecreasingSequence.findLongestNonDecreasingSequence(arr)
      Then("the length should be 3")
      length should be(3)
    }

    scenario("Find the longest sequence with a more complex array") {
      Given("a complex array")
      var arr = Array(5, 3, 4, 8, 6, 7)
      When("get longest sequence")
      var length = LongestNonDecreasingSequence.findLongestNonDecreasingSequence(arr)
      Then("the length should be 3")
      length should be(3)

      Given("a more complex array")
      arr = Array(5, 6, 7, 8, 3, 4, 8, 9, 10, 11, 12, 6, 7, 8, 9)
      When("get longest sequence")
      length = LongestNonDecreasingSequence.findLongestNonDecreasingSequence(arr)
      Then("the length should be 7")
      length should be(7)

      Given("a more complex array")
      arr = Array(1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 1, 2, 3, 4, 5, 1, 2, 3)
      When("get longest sequence")
      length = LongestNonDecreasingSequence.findLongestNonDecreasingSequence(arr)
      Then("the length should be 10")
      length should be(10)
    }
  }

}
