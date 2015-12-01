package com.jinloes.simple_functions

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class MinCoinsTest extends FlatSpec with Matchers {

  "Find min coins" should "return the minimum number of coins equal a sum" in {
    MinCoins.findMinCoins(0, Array(1, 3, 5)) should be (0)
    MinCoins.findMinCoins(1, Array(1, 3, 5)) should be (1)
    MinCoins.findMinCoins(3, Array(1, 3, 5)) should be (1)
    MinCoins.findMinCoins(5, Array(1, 3, 5)) should be (1)
    MinCoins.findMinCoins(4, Array(1, 3, 5)) should be (2)
    MinCoins.findMinCoins(11, Array(1, 3, 5)) should be (3)
    MinCoins.findMinCoins(15, Array(1, 3, 5)) should be (3)
    MinCoins.findMinCoins(21, Array(1, 3, 5)) should be (5)

    MinCoins.findMinCoins(16, Array(1, 2, 4, 6)) should be (3)
    MinCoins.findMinCoins(35, Array(1, 2, 4, 6)) should be (7)
  }

  "Find min coins alternative" should "return the minimum number of coins equal a sum" in {
    MinCoins.findMinCoinsAlternative(0, Array(1, 3, 5)) should be(0)
    MinCoins.findMinCoinsAlternative(1, Array(1, 3, 5)) should be(1)
    MinCoins.findMinCoinsAlternative(3, Array(1, 3, 5)) should be(1)
    MinCoins.findMinCoinsAlternative(5, Array(1, 3, 5)) should be(1)
    MinCoins.findMinCoinsAlternative(4, Array(1, 3, 5)) should be(2)
    MinCoins.findMinCoinsAlternative(11, Array(1, 3, 5)) should be(3)
    MinCoins.findMinCoinsAlternative(15, Array(1, 3, 5)) should be(3)
    MinCoins.findMinCoinsAlternative(21, Array(1, 3, 5)) should be(5)

    MinCoins.findMinCoinsAlternative(16, Array(1, 2, 4, 6)) should be(3)
    MinCoins.findMinCoinsAlternative(35, Array(1, 2, 4, 6)) should be(7)
    MinCoins.findMinCoinsAlternative(1, Array(2, 3)) should be(0)
  }
}