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
 * Dijkstra's Shortest Path First algoritm, implemented to act on a 
 * Graph of N-nodes and Edges, with L-labels
 * 
 * @param N The type of the Nodes in the graph
 * @param E The type of the Edges in the graph
 */
public class ShortestPathFirst<N,E> {

  /* internal convenience class to construct the SPF tree
   *
   * Could theoretically use a private instance of a Graph, but this
   * is more simple
   */
  private class SPFnode<N,L> implements Comparable<SPFnode<N,L>> {
    final N n;
    LinkedList<Edge<N,L>> parents;
    int cost = 0;
    
    SPFnode (N n, Edge<N,L> path, int cost) {
      this.n = n;
      this.parents = new LinkedList<Edge<N,L>> ();
      if (path != null)
        this.parents.add (path);
      this.cost = cost;
      debug.printf ("SPFnode: created %s\n", this);      
    }
    
    @Override
    public int compareTo (SPFnode<N,L> v) {
      return this.cost - v.cost;
    }
    
    @Override
    public String toString () {
      return "N: " + n 
             + ", " + cost
             + ", Edge to parent: " 
             + (!parents.isEmpty () ? parents.get (0) : "<none>");
    }
  }
  
  /* Map of user Nodes->SPFnodes, i.e. nodes with a path from the root */
  private HashMap<N,SPFnode<N,E>> spfnodes;
  
  private PriorityQueue<SPFnode<N,E>> q;
  
  // convenience pointer back to the original graph
  final private Graph<N,E> g;
  private N root;
  
  public ShortestPathFirst (Graph<N,E> g) {
    spfnodes = new HashMap<N,SPFnode<N,E>> ();
    q = new PriorityQueue<SPFnode<N,E>> ();
    this.g = g;
    
    if (g == null)
      throw new IllegalArgumentException ("graph must not be null");
  }
  
  /* Explore the node V, adding its child nodes to the queue and relaxing
   * the SPFedges as needed.
   */
  private void explore (SPFnode<N,E> v) {
    SPFnode<N,E> w;
    Set<Edge<N,E>> edges;
    
    debug.printf ("SPF: exploring %s\n", v);
    
    if ((edges = g.edges (v.n)) == null)
      return;
    
    /* For every child, W, of V */
    for (Edge<N,E> e : edges) {
      /* Relax the edge from V->W */
      
      debug.printf ("SPF: relaxing %s\n", e);
      
      if ((w = spfnodes.get (e.to ())) == null) {
        /* W is newly discovered, init and queue */
        w = new SPFnode<N,E> (e.to (), e, e.weight() + v.cost);
        spfnodes.put (w.n, w);
        q.add (w);
      } else if (e.weight() + v.cost < w.cost) { 
        /* V->W is a better path, relax W onto V */
        w.cost = e.weight() + v.cost;
        w.parents.clear();
        w.parents.add (e);
        debug.printf ("SPF: lower cost path found %s\n", w);
      } else if (e.weight() + v.cost == w.cost) {
        /* V->W is an equal cost path, add to W's parents */
        w.parents.add (e);
        debug.printf ("SPF: equal cost path found %s\n", w);
      }
    }
    
    if (debug.applies()) {
      debug.println ("SPFnodes:");
      for (SPFnode<N,E> s2 : spfnodes.values ())
        debug.println ("\t" + s2);
    }
  }
  
  /**
   * Construct the SPF tree rooted at the given node, to be used for
   * subsequent shortest-path query call.
   * @param root Root node for the Shortest-Path First tree.
   */
  public void run (N root) {
    SPFnode<N,E> v;
    
    if (root == null)
      throw new IllegalArgumentException ("root argument must not be null");
    
    /* init */
    this.root = root;
    spfnodes.clear ();
    q.clear ();
    
    /* initialise the root vertex, and seed the queue with its children */
    debug.println ("SPF: initialising");
    spfnodes.put (root, (v = new SPFnode<N,E> (root, null, 0)));
    explore (v);
    
    /* go through the tree */
    debug.println ("SPF: Search the nodes");
    while ((v = q.poll()) != null) {
      /* increase search radius to the next vertex */
      explore (v);

    }    
    debug.println ("SPF: done");
  }
  
  /**
   * Return the path from the root node to the 'to' node, as a List
   * of Edges, in the current SPF tree.
   *
   * While our SPF supports equal-cost, multiple-paths, we only return
   * one path, for simplicity's sake.
   * 
   * @param to The node to query a path for
   * @return List of Edges forming a path to the given node
   */
  public List<Edge<N,E>> path (N to) {
    SPFnode<N,E> s;
    LinkedList<Edge<N,E>> l = null;
    Edge<N,E> e;
    
    /* The SPF tree points from child to parent. Walk from 'to' till we get
     * to the root (which has no parents), building the path-List.
     */
    while ((s = spfnodes.get (to)) != null && s.parents.size() > 0) {
      if (l == null)
        l = new LinkedList<Edge<N,E>> ();
      
      debug.println ("in path");
      /* Follow the first path, if there's more than one */
      l.addFirst ((e = s.parents.getFirst ()));
      to = e.from ();
    }
    
    if (l != null)
      Collections.reverse (l);
    return l;
  }

  /**
   * Return all edges for paths from the root node to the 'to' node, as a
   * List of Edges, in the current SPF tree.
   *
   * @param to The node to query a path for @return List of Edges forming a
   * path to the given node
   * @return Set of edges in the ECMP paths from the root to the given node.
   */
  public Set<Edge<N,E>> edges (N to) {
    SPFnode<N,E> s;
    Set<Edge<N,E>> edges = null;
    LinkedList<N> explore = new LinkedList<> ();
    N n;
    
    explore.add (to);
    while ((n = explore.poll ()) != null) {      
      if ((s = spfnodes.get (n)) == null || s.parents.size () == 0)
        continue;
      
      if (edges == null)
        edges = new HashSet<Edge<N,E>> ();
      
      for (Edge<N,E> e : s.parents) {
        explore.add (e.from ());
        edges.add (e);
      }
    }    
    return edges;
  }
  
  /**
   * Return all edges in the SPF tree of the root.
   *
   * @return List of Edges in the SPF tree.
   */
  public Set<Edge<N,E>> edges () {
    Set<Edge<N,E>> edges = null;
    Edge<N,E> e;

    for (SPFnode<N,E> s : spfnodes.values()) {
      if (edges == null)
        edges = new HashSet<Edge<N,E>> ();
      edges.addAll (s.parents);
    }
    return edges;
  }
  
  /**
   * Return the next-hop node for the shortest path from the root node to 
   * the given node.
   * @param to Destination node to query path for
   * @return The best next-hop from the root
   */
  public N nexthop (N to) {
    SPFnode<N,E> s;
    N prev = null;
    
    /* The SPF tree points from child to parent. Walk from 'to' till we get
     * to the root (which has no parents), building the path-List.
     */
    while ((s = spfnodes.get (to)) != null && s.parents.size() > 0) {
      /* Follow the first path, if there's more than one */
      prev = to;
      to = s.parents.getFirst ().from ();
    }
    
    return prev;
  }
  
  public N root () {
    return root;
  }
}
