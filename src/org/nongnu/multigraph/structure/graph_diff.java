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
package org.nongnu.multigraph.structure;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.nongnu.multigraph.Graph;
import org.nongnu.multigraph.Edge;

/**
 * Compare two graphs and call the given specified user actions
 * accordingly.
 */
public class graph_diff<N,E> {
  public enum change_state { added, removed };
  public interface node_callback<N> {
    public void action (N node, change_state s);
  }
  public interface edge_callback<N,E> {
    public void action (Edge<N,E> edge, change_state s);
  }
  
  private Graph<N,E> gold = null;
  private Graph<N,E> gnew = null;
  private graph_diff.node_callback<N> node_cb = null;
  private graph_diff.edge_callback<N,E> edge_cb = null;
  private Set<N> all = null;
  private Iterator<N> it = all.iterator ();

  public graph_diff (Graph<N,E> old_graph, Graph<N,E> new_graph,
                     graph_diff.node_callback<N> node_cb,
                     graph_diff.edge_callback<N,E> edge_cb) {
    if (old_graph == null || new_graph == null)
      throw new IllegalArgumentException ("Graph arguments may not be null!");
    if (node_cb == null && edge_cb == null)
      throw new IllegalArgumentException
                                  ("At least one callback argument required!");
    gold = old_graph;
    gnew = new_graph;
    this.node_cb = node_cb;
    this.edge_cb = edge_cb;
    all = new HashSet<N> (gold);
    all.addAll (gnew);
  }

  private void _compare_edges (N n) {
    if (edge_cb == null)
      return;
    
    Set<Edge<N,E>> edges = new HashSet<Edge<N,E>> (gold.edges (n));
    edges.addAll (gnew.edges (n));

    for (Edge<N,E> e : edges) {
      boolean einold = edges.contains (e);
      boolean einnew = edges.contains (e);

      if (einold != einnew)
        edge_cb.action (e, einnew ? change_state.added : change_state.removed);
    }
  }
  public boolean compare_next () {
    if (!it.hasNext ())
      return false;

    N n = it.next ();

    boolean inold = gold.contains (n);
    boolean innew = gnew.contains (n);

    if (inold != innew) {
      change_state s = innew ? change_state.added
                             : change_state.removed;
      if (node_cb != null)
        node_cb.action (n, s);

      if (edge_cb != null) {
        Graph ingraph = innew ? gnew : gold;

        /* all edges have changed */
        Set<Edge<N,E>> edges = ingraph.edges (n);
        for (Edge<N,E> e : edges)
          edge_cb.action (e, s);
      }
    } else {
      /* node is in both, so check edge diff */
      _compare_edges (n);
    }
    
    return true;
  }

  public void compare () {
    while (compare_next ()) {};
  }
}