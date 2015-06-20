import com.jinloes.data_structures.HashTable
import org.scalatest.{Matchers, FeatureSpec}
import org.scalatest.GivenWhenThen

/**
 * Tests for {@link HashTable}
 */
class HashTableTest extends FeatureSpec with Matchers with GivenWhenThen {
  feature("A user should be able to create an hash table") {
    scenario("create an empty hash table with default capacity") {
      val table = new HashTable
      table should have (
        'size (0)
      )
    }
    scenario("create an empty hash table with specified capacity") {
      val table = new HashTable(5)
      table should have (
        'size (0)
      )
    }
  }

  feature("A user should be able to add an item to a hash table") {
    scenario("add an item to a hash table") {
      Given("a hash table")
      val table = new HashTable[String, Integer]
      When("insert a number into the hash table")
      table.put("a", 5)
      Then("the item should exist in the hash table")
      table.getSize() should be(1)
      table.containsKey("a") should be(true)
      table.containsValue(5) should be(true)
      table.get("a") should be (5)
    }
  }

  feature("A hash table should be able to handle collisions") (pending)
}