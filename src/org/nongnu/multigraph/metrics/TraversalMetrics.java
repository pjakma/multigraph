package org.nongnu.multigraph.metrics;

import java.util.ArrayList;

import org.nongnu.multigraph.Graph;

/**
 * Return 
 * @author paul
 *
 */
public class TraversalMetrics {
  /**
   * Traverse the Graph and create a histrogram of the distribution
   * of nodal out-degree.
   * @param <N> The type of the nodes in the graph.
   * @param <E> The type of the edges in the graph.
   * @param graph The graph to traverse
   * @return An integer array of the degree distribution, where the array
   *         indices correspond to the degree.
   */
  public static <N,E> int [] degree_distribution (Graph<N,E> graph) {
    int [] vals = new int [graph.max_nodal_degree () + 1];
    
    for (N node : graph)
      vals[graph.nodal_outdegree (node)]++;
    
    return vals;
  }
  
  public interface node_test<N>  {
    public boolean test (N node);
  }
  
  /**
   * Traverse the graph and count those nodes which are accepted by
   * the node_test callback.
   * 
   * @param <N> The type of the nodes in the graph.
   * @param <E> The type of the edges in the graph.
   * @param graph The graph to traverse
   * @param t The boolean callback to apply to decide whether a node
   *          should be counted or not.
   * @return The number of nodes which match the provided test.
   */
  public static <N,E> int count (Graph<N,E> graph, node_test<N> t) {
    int count = 0;
    
    for (N node : graph)
      if (t.test (node))
        count++;
    
    return count;
  }
}
