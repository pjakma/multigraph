package org.nongnu.multigraph.rewire;

import java.util.Random;

import org.nongnu.multigraph.Graph;
import org.nongnu.multigraph.debug;

/**
 * Rewire a graph such that the connectivity of its nodes have a scale-free
 * distribution, following the model given in the Barabis, Albert paper 
 * "Emergence of Scaling in Random Networks" paper.
 * <p>
 * Todo: Add a power factor quotient.
 * 
 * @author paul
 *
 * @param <N> The label type of nodes in the graph.
 * @param <E> The label type of edges in the graph.
 */
public class ScaleFreeRewire<N,E> extends AbstractRewire<N,E> {
  Random r = new Random ();
  
  public ScaleFreeRewire (Graph<N, E> graph, EdgeLabeler<N, E> el) {
    super (graph, el);
  }

  @SuppressWarnings ("unchecked")
  @Override
  public void rewire () {
    /* new N[..] is not allowed in java, but this works */
    N [] nodes = (N []) new Object[graph.size ()];
    
    /* this tracks the index at which our set of nodes is split between
     * already attached and yet to be attached.
     */
    int split;
    int sigmakj = 0;
    
    graph.plugObservable ();
    clear (graph);
    graph.toArray (nodes);
    
    /* The first 2 nodes are a special case, because they are not attached
     * and so ki = 0, and so Π(ki) = 0. I.e. the m0 graph from the paper.
     */
    graph.set (nodes[0], nodes[1], el.getLabel (nodes[0], nodes[1]));
    split = 2;
    sigmakj = 2;
    
    /* every new node to be attached to the existing graph.. */
    while (split < nodes.length) {
      N to_add = nodes[split];
      int sigmakj_new = 0;
      boolean was_added = false;
      
      debug.println ("ScaleFree, to_add: " + to_add);
      
      /* ..should consider adding an edge to every existing node ... */
      for (int i = 0; i < split; i++) {
        N vi = nodes[i];
        int ki = graph.nodal_outdegree (vi);
        
        debug.println ("\tvi: " + vi + ", ki: " + ki);
        debug.println ("\tSkj: " + sigmakj);
        
        float fr = r.nextFloat ();
        float pi = ki/(float)sigmakj;
        
        debug.println ("\tfr: " + fr + ", pi: " + pi);
        
        /* ... with probability Π(ki) = ki/sigmakj,
         * where ki is the connectivity of the existing node,
         * and sigmakj the sum of the connectivity of nodes in the graph
         * (in a directed graph, this will be == edge count, it's
         *  unclear to me though whether Barabasi and Albert considered
         *  this model to be applicable to directed graphs..)
         */
        if (fr <= pi) {
          graph.set (to_add, vi, el.getLabel (to_add, vi));
          was_added = true;
        }
        sigmakj_new += graph.nodal_outdegree (vi);
      }
      sigmakj_new += graph.nodal_outdegree (to_add);
      sigmakj = sigmakj_new;
      
      /* If node was added successfully, update the split, go onto next
       * next node.
       */
      if (was_added)
        split++;
    }
    graph.unplugObservable ();
  }
}
