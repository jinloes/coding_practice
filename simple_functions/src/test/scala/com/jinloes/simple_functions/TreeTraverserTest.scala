package com.jinloes.simple_functions

import javax.swing.tree.DefaultMutableTreeNode

import com.jinloes.simple_functions.tree.TreeTraverser
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.JavaConverters._

/**
  * Tests for {@link TreeTraverser}
  */
@RunWith(classOf[JUnitRunner])
class TreeTraverserTest extends FlatSpec with Matchers {
  "A tree traverser" should "be able to perform an preorder traversal" in {
    /*
            A
           / \
          B   C
         /
        D
    */
    val root: DefaultMutableTreeNode = new DefaultMutableTreeNode("A")
    val child1: DefaultMutableTreeNode = new DefaultMutableTreeNode("B")
    val child2: DefaultMutableTreeNode = new DefaultMutableTreeNode("C")
    val child1child: DefaultMutableTreeNode = new DefaultMutableTreeNode("D")
    root.insert(child1, 0)
    root.insert(child2, 1)
    child1.insert(child1child, 0)
    val actual = TreeTraverser.preOrder(root)
    actual should be(List("A", "B", "D", "C").asJava)
  }

  it should " be traverse an empty graph" in {
    val root: DefaultMutableTreeNode = null
    val actual = TreeTraverser.preOrder(root)
    actual should be(List().asJava)
  }

  it should "be able to perform an inorder traversal" in {
    /*
          A
         / \
        B   C
       / \
      D   E
     */
    val root: DefaultMutableTreeNode = new DefaultMutableTreeNode("A")
    val child1: DefaultMutableTreeNode = new DefaultMutableTreeNode("B")
    val child2: DefaultMutableTreeNode = new DefaultMutableTreeNode("C")
    val child1child: DefaultMutableTreeNode = new DefaultMutableTreeNode("D")
    val child1child2: DefaultMutableTreeNode = new DefaultMutableTreeNode("E")
    root.insert(child1, 0)
    root.insert(child2, 1)
    child1.insert(child1child, 0)
    child1.insert(child1child2, 1)
    val actual = TreeTraverser.inOrder(root)
    actual should be(List("D", "B", "E", "A", "C").asJava)
  }

  it should "perform an inorder traversal on an empty graph" in {
    val root: DefaultMutableTreeNode = null
    val actual = TreeTraverser.inOrder(root)
    actual should be('empty)
  }

  it should "be able to perform an postorder traversal" in {
    /*
          A
         / \
        B   C
       / \
      D   E
     */
    val root: DefaultMutableTreeNode = new DefaultMutableTreeNode("A")
    val child1: DefaultMutableTreeNode = new DefaultMutableTreeNode("B")
    val child2: DefaultMutableTreeNode = new DefaultMutableTreeNode("C")
    val child1child: DefaultMutableTreeNode = new DefaultMutableTreeNode("D")
    val child1child2: DefaultMutableTreeNode = new DefaultMutableTreeNode("E")
    root.insert(child1, 0)
    root.insert(child2, 1)
    child1.insert(child1child, 0)
    child1.insert(child1child2, 1)
    val actual = TreeTraverser.postOrder(root)
    actual should be(List("D", "E", "B", "C", "A").asJava)
  }
  it should "perform a postorder traversal on an empty graph" in {
    val root: DefaultMutableTreeNode = null
    val actual = TreeTraverser.postOrder(root)
    actual should be('empty)
  }
}
