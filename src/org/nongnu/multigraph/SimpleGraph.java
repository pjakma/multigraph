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

package org.nongnu.multigraph;

/**
 * Simple, undirected graph: No self-loop edges allowed and 0 or 1 edges
 * between nodes.
 * 
 * @see SimpleDiGraph
 * @param N The type of the Nodes in the graph
 * @param E The type of the Edges in the graph
 */
public class SimpleGraph<N,E> extends SimpleDiGraph<N, E> {
  @Override
  protected boolean _remove (N from, N to, E label) {
    if (!super._remove (from, to, label))
      return false;
    
    if (!super._remove (to, from, label))
      throw new AssertionError ("Unable to remove other half of edge!");
    
    return true;
  }
  /**
   * {@inheritDoc}
   */
  @Override
  protected void _set (N from, N to, int weight, E label) {
    super._set (from, to, weight, label);
    super._set (to, from, weight, label);
  }
}
