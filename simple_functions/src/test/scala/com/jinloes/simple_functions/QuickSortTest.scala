package com.jinloes.simple_functions

import java.util

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

/**
  * Tests for {@link Sorter#quickSort}.
  */
@RunWith(classOf[JUnitRunner])
class QuickSortTest extends FlatSpec with Matchers with TableDrivenPropertyChecks {
  val arrs = Table("arr", Array[Int](3, 1, 2),
    Seq.fill(10)(Random.nextInt).toArray[Int],
    Seq.fill(10)(Random.nextInt).toArray[Int])

  "A sorter" should "be able to quick sort an array" in {
    forAll(arrs) {
      (arr: Array[Int]) =>
        val expected = arr.clone()
        util.Arrays.sort(expected)
        Sorter.quickSort(arr)
        arr should be(expected)
    }
  }

  it should "quick sort (return null) a null array" in {
    val arr: Array[Int] = null
    Sorter.quickSort(null)
    arr should be(null)
  }

  it should "quick sort (return an empty array) an empty array" in {
    val arr: Array[Int] = Array()
    Sorter.quickSort(arr)
    arr should have length 0
  }
}
