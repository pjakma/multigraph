package org.nongnu.multigraph.structure;
import org.nongnu.multigraph.Graph;

/**
 * Calculate the maximum k-core membership of each vertex, according to the 
 * k-core definition from Seidman, "Network structure and minimum degree",
 * Social Networks, 1983.
 * 
 * @author Paul Jakma
 */
public class kshell {
  
  /**
   * Calculate the maximum k-shell membership for each node in the Graph, storing
   * the result in the gkc().k field of the node. The method returns the maximum 
   * k-shell in the graph.
   * 
   * @param <N> The type of the node, which must implement kshell_node.
   * @param <E> The type of the edges in the graph.
   * @param graph The graph to act on.
   * @return The k-value of the maximum k-shell present in the graph.
   */
  static public <N extends kshell_node,E> int calc (Graph<N,E> graph) {
    for (N p : graph) {
      p.gkc ().reset ();
      p.gkc ().k = graph.nodal_outdegree (p);
    }
    
    int remain = graph.size ();
    int kshell = 0;
    do {
      boolean removed = false;
      
      do {
        removed = false;
        
        for (N h : graph) {
          
          if (h.gkc ().k > kshell)
            continue;
          
          if (h.gkc ().removed)
            continue;
          
          h.gkc ().removed = true;
          remain--;
          
          for (N s : graph.successors (h))
            if (!s.gkc ().removed) {
              s.gkc ().k = Math.max (kshell, s.gkc ().k - 1);
            }
          
          removed = true;
        }
      } while (removed);
      
      kshell++;
      
    } while (remain > 0);
    
    return kshell;
  }
}
