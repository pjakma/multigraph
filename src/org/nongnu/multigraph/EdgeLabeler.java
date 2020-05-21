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
 * Callback interface to forward the labelling of each new edge back to 
 * the user.
 * 
 * @param <N> The type of the Nodes in the graph
 * @param <E> The type of the Edges in the graph
 */
public interface EdgeLabeler<N, E> {
  /**
   * @deprecated Use {@link #getEdge instead}
   * Java doesn't do interface versions, so this has to be supported forever,
   * or I just break the i'face and update my uses.
   * @param from The from node in the Edge to label
   * @param to The to node in the edge to label
   * @return The graph user's label for this edge.
   */
  public E getLabel (N from, N to);
  /**
  * Callback for the graph user to specify its label for a new edge.
   * @param from The from node in the Edge to label
   * @param to The to node in the edge to label
   * @return The graph user's label for this edge.
   */    
  public E getEdge (N from, N to);
}
