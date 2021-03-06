package org.nongnu.multigraph.rewire;

import org.nongnu.multigraph.EdgeLabeler;
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
  private m_modes p_mode = m_modes.STRICT;
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
  
  protected boolean consider_similar_link (N vi, N vj, int numnodes, int numlinks) {
    int ki = graph.nodal_outdegree (vi);
    int kj = graph.nodal_outdegree (vj);
    
    float fr = r.nextFloat ();
    double pi = kj*ki/(double)(4*numlinks * numlinks);
    debug.printf ("consider_similar_link: %5d, %5d, %6d, %6d: %10d/%10d = %8f >= %8f ? %s\n", 
                  kj, ki, numlinks, numnodes, kj * ki, 4*numlinks * numlinks, 
                  pi, fr, (fr <= pi ? "y" : " "));
    return fr <= pi;
  }
  
  protected int add_like_links (int split, int numlinks) {
    /* links may be added between like nodes */
    int added = 0;
    int pass = 0;
    
    do {
      for (int i = 0; i < split && !m_mode_stop (p_mode, p, added, pass); i++)
        for (int j = i + 1; j < split && !m_mode_stop (p_mode, p, added, pass); j++) {
          N n1 = nodes[i];
          N n2 = nodes[j];
          
          if (n1 == n2) continue;
          
          if (consider_similar_link (n1, n2, split, numlinks + added)) {
            if (graph.is_linked (n1, n2)) continue;
            
            add_link (n1, n2);
            /* consider it added, even if link already existed,
             * for purposes of number of peering links to add */
            added++;
          }
        }
    } while (!m_mode_stop (p_mode, p, added, ++pass));
    return added;
  }
  
  @Override
  protected int rewire_callback (int split, int numlinks) {
    /* consider like-links */
    return add_like_links (split, numlinks);
  }
}
