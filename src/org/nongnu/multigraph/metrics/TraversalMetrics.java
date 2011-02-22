package org.nongnu.multigraph.metrics;

import org.nongnu.multigraph.Graph;

/**
 * Return 
 * @author paul
 *
 */
public class TraversalMetrics {
  /**
   * Traverse the Graph and create a histogram of the distribution
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
  
  /**
   * Traverse the Graph and create a histogram of the normalised distribution
   * of nodal out-degree. I.e. the probability of a given degree, for that degree.
   * @param <N> The type of the nodes in the graph.
   * @param <E> The type of the edges in the graph.
   * @param graph The graph to traverse
   * @return An integer array of the degree distribution, where the array
   *         indices correspond to the degree.
   */
  public static <N,E> float [] norm_degree_distribution (Graph<N,E> graph) {
    int [] degrees = degree_distribution (graph);
    float [] vals = new float [degrees.length];
    
    for (int i = 0; i < degrees.length; i++)
      vals[i] = (float)degrees[i]/graph.size ();
    
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
  
  /**
   * Traverse the graph and count the number of edges.
   */
  public static <N,E> int edges (final Graph<N,E> graph) {
    int count = 0;
    for (N node : graph)
      count+= graph.edge_outdegree (node);
    return count / (graph.is_directed () ? 1 : 2);
  }
}
