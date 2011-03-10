package org.nongnu.multigraph.metrics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.nongnu.multigraph.Edge;
import org.nongnu.multigraph.Graph;
import org.nongnu.multigraph.debug;

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
   * of nodal out-degree. I.e. the probability of a given degree, for that 
   * degree.
   * 
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
      count += graph.edge_outdegree (node);
    return count / (graph.is_directed () ? 1 : 2);
  }
  
  private static <N,E> int FWdist (Graph<N,E> graph, dmap<N> dmap,
                                   N from, N to) {
    Collection<Edge<N,E>> edges;
    int w;
    
    if (from == to)
      return 0;
    
    w = dmap.dist (from, to);
    
    if (w < Integer.MAX_VALUE)
      return w;
    
    if ((edges = graph.edges (from, to)).size () > 0) {
      int min = Integer.MAX_VALUE;
      for (Edge<N,E> e : edges)
        min = Math.min (min, e.weight ());
      return min;
    }
    
    return Integer.MAX_VALUE;
  }
  
  /* distance addition, handling absorption of distances to infinity */
  private static int FWdplus (int w1, int w2) {
    int max = Integer.MAX_VALUE;
    if (w1 == max || w2 == max)
      return max;
    if (w2 >= (max - w1))
      return max;
    return w1 + w2;
  }
  
  public static <N,E> dmap<N> FloydWarshal (final Graph<N,E> graph) {
    //int [][] d = new int[graph.size ()][graph.size ()];
    dmap<N> dmap = new dmap<N> ();
    
    for (N k : graph)
      for (N i : graph)
        for (N j : graph)
          dmap.set (i, j, Math.min(FWdist (graph, dmap, i,j),
                                   FWdplus (FWdist(graph, dmap, i, k), 
                                            FWdist (graph, dmap, k, j))));
    return dmap;
  }
  
  public static <N,E> Map<String,Double> stats (final Graph<N,E> graph) {
    return stats (FloydWarshal (graph), graph);
  }
  
  public static <N,E> Map<String,Double> stats (final dmap<N> dmap, final Graph<N,E> graph) {
    Map<String, Double> results = new HashMap<String, Double> ();
    double avg = 0, delta2 = 0;
    int max = 0, num = 0;
    int radius = Integer.MAX_VALUE;
    int diameter = 0;
    
    for (Map.Entry<N,Map<N,Integer>> from : dmap.entrySet ()) {
      int eccentricity = 0;
      
      for (Map.Entry<N,Integer> to: from.getValue ().entrySet ()) {
        int w = to.getValue ();
        double delta = (double)w - avg;
        max = Math.max (max, w);
        
        debug.printf ("%s -> %s: %d\n",
                      from.getKey (), to.getKey (), w);
        
        if (w == 0)
          continue;
        
        eccentricity = Math.max (eccentricity, w);
        avg += delta / ++num;
        delta2 += delta * (w - avg);
        
        debug.printf ("w: %d, num %d, avg %4f, delta2 %4f\n",
                      w, num, avg, delta2);
      }
      radius = Math.min (radius, eccentricity);
      diameter = Math.max (diameter, eccentricity);
    }
    
    double stddev = Math.sqrt (avg /(Math.sqrt (num)));
    results.put ("max", (double) max);
    results.put ("avg", avg);
    results.put ("stddev", stddev);
    results.put ("stderr",stddev / Math.sqrt(num));
    results.put ("radius", (double) radius);
    results.put ("diameter", (double) diameter);
    
    return results;
  }
}
