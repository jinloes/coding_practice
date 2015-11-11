import com.jinloes.simple_functions.MaxSum
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

/**
 * Tests for {@link MaxSum}
 */
class MaxSumTest extends FeatureSpec with Matchers with GivenWhenThen {
  feature("Find the max sum in an array in which numbers can be positive or negative") {
    scenario("Find the max sum in a simple array") {
      Given("a simple array")
      val arr = Array(-1, 2)
      When("find max sum")
      val max = MaxSum.findMax(arr)
      Then("max sum should be 2")
      max should be (2)
    }

    scenario("Find the max sum in a more complex array") {
      Given("A more complex array")
      val arr = Array(1, -2, 3, 10, -4, 7, 2, -5)
      When("find the max sum")
      val max = MaxSum.findMax(arr)
      Then("max sum should be 18")
      max should be(18)
    }
  }

  feature("Find the max sum in a 2d array in which you can only go right or down") {
    scenario("Find max sum in a simple array") {
      Given("a simple array")
      val arr = Array(Array(2, 0), Array(3, 1))
      When("find max sum")
      val max = MaxSum.findMax(arr)
      Then("the max sum should be 6")
      max should be(6)
    }
    scenario("Find max sum in a more complex array") {
      Given("a more complex array")
      val arr = Array(Array(5, 8, 12, 1), Array(9, 10, 14, 5), Array(2, 3, 10, 8), Array(11, 10, 2, 6))
      When("find max sum")
      val max = MaxSum.findMax(arr)
      Then("the max sum should be 63")
      max should be(63)
    }
  }
}
