package org.nongnu.multigraph.rewire;

import org.nongnu.multigraph.Graph;
import org.nongnu.multigraph.debug;

/**
 * 
 * @author paul
 *
 * @param <N> The label type of nodes in the graph.
 * @param <E> The label type of edges in the graph.
 */
public class MultiClassScaleFreeRewire<N,E> extends ScaleFreeRewire<N,E> {
  protected int p = 1;
  private m_modes p_mode = m_modes.MAX;
  /**
   * @see #p(int)
   * @return The number of links to consider adding between alike nodes on each time-step.
   */
  public int p () {
    return p;
  }
  
  /**
   * The 'p' parameter is the number of links to add between existing, sufficiently alike
   * nodes on each time-step. It defaults to 1, and must be greater than or equal to 0.
   * <p>
   * @param p The number of links to consider adding between alike nodes on each time-step.
   * @return reference to this class.
   */
  public MultiClassScaleFreeRewire<N,E> p (int p) {
    if (p >= graph.size ())
      throw new java.lang.IllegalArgumentException ("p must be less than the graph size");
    
    if (p < 0)
      throw new java.lang.IllegalArgumentException ("p must be >= 0");
    
    this.p = p;
    return this;
  }
  
  public MultiClassScaleFreeRewire (Graph<N, E> graph,
                                    EdgeLabeler<N, E> el) {
    super (graph, el);
  }
  
  protected boolean consider_similar_link (N vi, N vj, int numlinks) {
    int ki = graph.nodal_outdegree (vi);
    int kj = graph.nodal_outdegree (vj);
    
    float fr = r.nextFloat ();
    float pi = Math.abs (kj*ki)/(float)(numlinks*numlinks);
    debug.printf ("consider_similar_link: %d*%d / numlinks^2 = %f\n", pi);
    return pi <= fr;
  }
  
  protected void add_like_links (int split) {
    /* links may be added between like nodes */
    int added = 0;
    int pass = 0;
    do {
      for (int i = 0; i < split && !m_mode_stop (p_mode, p, added, pass); i++)
        for (int j = i + 1; j < split; j++) {
          N n1 = nodes[r.nextInt (split)];
          N n2 = nodes[r.nextInt (split)];
          
          if (n1 == n2) continue;
          
          if (consider_similar_link (n1, n2, split)) {
            add_link (n1, n2);
            /* consider it added, even if link already existed,
             * for purposes of number of peering links to add */
            added++;
          }
        }
    } while (!m_mode_stop (p_mode, p, added, ++pass));
  }
  
  @Override
  public void rewire () {
    int split = m + 1;
    
    graph.plugObservable ();
    
    m0 ();
    
    /* Add the new node to an existing transit node */
    while (split < nodes.length) {
      N to_add = nodes[split];
      
      /* preferentially attach to_add, as normal */
      add (to_add, split);
      
      /* consider like-links */
      add_like_links (split);
      
      split++;
    }
    
    graph.unplugObservable ();
  }
}
