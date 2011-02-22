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
 * @param N The type of the Nodes in the graph
 * @param E The type of the Edges in the graph
 * 
 * @author Paul Jakma
 */
public interface Graph<N,E> extends Set<N> {
  /**
   * @return Whether the graph is directed or not, i.e. whether
   *         edges have a direction.
   */
  boolean is_directed ();
  /**
   * @return Whether the graph is simple or not. A simple graph
   *         allows only single edges between nodes.
   */
  boolean is_simple ();
  
  /**
   * Set an edge from one node to another with the given label. 
   *
   * Weight defaults to 1. Nodes are automatically added to graph, as needs be.
   *
   * If a given edge for {@literal <from,to,label>} }already exists, then the
   * weight is updated.
   * 
   * @param from Node from which the edge originates.
   * @param to Node to which the edges goes.
   * @param label The user's label for this edge. 
   */
  void set (N from, N to, E label);
  /**
   * Set an edge from one node to another with the given label and weight.
   * @param weight a user-specified metric or weight for the edge
   * @see #set(Object, Object, Object)
   */
  void set (N from, N to, E label, int weight);
  
  /**
   * Add the given node to the graph, sans edges
   */
  boolean add (N node);

  /**
   * Remove 1 specific edge, given by the label, from between two nodes. 
   * @return Whether an edge was removed
   */
  boolean remove (N from, N to, E label);
  /**
   * Remove all edges that go from one node to another 
   * @return Whether an edge was removed
   * @see #set(Object, Object, Object)
   * @see #add(Object)
   */
  boolean  remove (N from, N to);
  /**
   * Remove the given node and all its edges.
   * @see #remove(Object, Object)
   * @see #remove(Object)
   * @see #set(Object, Object, Object)
   */
  boolean remove (N node);
  
  /**
   * Clear all edges from the graph.
   * 
   * This can NOT be done by iterating over the sets of edges from each node,
   * as that will raise a concurrent modification exception.
   */
  void clear_all_edges ();
  
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
   * @return The average nodal out-degree.
   */
  float avg_nodal_degree ();
  
  /**
   * @return The maximum degree of any node in the graph
   */
  int max_nodal_degree ();
  
  /**
   * @param node The given node, which is to be queried.
   * @return The set of nodes that succeed the given node. I.e. those nodes 
   *         to which the given node has an edge. The returned set is read-only
   *         and may not be modified. The returned set will be null
   *         if the node does not exist, and will be the empty set if
   *         the node exists but has no successors.
   */
  Set<N> successors (N from);
  /**
   * @param node The given node, which is to be queried.
   * @return The set of edges that go out from this node. The returned set is 
   *          read-only and may not be modified. The set will be null
   *          if the node does not exist, and an empty set if the node
   *          exists but has no edges.
   */
  Set<Edge<N,E>> edges (N from);
  
  /**
   * Find the edges going <em>from</em> one node <em>to</em> another node.
   * @param from Which node we want to query edges from.
   * @param to The node to which we're looking for an edge.
   * @return An immutable Collection of edges going from 
   *         @<em>from</em>-&gt;<em>to</em> if the from node exists, 
   *         or {@code null} otherwise.
   * @see #edges(Object)
   */
  Collection<Edge<N,E>> edges (N from, N to);
  /**
   * Find the first edge from a node to another node.
   * @param from Which node we want to query edges from.
   * @param to The node to which we're looking for an edge.
   * @return An edge going from <em>from</em>-&gt;<em>to</em> if any exist,
   *         or {@code null} otherwise.
   */
  Edge<N,E> edge (N from, N to);
  
  /**
   * Determine if there is a link from one node to another.
   * 
   * @return True if an edge exists from -&gt; to
   */
  boolean is_linked (N from, N to);
  
  /**
   * Find the edge with the given label, from the one node to another
   * node, if it exists.
   * @param from Which node we want to query edges from.
   * @param to The node to which we're looking for an edge.
   * @param label The label of the edge we're interested in.
   * @return The edge going from <em>from</em>-&gt;<em>to</em> specified
   *         by the given label, if it exists,
   *         or {@code null} otherwise.
   */
  Edge<N,E> edge (N from, N to, E label);
  
  /**
   * Provide a random-access Iterable over the &lt;N&gt;-nodes in the graph.
   */
  public Iterable<N> random_node_iterable ();
  
  /* Shame there's no Observable interface ? */
  /**
   * @see java.util.Observable
   */
  void addObserver(Observer o);
  /**
   * @see java.util.Observable
   */
  int  countObservers();
  /**
   * @see java.util.Observable
   */
  void  deleteObserver(Observer o);
  /**
   * @see java.util.Observable
   */
  void deleteObservers();
  /**
   * @see java.util.Observable
   */
  boolean hasChanged();
  /**
   * @see java.util.Observable
   */
  void notifyObservers();
  /**
   * @see java.util.Observable
   */
  void notifyObservers(Object arg);
  /**
   * "Plug" delivery of Observable events to observers, so that any such events
   * are instead queued up internally, and potentially coalesced, rather than
   * delivered to Observers.
   * 
   * This potentially allows bulk updates to be made to the graph more
   * efficiently.
   */
  void plugObservable ();
  /**
   * Unplug the delivery of Observable events. Stored events will be delivered,
   * though potentially coalesced, so that multiple events for the same object
   * are collapsed into one. Further, a general "notifyObservers" event will
   * coalesce with and subsume *all* events for specific objects. 
   */
  void unplugObservable ();
}
