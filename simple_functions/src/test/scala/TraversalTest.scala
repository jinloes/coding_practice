import javax.swing.tree.DefaultMutableTreeNode

import com.jinloes.simple_functions.tree.Traversal
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

import scala.collection.JavaConverters._

/**
 * Tests for {@link Traversal}
 */
class TraversalTest extends FeatureSpec with Matchers with GivenWhenThen {
  feature("A traversal should be able to perform an preorder traversal") {
    scenario("preorder traversal on a graph") {
      Given("a basic tree")
      val root: DefaultMutableTreeNode = new DefaultMutableTreeNode("A")
      val child1: DefaultMutableTreeNode = new DefaultMutableTreeNode("B")
      val child2: DefaultMutableTreeNode = new DefaultMutableTreeNode("C")
      val child1child: DefaultMutableTreeNode = new DefaultMutableTreeNode("D")
      root.insert(child1, 0)
      root.insert(child2, 1)
      child1.insert(child1child, 0)
      When("perform a preorder traversal")
      val actual = Traversal.preOrder(root)
      Then("a preorder traversal should have been performed")
      actual should be(List("A", "B", "D", "C").asJava)
    }
    scenario("preorder traversal on an empty graph") {
      Given("an empty graph")
      val root: DefaultMutableTreeNode = null
      When("perform a preorder traversal")
      val actual = Traversal.preOrder(root)
      Then("an empty list should be returned")
      actual should be(List().asJava)
    }
  }

  feature("A traversal should be able to perform an inorder traversal") {
    scenario("inorder traversal on a graph") {
      Given("a basic tree")
      val root: DefaultMutableTreeNode = new DefaultMutableTreeNode("A")
      val child1: DefaultMutableTreeNode = new DefaultMutableTreeNode("B")
      val child2: DefaultMutableTreeNode = new DefaultMutableTreeNode("C")
      val child1child: DefaultMutableTreeNode = new DefaultMutableTreeNode("D")
      val child1child2: DefaultMutableTreeNode = new DefaultMutableTreeNode("E")
      root.insert(child1, 0)
      root.insert(child2, 1)
      child1.insert(child1child, 0)
      child1.insert(child1child2, 1)
      When("perform a inorder traversal")
      val actual = Traversal.inOrder(root)
      Then("a inorder traversal should have been performed")
      actual should be(List("D", "B", "E", "A", "C").asJava)
    }
    scenario("inorder traversal on an empty graph") {
      Given("an empty graph")
      val root: DefaultMutableTreeNode = null
      When("perform a inorder traversal")
      val actual = Traversal.inOrder(root)
      Then("an empty list should be returned")
      actual should be(List().asJava)
    }
  }

  feature("A traversal should be able to perform an postorder traversal") {
    scenario("postorder traversal on a graph") {
      Given("a basic tree")
      val root: DefaultMutableTreeNode = new DefaultMutableTreeNode("A")
      val child1: DefaultMutableTreeNode = new DefaultMutableTreeNode("B")
      val child2: DefaultMutableTreeNode = new DefaultMutableTreeNode("C")
      val child1child: DefaultMutableTreeNode = new DefaultMutableTreeNode("D")
      val child1child2: DefaultMutableTreeNode = new DefaultMutableTreeNode("E")
      root.insert(child1, 0)
      root.insert(child2, 1)
      child1.insert(child1child, 0)
      child1.insert(child1child2, 1)
      When("perform a postorder traversal")
      val actual = Traversal.postOrder(root)
      Then("a postorder traversal should have been performed")
      actual should be(List("D", "E", "B", "C", "A").asJava)
    }
    scenario("postorder traversal on an empty graph") {
      Given("an empty graph")
      val root: DefaultMutableTreeNode = null
      When("perform a postorder traversal")
      val actual = Traversal.postOrder(root)
      Then("an empty list should be returned")
      actual should be(List().asJava)
    }
  }
}
