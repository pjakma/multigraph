package org.nongnu.multigraph.rewire;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import org.nongnu.multigraph.EdgeLabeler;
import org.nongnu.multigraph.Graph;
import org.nongnu.multigraph.debug;

/**
 * Rewire a graph such that the connectivity of its nodes have a scale-free
 * distribution, following the model given in the Barabis, Albert paper 
 * "Emergence of Scaling in Random Networks" paper.
 * <p>
 * The model uses an m parameter, which specifies the number of edges
 * to add from each new node to existing nodes, on each time-step. The initial
 * seed size of the graph, (m_0 in the paper) will be taken as m + 1. The default
 * for m is 1. 
 * <p>
 * In the BA model, m is very rigidly the number of links added. This may
 * be useful for analytical purposes, but does not lead to much variation in
 * the structure of the generated graphs. Other interpretations are possible
 * for m, which deviate slightly from the BA model. E.g. it could be taken 
 * as a minimum number of links to add, while
 * considering adding links from the new node to every existing node; or as
 * a maximum number to add. This can be controlled with the "m_mode" parameter
 * (@see m_modes).
 * <p>
 * An additional 'a' parameter allows an additive bias to be introduced, which
 * gives low-degree (i.e. younger) nodes a better chance - in the spirit
 * of the Dorogovtsev, et al, paper. @see #a
 * <p>
 * 
 * @author paul
 *
 * @param <N> The label type of nodes in the graph.
 * @param <E> The label type of edges in the graph.
 */
public class ScaleFreeRewire<N,E> extends Rewire<N,E> {
  protected Random r = new Random ();
  protected N [] nodes;
  protected int m = 1;
  protected int a = 0;
  
  public int m () {
    return m;
  }
  /**
   * The 'm' parameter is the number of links each new node will get to
   * the existing nodes in the graph. It defaults to 1, and must be greater
   * than or equal to 1.
   * 
   * @param m The number of links to add from a new node on every time-step.
   * @return reference to this class.
   */
  public ScaleFreeRewire<N,E> m (int m) {
    if (m >= graph.size ())
      throw new java.lang.IllegalArgumentException ("m must be less than the graph size");
    
    if (m <= 0)
      throw new java.lang.IllegalArgumentException ("m must be >= 1");
    
    this.m = m;
    return this;
  }

  public int a () {
    return a;
  }
  
  /**
   * The 'a' parameter controls the initial attractiveness of a node, along
   * the lines of the A parameter in the Dorogovtsev, et al, "Structure
   * of Growing Networks with Preferential Linking", Phys. Rev. Lett., 2000, 
   * model.
   * 
   * The default is 0, in which case the behaviour is identical to the 
   * BA model. The value must be ≥ 0.
   * @param a The initial attractiveness of new nodes.
   * @return This ScaleFreeRewire instance.
   */
  public ScaleFreeRewire<N,E> a (int a) {
    if (a < 0)
      throw new java.lang.IllegalArgumentException ("a must be >= 0");
    this.a = a;
    return this;
  }

  public m_modes m_mode () {
    return m_mode;
  }
  
  /**
   * @param m_mode Whether to interpret m as a maximum, a minimum or strictly
   *               according to BA model, as a precise number of links to 
   *               add. @see #m_modes.
   * @return reference to this class.
   */
  public ScaleFreeRewire<N,E> m_mode (m_modes m_mode) {
    this.m_mode = m_mode;
    return this;
  }
  /**
   * @param m_mode Whether to interpret m as a maximum, a minimum or strictly
   *               according to BA model, as a precise number of links to 
   *               add. @see #m_modes.
   * @return reference to this class.
   */
  public ScaleFreeRewire<N,E> m_mode (String m_mode) {
    this.m_mode (m_modes.valueOf (m_mode.toUpperCase ()));
    return this;
  }

  public enum m_modes {
    /**
     * Strict interpretation of the m-value used, according
     * to the Barabasi-Albert model. I.e. exactly m links
     * are added on each time-step. This means not all
     * pairs of nodes may be considered.
     */
    STRICT,
    /**
     * The m-value is interpreted as a minimum value. At every
     * time-step, the creation of links will be considered between 
     * *every* existing node and the new node, AND at least
     * m-links will be added.
     */
    MIN,
    /**
     * The m-value is interpreted as a maximum value. At every
     * time-step, the creation of links will be considered between 
     * *every* existing node and the new node, OR at least
     * m-links have been added. A minimum of 1 link is also
     * enforced.
     */
    MAX,
  };
  m_modes m_mode = m_modes.STRICT;
  
  @SuppressWarnings ("unchecked")
  protected void _init_nodes () {
    /* new N[..] is not allowed in java, but this works */
    nodes = (N []) new Object[graph.size ()];
    
    int i = 0;
    for (N n : graph.random_node_iterable ())
      nodes[i++] = n;
  }
  
  /**
   * Create a ScaleFreeRewiring instancing, with the default m value.
   * @param graph Graph to act on
   * @param el EdgeLabeler callback, whose getLabel method will be
   *           called when an edge is added.
   */
  public ScaleFreeRewire (Graph<N, E> graph, EdgeLabeler<N, E> el) {
    super (graph, el);
  }
  
  protected static boolean m_mode_stop (m_modes m_mode, int m, int added, int pass) {
    /* In no case should we allow the process to spin forever trying
     * to add a link but being unable to. As an arbitrary limit, we
     * hard-bound all processes to max(10,m*10) passes
     */
    if (pass > Math.max (10,m*10)) {
      debug.printf ("hit hard-limit on passes! mode/m/added/pass: %s/%d/%d/%d\n",
                    m_mode.name (), m, added, pass);
      return true;
    }
    
    switch (m_mode) {
      case STRICT: return added >= m;
      case MIN: return pass > 0 && added >= m;
      case MAX: return (pass > 0 && added >= 1) || added >= m;
      default: return true;
    }
  }
  
  /**
   * Consider whether to add a link between the new node and the
   * given existing node.
   * @param to_add The vertex to be added to the graph
   * @param vi The vertex being considered, v_i in the paper.
   * @param numlinks The number of links that were in the graph,
   *                 before any links were added to this new node.
   * @return True if a link should be created
   */
  protected boolean consider_link (N to_add, N vi, long numlinks) {
    int ki = graph.nodal_outdegree (vi);
    
    debug.printf ("\tvi: %s, ki: %d\n", vi, ki);
    
    /* ... with probability Π(ki) = ki/sigmakj,
     * where ki is the connectivity of the existing node,
     * and sigmakj the sum of the connectivity of nodes in the graph
     * (in a directed graph, this will be == edge count, it's
     *  unclear to me though whether Barabasi and Albert considered
     *  this model to be applicable to directed graphs..)
     *  
     *  sigmakj = 2 * links
     */
    float fr = r.nextFloat ();
    float pi = (a + ki)/(float)(2 * numlinks);
    
    return fr <= pi;
  }
  
  /**
   * Called when a link is created between 2 nodes. To facilitate the
   * maintenance of any state. For the standard BA-model, there is
   * no state beside the number of links, used to calculate the
   * sum of the degrees, which the class already tracks.
   * 
   * @param added The added node.
   * @param vi The existing node the edge is added to.
   */
  protected void link_added (N added, N vi) {
    debug.printf ("link added: %s -> %s\n", added, vi);
    return;
  }
  
  protected void m0 () {
    /* The first m nodes are a special case, because they are not attached
     * and so ki = 0, and so Π(ki) = 0. I.e. the m0 graph from the paper.
     */
    for (int i = 1; i < m + 1; i++) {
      graph.set (nodes[i - 1], nodes[i],
                 el.getLabel (nodes[i - 1], nodes[i]));
      link_added (nodes[i - 1], nodes[i]);
    }
  }
  
  protected Iterator<N> nodes_iterator (final int split) {
    return new Iterator<N> () {
      int i = 0;
      @Override
      public boolean hasNext () {
        return i < split;
      }

      @Override
      public N next () {
        if (i >= split) throw new NoSuchElementException ();
        
        return nodes[i++];
      }

      @Override
      public void remove () {
        throw new UnsupportedOperationException ();
      }
    };
  }
  
  protected int rewire_callback (int split, int numlinks) { return 0; }
  
  @Override
  public void rewire () {
    /* this tracks the index at which our set of nodes is split between
     * already attached and yet to be attached.
     */
    int split = m + 1;
    int links = m;
    
    graph.clear_all_edges ();
    _init_nodes ();
    
    m0 ();
    
    /* every new node to be attached to the existing graph.. */
    while (split < nodes.length) {
      N to_add = nodes[split];
      final int tmpsplit = split;
      
      debug.println ("to_add: " + to_add);
      
      /* ..should consider adding an edge to every existing node ... */
      links += add (to_add, links, new Iterable<N> () {
        @Override
        public Iterator<N> iterator () {
          return nodes_iterator (tmpsplit);
        }
      });
      
      links += rewire_callback (split, links);
      
      split++;
    }
  }
  
  protected boolean add_link (N to_add, N to) {
    debug.printf ("%s to %s\n", to_add, to);
    
    if (graph.is_linked (to_add, to))
      return false;
    
    graph.set (to_add, to, el.getLabel (to_add, to));
    link_added (to_add, to);
    
    return true;
  }
  
  protected int add (N to_add, long numlinks, Iterable<N> iter) {
    int added = 0;
    int pass = 0;
    
    debug.printf ("toadd %s\n", to_add);
    
    do {
      for (N node : iter) {
        if (m_mode_stop (m_mode, m, added, pass))
          break;
        if (to_add != node
            && !graph.is_linked (to_add, node)
            && consider_link (to_add, node, numlinks)
            && add_link (to_add, node))
              added++;
      }
      pass++;
    } while (!m_mode_stop (m_mode, m, added, pass));
    
    return added;
  }
  
  @Override
  public void add (N to_add) {
    add (to_add, graph.link_count (), graph.random_node_iterable ());
  }
}
