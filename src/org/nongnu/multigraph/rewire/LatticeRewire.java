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

import org.nongnu.multigraph.EdgeLabeler;
import org.nongnu.multigraph.Graph;
/**
 * Wire up the nodes in a 2D lattice. The algorithm defaults to 
 * floor (sqrt(|V|)) columns, unless a columns value greater than 0 
 * is specified.
 *   
 * @author paul
 *
 * @param <N> The type of the nodes of the graph.
 * @param <E> The type of the edge labels of the graph.
 */
public class LatticeRewire<N, E> extends Rewire<N, E> {
  int cols;
  
  public LatticeRewire (Graph<N, E> graph, EdgeLabeler<N, E> el,
                        int cols) {
    super (graph, el);
    this.cols = (cols > 0 ? cols : (int) Math.sqrt (graph.size ()));
  }

  public LatticeRewire (Graph<N, E> graph, EdgeLabeler<N, E> el) {
    super (graph, el);
    cols = (int) Math.sqrt (graph.size ());
  }
  
  @SuppressWarnings ("unchecked")
  @Override
  public void rewire () {
    N [] prevrow = (N []) new Object [cols];
    N pn = null;
    int i = 0;
    
    graph.plugObservable ();
    graph.clear_all_edges ();
    
    for (N n : graph) {
      E label;
      
      if (pn != null && (label = el.getLabel (pn, n)) != null)
        graph.set (pn, n, label);
      
      if (prevrow[i] != null
          && (label = el.getLabel (prevrow[i], n)) != null)
        graph.set (prevrow[i], n, label);
      
      prevrow[i] = n;
      pn = n;
      i = ++i % cols;
      
      if (i == 0)
        pn = null;
    }
    graph.unplugObservable ();
  }
}
