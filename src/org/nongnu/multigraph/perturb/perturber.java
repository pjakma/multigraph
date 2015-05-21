/* This file is part of 'MultiGraph'
 *
 * Copyright (C) 2015 Paul Jakma
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
package org.nongnu.multigraph.perturb;

import java.util.List;
import org.nongnu.multigraph.Edge;

/**
 * Operations common to graph perturbation implementations. These are used
 * to iteratively apply changes to a graph according to some cohesive
 * policy, provided by a concrete implementation.
 *
 * @author Paul Jakma
 */

public interface perturber<N,E> {
  /**
   * Carry out 1 iteration of the perturbation of the graph, according to the
   * policy of the implementation. Edges may be added or removed.
   *
   * @return An unmodifiable list of any edges removed in this iteration.
   */
  public List<Edge<N,E>> perturb ();

  /**
   * Restore edges removed previously, up until the last call to
   * {@link #clear_removed_edges}. This also clears the remember, removed edges.
   *
   * @return The number of restored edges.
   */
  public int restore ();

  /**
   * @return An unmodifiable list view of any remembered, removed edges.
   */
  public List<Edge<N,E>> removed_edges ();

  /**
   * Clear state held for any removed edges. Future {@link #restore} calls will
   * not be able to add back any of the edges held before the call to this
   * method.
   *
   * @return The number of edges cleared.
   */
  public int clear_removed_edges ();
}
