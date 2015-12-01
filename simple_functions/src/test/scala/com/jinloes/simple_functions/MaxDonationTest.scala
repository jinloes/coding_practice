package com.jinloes.simple_functions

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

/**
  * Tests for {@link MaxDonation}
  */
@RunWith(classOf[JUnitRunner])
class MaxDonationTest extends FeatureSpec with Matchers with GivenWhenThen {
  feature("Calculate the max donation") {
    scenario("a simple neighborhood") {
      Given("a simple neighborhood")
      val arr = Array(11, 15)
      When("calculate the max donation")
      val max = MaxDonation.getMax(arr)
      val maxAlt = MaxDonation.getMaxAlternative(arr)
      Then("the max should be 15")
      max should be(15)
      maxAlt should be(15)
    }
    scenario("a more complex neighborhood") {
      Given("a more complex neighborhood")
      var arr = Array(10, 3, 2, 5, 7, 8)
      When("calculate the max donation")
      var max = MaxDonation.getMax(arr)
      var maxAlt = MaxDonation.getMaxAlternative(arr)
      Then("the max should be 19")
      max should be(19)
      maxAlt should be(19)

      Given("a more complex neighborhood")
      arr = Array(7, 7, 7, 7, 7, 7, 7)
      When("calculate the max donation")
      max = MaxDonation.getMax(arr)
      maxAlt = MaxDonation.getMaxAlternative(arr)
      Then("the max should be 21")
      max should be(21)
      maxAlt should be(21)
    }

    scenario("a complex neighborhood") {
      Given("a complex neighborhood")
      val arr = Array(1, 2, 3, 4, 5, 1, 2, 3, 4, 5)
      When("calculate the max donation")
      val max = MaxDonation.getMax(arr)
      val maxAlt = MaxDonation.getMaxAlternative(arr)
      Then("the max should be 16")
      max should be(16)
      maxAlt should be(16)
    }
  }

}
