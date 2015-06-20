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

  feature("A hash table should be able to handle collisions") {
    scenario("add two items that have the same hash index") {
      Given("a hash table with small capacity")
      val table = new HashTable[String, Integer](1)
      When("insert two values")
      table.put("a", 5)
      table.put("b", 6)
      Then("Both values should exist in the hash table")
      table.get("a") should be(5)
      table.get("b") should be(6)
      table.getSize should be(2)
    }
  }

  feature("A hash table should be able to remove elements from itself"){
    scenario("remove an item that exists in the hash table") {
      Given("a hash table with a value")
      val table = new HashTable[String, Integer](1)
      table.put("a", 5)
      When("remove key from the hash table")
      val removedItem = table.remove("a")
      Then("the key should not exist in the hash table")
      removedItem should be(5)
      table.containsKey("a") should be(false)
    }

    scenario("remove an item that does not exist in the hash table") {
      Given("an empty hash table")
      val table = new HashTable[String, Integer](1)
      When("remove key from the hash table")
      val removedItem = table.remove("a")
      Then("the key should not exist in the hash table")
      removedItem should be(null)
      table.containsKey("a") should be(false)
    }
  }
}