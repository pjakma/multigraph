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

import org.nongnu.multigraph.Graph;

/**
 * Abstract interface for algorithms to rewire the edges of a graph.
 * 
 * All edges may be initially cleared from the graph.
 * 
 * @param <N> The type of the Nodes in the graph
 * @param <E> The type of the Edges in the graph
 * 
 * @author paul
 *
 */
public abstract class AbstractRewire<N,E> {
  protected Graph<N,E> graph;
  protected EdgeLabeler<N,E> el;
  
  public AbstractRewire (Graph<N,E> graph, EdgeLabeler<N,E> el) {
    this.graph = graph;
    this.el = el;
  }
  
  /**
   * Rewire the whole graph. All edges potentially are first cleared.
   * Then some edges added back, according to the specified algorithm.
   */
  public abstract void rewire ();
  
  /**
   * Add a single node to the graph.
   * <p>
   * This may have different resource usage relative to rewire(). E.g. it
   * may use less memory than rewire(), but more CPU over all if called 
   * for a significant number of nodes. 
   * <p>
   * This method is optional, and not all implementations support it.
   *
   * @throws UnsupportedOperationException If this method is not supported.
   * @param node
   */
  public void add (N node) { throw new UnsupportedOperationException ();};
}
