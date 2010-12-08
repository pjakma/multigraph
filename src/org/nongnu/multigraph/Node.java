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
 * Implementation specific class for the MultiGraph.
 * <p>
 * Maintains state for a Node, including edges out to other nodes
 * 
 * @param N The type of the Nodes in the graph
 * @param E The type of the Edges in the graph
 */
class Node<N,E> {
  /* Hash of destination Nodes to a hash of edge Object to labels
   * i.e.:
   * 
   * destination[A] -> Label Object[A1] -> Edge[A1]
   *                -> Label Object[A2] -> Edge[A2]
   *                -> ...
   * ....
   * destination[N] -> Label Object[N1] -> Edge[N1]
   *                -> Label Object[N2] -> Edge[N2]
   * 
   * destination node:label is 1:1..m
   * label:edge should be 1:1
   */ 
  private Map<Node<N,E>,Map<E,Edge<N,E>>> edgelist 
    = Collections.synchronizedMap
      (new HashMap<Node<N,E>,Map<E,Edge<N,E>>> ());
  /* Cache a set of all edges, so that edges() can be performant */
  private Set<Edge<N,E>> all_edges = new HashSet<Edge<N,E>> ();
  
  // convenience pointer to the user node object
  final N unode;
  
  Node (N node) {
    unode = node;
  }
  
  /* Set Edge from this Node to other Node for the given label.
   *
   * adds the edge if none exist for this Node -> <to,label> 
   * sets the weight if <to,label> edge already exists.
   */
  void set (Node<N,E> to, int weight, E label) {
    Map<E,Edge<N,E>> to_edges = edgelist.get (to);
    Edge<N,E> e;
    
    assert (to != null);
    assert (label != null);
    
    if ((to_edges != null) && ((e = to_edges.get (label)) != null) ) {
      e.set_weight (weight);
      return;
    }
    
    if (to_edges == null) {
      to_edges = new HashMap<E,Edge<N,E>> ();
      edgelist.put (to, to_edges);
    }
    
    all_edges.add ((e = new Edge<N,E> (this.unode, to.unode, weight, label)));
    to_edges.put (label, e);
  }

  private boolean _remove (Node<N,E> to, E label, boolean clear,
                           Iterator<Node<N,E>> edgelist_it) {
    Map<E,Edge<N,E>> to_edges = edgelist.get (to);
    Edge<N,E> e;
    
    assert (to != null);
    
    // nothing to do
    if (to_edges == null)
      return false;
    
    e = to_edges.get (label);
    
    if ((label != null) && (to_edges.remove (label) != null)) {
      assert all_edges.size () > 0;
      boolean ret = all_edges.remove (e);

      /* If there are no more edges to the 'to' node, then remove
       * its entry from the general map of nodes
       */
      if (to_edges.size () == 0) {
        if (edgelist_it == null)
          return edgelist.remove (to) != null;
        edgelist_it.remove ();
      }
      
      return ret;
    }
    
    // label not given or not found - clear all the edges, or finished?
    if (!clear)
      return false;
    
    all_edges.removeAll (to_edges.values ());
    to_edges.clear ();

    if (edgelist_it == null)
      return edgelist.remove (to) != null;

    edgelist_it.remove ();
    return true;
  }
  
  // Removes the edge from this Node to 'to' keyed by <to,label>
  boolean remove (Node<N,E> to, E label) {
    assert to != null;
    assert label != null;
    return _remove (to, label, false, null);
  }
  
  // Removes all edges from this Node to 'to'
  boolean remove (Node<N,E> to) {
    assert to != null;
    boolean ret = true;
    
    for (Edge<N,E> e : edges (to))
      if (! _remove (to, e.label (), false, null))
        ret = false;
    
    return ret;
  }
  
  /**
   * Clear all edges, but leave the node alone.
   */
  boolean clear () {
    boolean ret = true;
    for (Iterator<Node<N,E>> it = edgelist.keySet ().iterator ();
         it.hasNext ();)
      if (! _remove (it.next (), null, true, it))
        ret = false;
    
    return ret;
  }
  // Node-centric out-degree
  // i.e. the number of distinct nodes connected to.
  int nodal_outdegree () {
    return edgelist.size();
  }
  //Edge-centric out-degree
  // i.e. the degree by number of edges.
  int edge_outdegree () {
    return all_edges.size();
  }
  Collection<Map<E,Edge<N,E>>> edgelist () {
    return edgelist.values();
  }
  
  /* Return all edges out of this node */
  Set<Edge<N,E>> edges () {
    return all_edges;
  }

  /* Return edges out of this node, to given node. */
  Collection<Edge<N,E>> edges (Node<N,E> to) {
    Map<E,Edge<N,E>> edges;
    
    if (to == null)
      throw new NullPointerException ("Node get requires non-null argument");
    if ((edges = edgelist.get (to)) == null)
      return Collections.emptySet ();
    
    return edges.values ();
  }
  
  boolean isLinked (Node<N,E> to) {
    if (to == null)
      throw new NullPointerException ("Node get requires non-null argument");
    
    return edgelist.containsKey (to);
  }
  
  public String toString () {
    return unode.toString ();
  }
}
