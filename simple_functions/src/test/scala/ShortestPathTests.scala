import com.jinloes.simple_functions.graph.ShortestPath
import org.jgrapht.graph.{DefaultDirectedWeightedGraph, DefaultWeightedEdge}
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

import scala.collection.JavaConverters._

/**
 * Tests for {@Link ShortestPath}
 */
class ShortestPathTests extends FeatureSpec with Matchers with GivenWhenThen {
  feature("Find the shortest path in a graph with BFS") {
    scenario("find the shortest path with a simple graph") {
      Given("a simple graph")
      val graph: DefaultDirectedWeightedGraph[String, DefaultWeightedEdge] = new DefaultDirectedWeightedGraph(
        classOf[DefaultWeightedEdge])
      graph.addVertex("A")
      graph.addVertex("B")

      val abEdge = graph.addEdge("A", "B")
      graph.setEdgeWeight(abEdge, 5)

      When("find the shortest path using BFS")
      val actual = ShortestPath.shortestPathBfs(graph, "A", "B")
      Then("the shortest path should be found")
      actual should be(List[String]("A", "B").asJava)
    }

    scenario("find the shortest path with a more complex graph") {
      Given("a complex graph")
      val graph: DefaultDirectedWeightedGraph[String, DefaultWeightedEdge] = new DefaultDirectedWeightedGraph(
        classOf[DefaultWeightedEdge])
      graph.addVertex("A")
      graph.addVertex("B")
      graph.addVertex("C")
      graph.addVertex("D")
      graph.addVertex("E")
      graph.addVertex("F")
      graph.addVertex("G")

      var abEdge = graph.addEdge("A", "B")
      graph.setEdgeWeight(abEdge, 5)
      var acEdge = graph.addEdge("A", "C")
      graph.setEdgeWeight(acEdge, 3)
      var bcEdge = graph.addEdge("B", "C")
      graph.setEdgeWeight(bcEdge, 1)
      var bdEdge = graph.addEdge("B", "D")
      graph.setEdgeWeight(bdEdge, 5)
      var cdEdge = graph.addEdge("C", "B")
      graph.setEdgeWeight(cdEdge, 1)
      var deEdge = graph.addEdge("D", "E")
      graph.setEdgeWeight(deEdge, 2)
      var dfEdge = graph.addEdge("D", "F")
      graph.setEdgeWeight(dfEdge, 4)
      var egEdge = graph.addEdge("E", "G")
      graph.setEdgeWeight(egEdge, 3)
      var fgEdge = graph.addEdge("F", "G")
      graph.setEdgeWeight(fgEdge, 2)
      var cgEdge = graph.addEdge("C", "G")
      graph.setEdgeWeight(cgEdge, 15)

      When("findings the shortest path using bfs")
      val actual = ShortestPath.findShortestPath(graph, "A", "G")
      Then("the shortest path should be found")
      actual should be(List[String]("A", "C", "B", "D", "E", "G").asJava)
    }
  }
}
