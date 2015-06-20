import java.util
import java.util.Comparator

import com.jinloes.simple_functions.Sorting
import org.scalatest.{Matchers, FlatSpec}

import scala.collection.JavaConverters._
import scala.util.Random

/**
 * Tests for {@link Sorting#mergeSort}
 */
class MergeSortTest extends FlatSpec with Matchers {
  "Merge sort" should "be able to sort and array" in {
    val arr: List[Integer] = List(3, 1, 6, 2)
    val expected: Array[Integer] = Array(3, 1, 6, 2)
    util.Arrays.sort(expected, new Comparator[Integer] {
      override def compare(o1: Integer, o2: Integer): Int = o1.compareTo(o2)
    })
    Sorting.mergeSort(arr.asJava) should be(expected.toList.asJava)
    val randomArr: Array[Integer] = Seq.fill(10)(Random.nextInt).asInstanceOf[List[java.lang.Integer]].toArray
    val expectedRandom: Array[Integer] = randomArr.clone()
    util.Arrays.sort(expectedRandom, new Comparator[Integer] {
      override def compare(o1: Integer, o2: Integer): Int = o1.compareTo(o2)
    })
    Sorting.mergeSort(randomArr.toList.asJava) should be(expectedRandom.toList.asJava)
  }
}
