package org.nongnu.multigraph.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Distance maps, from each node to every other node. Suitable for sparse
 * graphs.
 * 
 * @author Paul Jakma
 *
 * @param <N>
 */
public class dmap<N> {
  private static final long serialVersionUID = 1L;
  
  private Map<N, Map<N,Integer>> dmap = new HashMap<N, Map<N,Integer>> ();
  
  public int dist (N from, N to) {
    Map<N,Integer> to_weights;
    Integer w;
    
    if ((to_weights = dmap.get (from)) != null
        && (w = to_weights.get (to)) != null)
      return w.intValue ();
    
    return Integer.MAX_VALUE;
  }
  
  public void set (N from, N to, int w) {
    Map<N,Integer> to_weights;
    Integer oldw;
    
    if (w == Integer.MAX_VALUE)
      return;
    
    if (w < 0)
      throw new IllegalArgumentException ("weight " + w + " is less than 0");
    
    if ((to_weights = dmap.get (from)) == null) {
      to_weights = new HashMap<N,Integer> ();
      dmap.put (from, to_weights);
    }
    
    if ((oldw = to_weights.get (to)) == null
        || oldw.intValue () > w)
      to_weights.put (to, w);
  }
  
  public Set<Map.Entry<N,Map<N,Integer>>> entrySet() {
    return dmap.entrySet ();
  }
}
