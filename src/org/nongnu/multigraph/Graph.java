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

/**
 * General Graph interface, for graphs of N-typed nodes, each having
 * 0 or more edges to other nodes, labeled L.
 * <p>
 * Edges may optionally be weighted, otherwise they are assigned a default
 * weight of 1. Weights of edges may be revised.
 * <p>
 * This interface does not specify how many edges are allowed between nodes 
 * and does specify whether a node may have an edge to itself, or not. A 
 * graph which explicitly allows both these cases is called a "multi-graph",
 * a graph which does not allow either condition is called a "simple-graph".
 * <p>
 * This interface does not restrict whether edges are directed or not. Where
 * edges are directed, then the presence of an edge out from one node to another
 * does not imply that the other node has a corresponding edge back. Where edges
 * are undirected, then the presence of an edge at one node must imply the presence
 * of a similar edge at the other node.
 * <p>
 * This interface does not allow for "hyper-graphs", where 1 edge may connect
 * to multiple nodes, however hyper-graphs can be supported by using a node to act
 * as hyper-edge.
 * 
 * @param N The type of Nodes that will be added to this graph.
 * @param L The type of Label's that will be applied to edges of this graph.
 * 
 * @author Paul Jakma
 */
public interface Graph<N,L> extends Set<N> {

  /**
   * Set an edge from one node to another with the given label. 
   *
   * Weight defaults to 1. Nodes are automatically added to graph, as needs be.
   *
   * If a given edge for &lt;from,to,label&gt; already exists, then the
   * weight is updated.
   * 
   * @param from Node from which the edge originates.
   * @param to Node to which the edges goes.
   * @param label The user's label for this edge. 
   */
  void set (N from, N to, L label);
  /**
   * Set an edge from one node to another with the given label and weight.
   * @param weight a user-specified metric or weight for the edge
   * @see #set(Object, Object, Object)
   */
  void set (N from, N to, L label, int weight);
  
  /**
   * Add the given node to the graph, sans edges
   */
  boolean add (N node);

  /**
   * Remove 1 specific edge, given by the label, from between two nodes. 
   * @return Whether an edge was removed
   */
  boolean remove (N from, N to, L label);
  /**
   * Remove all edges that go from one node to another 
   * @return Whether an edge was removed
   * @see #set(Object, Object, Object)
   * @see #add(Object)
   */
  boolean  remove (N from, N to);
  /**
   * Remove the given node and all its edges
   * @see #remove(Object, Object)
   * @see #remove(Object)
   * @see #set(Object, Object, Object)
   */
  boolean remove (N node);
  
  /* Query methods, beyond those available through Set<N> */
  /**
   * The out-degree for a give node. I.e. the number of edges leaving
   * the node.
   */
  int edge_outdegree (N node);
  /**
   * @param node
   * @return The number of distinct nodes to which this node has edges.
   */
  int nodal_outdegree (N node);
  
  /**
   * The average nodal out-degree.
   */
  float avg_nodal_degree ();
  
  /**
   * @param node The given node, which is to be queried.
   * @return The set of nodes that succeed the given node. I.e. those nodes to which the given node
   *  has an edge.
   */
  Set<N> successors (N node);
  /**
   * @param node The given node, which is to be queried.
   * @return The set of edges that go out from this node.
   */
  Set<Edge<N,L>> edges (N node);
}
