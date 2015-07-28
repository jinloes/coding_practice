import com.jinloes.simple_functions.graph.{ConnectNodes, Node}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

/**
 * Tests for {@link ConnectNodes}.
 */
class ConnectNodesTest extends FeatureSpec with Matchers with GivenWhenThen {
  feature("Connect nodes at each level so that a node will have a next pointer and a previous pointer") {
    scenario("connect nodes in a simple graph") {
      Given("a simple graph")
      val root = new Node(null, null, "A")
      When("connect the nodes")
      ConnectNodes.connectNodes(root)
      Then("A should be connected with nothing")
      root.getNext should be(null)
      root.getPrevious should be(null)
    }
    scenario("connect nodes in a more complex graph") {
      Given("a more complex graph")
      val child2 = new Node(null, null, "B")
      val child3 = new Node(null, null, "C")
      val root = new Node(child2, child3, "A")
      When("connect the nodes")
      ConnectNodes.connectNodes(root)
      Then("B and C should be connected")
      child2.getPrevious should be(null)
      child2.getNext should be(child3)
      child3.getPrevious should be(child2)
    }
    scenario("connect nodes in complex graph") {
      Given("a complex graph")
      val child3Child = new Node(null, null, "F")
      val child2Child = new Node(null, null, "D")
      val child2Child2 = new Node(null, null, "E")
      val child2 = new Node(child2Child, child2Child2, "B")
      val child3 = new Node(child3Child, null, "C")
      val root = new Node(child2, child3, "A")
      When("connect the nodes")
      ConnectNodes.connectNodes(root)
      Then("D E and F should be connected")
      child2Child.getPrevious should be(null)
      child2Child.getNext should be(child2Child2)
      child2Child2.getPrevious should be(child2Child)
      child2Child2.getNext should be(child3Child)
      child3Child.getPrevious should be(child2Child2)
      child3Child.getNext should be(null)
    }
  }
}