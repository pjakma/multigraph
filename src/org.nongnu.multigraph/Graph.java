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

import java.util.*;

/* General Graph interface, for graphs of N-typed nodes, each having
 * 0 or more directed edges to other nodes, labeled L.
 *
 * Edges may optionally be weighted, otherwise they are assigned a default
 * weight of 1. Weights of edges may be revised.
 *
 */
public interface Graph<N,L> extends Set<N> {

  /* Set an edge from one node to another with the given label. 
   *
   * Weight defaults to 1.
   * Nodes are automatically added to graph, as needs be.
   *
   * If a given edge for <from,to,label> already exists, then the
   * weight is updated.
   */
  void set (N from, N to, L label);
  void set (N from, N to, L label, int weight);
  
  /* Add node to the graph, sans edges */
  boolean add (N node);

  /* Remove a specific edge, given by the label, from between two nodes. */
  boolean remove (N from, N to, L label);
  /* Remove all edges that go from one node to another */
  boolean  remove (N from, N to);
  /* remove node and all its edges */
  boolean remove (N node);
  
  /* Query methods, beyond those available through Set<N> */
  int edge_outdegree (N node);
  int nodal_outdegree (N node);
  float avg_nodal_degree ();
  
  Set<N> successors (N node);
  Set<Edge<N,L>> edges (N node);
}
