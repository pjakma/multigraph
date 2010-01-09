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

/* Dijkstra's Shortest Path First algoritm, implemented to act on a 
 * Graph of N-nodes and Edges, with L-labels
 */
public class ShortestPathFirst<N,L> {

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
      debug.println ("SPFnode: created " + this);      
    }
    
    public int compareTo (SPFnode<N,L> v) {
      return this.cost - v.cost;
    }
    
    public String toString () {
      return "N: " + n 
             + ", " + cost
             + ", Edge to parent: " 
             + (!parents.isEmpty () ? parents.get (0) : "<none>");
    }
  }
  
  /* Map of user Nodes->SPFnodes, i.e. nodes with a path from the root */
  private HashMap<N,SPFnode<N,L>> spfnodes;
  
  private PriorityQueue<SPFnode<N,L>> q;
  
  // convenience pointer back to the original graph
  final private Graph<N,L> g;
  private N root;
  
  public ShortestPathFirst (Graph<N,L> g) {
    spfnodes = new HashMap<N,SPFnode<N,L>> ();
    q = new PriorityQueue<SPFnode<N,L>> ();
    this.g = g;
  }
  
  /* Explore the node V, adding its child nodes to the queue and relaxing
   * the SPFedges as needed.
   */
  private void explore (SPFnode<N,L> v) {
    SPFnode<N,L> w;
    
    debug.println ("SPF: exploring " + v);
    
    /* For every child, W, of V */
    for (Edge<N,L> e : g.edges (v.n)) {
      /* Relax the edge from V->W */
      
      debug.println ("SPF: relaxing " + e);
      
      if ((w = spfnodes.get (e.to ())) == null) {
        /* W is newly discovered, init and queue */
        w = new SPFnode<N,L> (e.to (), e, e.weight() + v.cost);
        spfnodes.put (w.n, w);
        q.add (w);
      } else if (e.weight() + v.cost < w.cost) { 
        /* V->W is a better path, relax W onto V */
        w.cost = e.weight() + v.cost;
        w.parents.clear();
        w.parents.add (e);
        debug.println ("SPF: lower cost path found " + w);
      } else if (e.weight() + v.cost == w.cost) {
        /* V->W is an equal cost path, add to W's parents */
        w.parents.add (e);
        debug.println ("SPF: equal cost path found " + w);
      }
    }
    
    if (debug.applies()) {
      debug.println ("SPFnodes:");
      for (SPFnode<N,L> s2 : spfnodes.values ())
        debug.println ("\t" + s2);
    }
  }
  
  public void run (N root) {
    SPFnode<N,L> v;
    
    /* init */
    this.root = root;
    spfnodes.clear ();
    q.clear ();
    
    /* initialise the root vertex, and seed the queue with its children */
    debug.println ("SPF: initialising");
    spfnodes.put (root, (v = new SPFnode<N,L> (root, null, 0)));
    explore (v);
    
    /* go through the tree */
    debug.println ("SPF: Search the nodes");
    while ((v = q.poll()) != null) {
      /* increase search radius to the next vertex */
      explore (v);

    }    
    debug.println ("SPF: done");
  }
  
  /* Return the path from the root node to the 'to' node, as a List
   * of Edges.
   *
   * While our SPF supports equal-cost, multiple-paths, we only return
   * one path, for simplicity's sake.
   */
  public List<Edge<N,L>> path (N to) {
    SPFnode<N,L> s;
    LinkedList<Edge<N,L>> l = null;
    Edge<N,L> e;
    
    /* The SPF tree points from child to parent. Walk from 'to' till we get
     * to the root (which has no parents), building the path-List.
     */
    while ((s = spfnodes.get (to)) != null && s.parents.size() > 0) {
      if (l == null)
        l = new LinkedList<Edge<N,L>> ();
      
      debug.println ("in path");
      /* Follow the first path, if there's more than one */
      l.addFirst ((e = s.parents.getFirst ()));
      to = e.from ();
    }
    
    return l;
  }
  
  public N root () {
    return root;
  }
}
