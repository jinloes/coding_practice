import com.jinloes.simple_functions.graph.ShortestPath
import org.jgrapht.graph._
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.JavaConverters._

/**
 * Tests for {@link ShortestPath}
 */
class ShortestPathTest extends FlatSpec with Matchers {

  "Shortest path" should "return the shortest path from a point to another" in {
    val graph: DefaultDirectedWeightedGraph[String, DefaultWeightedEdge] = new DefaultDirectedWeightedGraph(
      classOf[DefaultWeightedEdge])
    graph.addVertex("A")
    graph.addVertex("B")

    val abEdge = graph.addEdge("A", "B")
    graph.setEdgeWeight(abEdge, 5)

    ShortestPath.findShortestPath(graph, "A", "B") should be(List[String]("A", "B").asJava)
  }

  it should "return the shortest path from a point to another in a more complex graph" in {
    val graph: DefaultDirectedWeightedGraph[String, DefaultWeightedEdge] = new DefaultDirectedWeightedGraph(
      classOf[DefaultWeightedEdge])
    graph.addVertex("A")
    graph.addVertex("B")
    graph.addVertex("C")
    graph.addVertex("D")

    var abEdge = graph.addEdge("A", "B")
    graph.setEdgeWeight(abEdge, 5)
    var acEdge = graph.addEdge("A", "C")
    graph.setEdgeWeight(acEdge, 2)
    var bdEdge = graph.addEdge("B", "D")
    graph.setEdgeWeight(bdEdge, 3)
    var cdEdge = graph.addEdge("C", "D")
    graph.setEdgeWeight(cdEdge, 4)

    ShortestPath.findShortestPath(graph, "A", "D") should be(List[String]("A", "C", "D").asJava)

    val graph2: DefaultDirectedWeightedGraph[String, DefaultWeightedEdge] = new DefaultDirectedWeightedGraph(
      classOf[DefaultWeightedEdge])
    graph2.addVertex("A")
    graph2.addVertex("B")
    graph2.addVertex("C")
    graph2.addVertex("D")

    abEdge = graph2.addEdge("A", "B")
    graph2.setEdgeWeight(abEdge, 2)
    acEdge = graph2.addEdge("A", "C")
    graph2.setEdgeWeight(acEdge, 5)
    bdEdge = graph2.addEdge("B", "D")
    graph2.setEdgeWeight(bdEdge, 4)
    cdEdge = graph2.addEdge("C", "D")
    graph2.setEdgeWeight(cdEdge, 3)

    ShortestPath.findShortestPath(graph2, "A", "D") should be(List[String]("A", "B", "D").asJava)
  }

  it should "return the shortest path from a point to another in a very complex graph" in {
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

    ShortestPath.findShortestPath(graph, "A", "G") should be(List[String]("A", "C", "B", "D", "E", "G").asJava)
  }

  it should "return an empty path for an empty graph" in {
    val graph: DefaultDirectedWeightedGraph[String, DefaultWeightedEdge] = new DefaultDirectedWeightedGraph(
      classOf[DefaultWeightedEdge])
    ShortestPath.findShortestPath(graph, "A", "B") should be(List().asJava)
  }

  it should "return an empty path if there is no path to the target" in {
    val graph: DefaultDirectedWeightedGraph[String, DefaultWeightedEdge] = new DefaultDirectedWeightedGraph(
      classOf[DefaultWeightedEdge])
    graph.addVertex("A")
    graph.addVertex("B")
    ShortestPath.findShortestPath(graph, "A", "B") should be(List().asJava)
  }
}
