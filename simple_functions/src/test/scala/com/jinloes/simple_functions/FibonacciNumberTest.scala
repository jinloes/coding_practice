package com.jinloes.simple_functions

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

/**
  * Test for {@link FibonacciNumber}
  */
@RunWith(classOf[JUnitRunner])
class FibonacciNumberTest extends FeatureSpec with Matchers with GivenWhenThen {
  feature("Calculate a fibonacci number") {
    scenario("small fibonacci number") {
      When("calculate 2nd fibonacci number")
      val num = FibonacciNumber.getNumber(2)
      Then("1 should be returned")
      num should be(1)
    }

    scenario("larger fibonacci number") {
      When("calculate 13th fibonacci numeber")
      val num = FibonacciNumber.getNumber(13)
      Then("144 should be returned")
      num should be(144)
    }
  }
}
