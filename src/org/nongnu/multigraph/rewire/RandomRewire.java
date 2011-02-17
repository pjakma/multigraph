/* This file is part of 'MultiGraph'
 *
 * Copyright (C) 2009 Paul Jakma
 *
 * MultiGraph is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3, or (at your option) any
 * later version.  
 * 
 * MultiGraph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.   
 *
 * You should have received a copy of the GNU General Public License
 * along with MultiGraph.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.nongnu.multigraph.rewire;

import java.util.Random;

import org.nongnu.multigraph.Graph;

/** 
 * Randomly wire up nodes of a graph, with each node having at least
 * the {@value mindegree} number of outgoing edges. Note that the 
 * graph need not be continuous.
 * 
 * @author paul
 * @param <N> The type of the Nodes in the graph
 * @param <E> The type of the Edges in the graph
 */
public class RandomRewire<N,E> extends Rewire<N,E> {
  Random r = new Random ();
  int mindegree = 1;
  
  public RandomRewire (Graph<N, E> graph, EdgeLabeler<N, E> el) {
    super (graph, el);
  }
  /**
   * Create a RandomRewire graph rewirer, with a minimum out-degree 
   * which nodes should have after the graph is rewired.
   * @param graph The graph to rewire.
   * @param el An EdgeLabeler callback, to allow the user to create Labels
   *           for new Edges.
   * @param mindegree The minimum out degree (edges to other nodes)
   */
  public RandomRewire (Graph<N, E> graph, EdgeLabeler<N, E> el,
                       int mindegree) {
    super (graph, el);
    this.mindegree = mindegree;
  }
  
  private void rewire_one (N node, int mindegree, N [] nodes) {
    /* XXX: perhaps unnecessarily sigma(2N)
     * and worst-case is unbounded. Iterative algorithm, would be
     * better
     */
    while (graph.nodal_outdegree (node) < mindegree) {
      N to;
      do {
        to = nodes[r.nextInt (nodes.length)];
      } while (to == node);
      
      try {
        E label;
        
        if ((label = el.getLabel (node, to)) != null)
          graph.set (node, to, label);
      } catch (UnsupportedOperationException e) {}
    }
  }
  
  /**
   * Set the minimum out-degree which nodes should have after the graph is
   * rewired.
   * 
   * @param mindegree The minimum out degree (edges to other nodes)
   * @return This RandomRewire instance.
   */
  public RandomRewire<N,E> set_mindegree (int mindegree) {
    this.mindegree = mindegree;
    return this;
  }
  
  @SuppressWarnings("unchecked")
  public void rewire () {
    N[] nodes;
    
    if (mindegree > graph.size () - 1)
      throw new IllegalArgumentException ("mindegree too high for size of graph");
    
    graph.plugObservable ();
    nodes = (N[]) graph.toArray (new Object[0]);

    graph.clear_all_edges ();

    for (N node : nodes) {
      /* work around fact you can't have generic typed arrays */
      rewire_one (node, mindegree, nodes);
    }
    
    graph.unplugObservable ();
  }
}
