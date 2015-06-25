import com.jinloes.simple_functions.ZigZagSequence
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

/**
 * Tests for {@link ZigZagSequence}
 */
class ZigZagSequenceTest extends FeatureSpec with Matchers with GivenWhenThen {
  feature("find the longest zigzag sequence in an array") {
    scenario("simple array") {
      Given("a simple array")
      val arr = Array(44)
      When("find zigzag sequence")
      val len = ZigZagSequence.findLongest(arr)
      Then("the longest sequence should be 1")
      len should be(1)
    }
    scenario("a more complex array") {
      Given("a more complex array")
      var arr = Array(1, 17, 5, 10, 13, 15, 10, 5, 16, 8)
      When("find zigzag sequence")
      var len = ZigZagSequence.findLongest(arr)
      Then("the longest sequence should be 7")
      len should be(7)

      Given("a more complex array")
      arr = Array(1, 7, 4, 9, 2, 5)
      When("find zigzag sequence")
      len = ZigZagSequence.findLongest(arr)
      Then("the longest sequence should be 7")
      len should be(6)

      Given("a more complex array")
      arr = Array(1, 2, 3, 4, 5, 6, 7, 8, 9)
      When("find zigzag sequence")
      len = ZigZagSequence.findLongest(arr)
      Then("the longest sequence should be 7")
      len should be(2)
    }

    scenario("a complex array") {
      Given("a complex array")
      var arr = Array(70, 55, 13, 2, 99, 2, 80, 80, 80, 80, 100, 19, 7, 5, 5, 5, 1000, 32, 32)
      When("find zigzag sequence")
      var len = ZigZagSequence.findLongest(arr)
      Then("the longest sequence should be 8")
      len should be(8)
      Given("a complex array")
      arr = Array(374, 40, 854, 203, 203, 156, 362, 279, 812, 955,
        600, 947, 978, 46, 100, 953, 670, 862, 568, 188,
        67, 669, 810, 704, 52, 861, 49, 640, 370, 908,
        477, 245, 413, 109, 659, 401, 483, 308, 609, 120,
        249, 22, 176, 279, 23, 22, 617, 462, 459, 244)
      When("find zigzag sequence")
      len = ZigZagSequence.findLongest(arr)
      Then("the longest sequence should be 36")
      len should be(36)

    }
  }
}
