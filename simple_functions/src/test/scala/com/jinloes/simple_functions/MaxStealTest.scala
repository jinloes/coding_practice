package com.jinloes.simple_functions

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

/**
 * Tests for {@link MaxSteal}
 */
@RunWith(classOf[JUnitRunner])
class MaxStealTest extends FeatureSpec with Matchers with GivenWhenThen {
  feature("A thief should be able to steal the max values from a houses") {
    scenario("A thief should steal from 1 house") {
      Given("one house")
      val arr = Array(3)
      When("the thief steals")
      val max = MaxSteal.steal(arr)
      Then("The max amount the thief can steal is 3")
      max should be(3)
    }

    scenario("A thief should steal from 2 houses") {
      Given("one house")
      val arr = Array(3, 5)
      When("the thief steals")
      val max = MaxSteal.steal(arr)
      Then("The max amount the thief can steal is 5")
      max should be(5)
    }

    scenario("A thief should steal from 4 houses") {
      Given("one house")
      val arr = Array(6, 1, 2, 7)
      When("the thief steals")
      val max = MaxSteal.steal(arr)
      Then("The max amount the thief can steal is 13")
      max should be(13)
    }
  }
}
