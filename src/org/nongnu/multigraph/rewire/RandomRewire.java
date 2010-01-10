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

import org.nongnu.multigraph.EdgeLabeler;
import org.nongnu.multigraph.Graph;

/** Randomly wire up nodes of a graph.
 * 
 * @author paul
 *
 */
public class RandomRewire extends AbstractRewire {
  static private <N,L> void rewire_one (Graph<N,L> graph, N node, 
                                        int mindegree, EdgeLabeler<N,L> el,
                                        N [] nodes, Random r) {
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
  
  @SuppressWarnings("unchecked")
  public static <N,L> void rewire (Graph<N,L> graph,N node, int mindegree, 
                            EdgeLabeler<N,L> el) {
    N[] nodes = (N[]) graph.toArray (new Object[0]);
    Random r = new Random ();
    
    rewire_one (graph, node, mindegree, el, nodes, r);
  }
  
  @SuppressWarnings("unchecked")
  public static <N,L> void rewire (Graph<N,L> graph, int mindegree,
                            EdgeLabeler<N,L> el) {
    N[] nodes = (N[]) graph.toArray (new Object[0]);
    Random r = new Random ();
    
    RandomRewire.clear (graph);
    
    for (N node : nodes) {
      /* work around fact you can't have generic typed arrays */
      rewire_one (graph, node, mindegree, el, nodes, r);
      
    }
  }
}
