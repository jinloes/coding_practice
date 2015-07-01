import javafx.util.Pair

import com.jinloes.simple_functions.TwoSum
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

import scala.collection.JavaConverters._

/**
 * Tests for {@link TwoSum}.
 */
class TwoSumTest extends FeatureSpec with Matchers with GivenWhenThen {
  feature("Two should should find all the pairs of numbers that sum to a value") {
    scenario("an empty array") {
      Given("an empty array")
      val arr:Array[Int] = Array()
      When("find the 2 sum")
      val actual = TwoSum.find2Sum(arr, 3)
      Then("the pairs should be found")
      actual should be(Set().asJava)
    }
    scenario("a simple array") {
      Given("a simple array")
      val arr = Array(1, 2)
      When("find the 2 sum")
      val actual = TwoSum.find2Sum(arr, 3)
      Then("the pairs should be found")
      actual should be(Set(new Pair(1, 2)).asJava)
    }

    scenario("a more complex array") {
      Given("a simple array")
      val arr = Array(3, 1, 2)
      When("find the 2 sum")
      val actual = TwoSum.find2Sum(arr, 6)
      Then("the pairs should be found")
      actual should be(Set().asJava)
    }

    scenario("an array with duplicates") {
      Given("a simple array")
      val arr = Array(6, 1, 4, 3, 1, 7, 3)
      When("find the 2 sum")
      val actual = TwoSum.find2Sum(arr, 6)
      Then("the pairs should be found")
      actual should be(Set(new Pair(3, 3)).asJava)
    }

    scenario("a complex array") {
      Given("a simple array")
      val arr = Array(3, 1, 2, 4, 5, 0)
      When("find the 2 sum")
      val actual = TwoSum.find2Sum(arr, 5)
      Then("the pairs should be found")
      actual should be(Set(new Pair(2, 3), new Pair(1, 4), new Pair(0, 5)).asJava)
    }
  }
}
