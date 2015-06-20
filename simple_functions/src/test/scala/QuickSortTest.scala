import java.util

import com.jinloes.simple_functions.Sorting
import org.scalatest.{Matchers, FlatSpec}

import scala.util.Random

/**
 * Tests for {@link Sorting#quickSort}.
 */
class QuickSortTest extends FlatSpec with Matchers {
  "Quick sort" should "be able to sort an array" in {
    var arr: Array[Int] = Array(3, 1, 2);
    Sorting.quickSort(arr)
    arr should be(Array(1, 2, 3))
    arr = Seq.fill(10)(Random.nextInt).toArray
    val expected = arr.clone()
    util.Arrays.sort(expected)
    Sorting.quickSort(arr)
    arr should be(expected)
  }

  it should "return a null array for null input" in {
    val arr: Array[Int] = null
    Sorting.quickSort(null)
    val expected: Array[Int] = null
    arr should be(expected)
  }

  it should "handle an empty array" in {
    val arr: Array[Int] = Array()
    Sorting.quickSort(null)
    val expected: Array[Int] = Array()
    arr should be(expected)
  }
}
