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

import org.nongnu.multigraph.Edge;
import org.nongnu.multigraph.Graph;

/**
 * Abstract interface for algorithms to rewire the edges of a graph.
 * 
 * All edges may be initially cleared from the graph.
 * 
 * @author paul
 *
 */
public abstract class AbstractRewire<N,L> {
  protected Graph<N,L> graph;
  protected EdgeLabeler<N,L> el;
  
  public AbstractRewire (Graph<N,L> graph, EdgeLabeler<N,L> el) {
    this.graph = graph;
    this.el = el;
  }
  
  static protected <N,L> void clear (Graph<N, L> graph) {
    graph.clear_all_edges ();
  }
  
  public abstract void rewire ();
}
