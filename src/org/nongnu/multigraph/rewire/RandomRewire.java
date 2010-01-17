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
 *
 */
public class RandomRewire<N,L> extends AbstractRewire<N,L> {
  Random r = new Random ();
  int mindegree = 1;
  
  public RandomRewire (Graph<N, L> graph, EdgeLabeler<N, L> el) {
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
  public RandomRewire (Graph<N, L> graph, EdgeLabeler<N, L> el,
                       int mindegree) {
    super (graph, el);
    this.mindegree = mindegree;
  }
  
  private void rewire_one (N node,int mindegree, N [] nodes) {
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
        graph.set (node, to, el.getLabel (node, to));
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
  public RandomRewire<N,L> set_mindegree (int mindegree) {
    this.mindegree = mindegree;
    return this;
  }
  
  @SuppressWarnings("unchecked")
  public void rewire () {
    N[] nodes = (N[]) graph.toArray (new Object[0]);
    
    RandomRewire.clear (graph);
    
    for (N node : nodes) {
      /* work around fact you can't have generic typed arrays */
      rewire_one (node, mindegree, nodes);
    }
  }
}
