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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.nongnu.multigraph.Edge;
import org.nongnu.multigraph.Graph;

/**
 * Perturb the graph by removing a randomly chosen set of edges from the graph.
 * Methods are also given to restore removed edges, and clear the state kept
 * for removed edges.
 * @author Paul Jakma
 */
public class RandomRemove<N,E> implements perturber<N,E> {

  private List<Edge<N,E>> removed_remembered_edges = new ArrayList<> ();
  private Graph<N,E> network;
  private int remove_edges = 1;
  private int maxperturbs = -1;
  private int runs = 0;

  private List<Edge<N,E>> run_perturb_edges = null;

  private boolean combine = false;
  public void combine (boolean combine) { this.combine = combine; }
  public boolean combine () { return combine; }

  private void setup () {
    run_perturb_edges = new LinkedList<> ();
    for (N h : network)
      for (Edge<N,E> edge : network.edges (h))
        run_perturb_edges.add (edge);
  }

  /**
   * Create new instance to randomly remove 1 edge on each call to
   * @link #remove}.
   * and added.
   *
   * @param graph The graph to act on.
   */
  public RandomRemove (Graph<N,E> graph) {
    this.network = graph;
    setup ();
  }
  /**
   * Create new instance to iterate over the specified graph and remove then
   * add up to remove_edges # of edges on each call to {@link #remove},
   *
   * @param graph The graph to act on
   * @param remove_edges The number of edges to remove/add at a time, on each
   *                     call to remove. This may be specified either as
   *                     an absolute number of edges (&gt; 1), or else as
   *                     a proportion of the number of nodes, (0 &lt; x &lt; 1).
   */
  public RandomRemove (Graph<N,E> graph,
                       float remove_edges) {
    this.network = graph;
    this.remove_edges
      = (int) (remove_edges < 1 ? Math.max (network.size () * remove_edges, 1)
                                : remove_edges);
    setup ();
  }

  /**
   * Create new instance to iterate over the specified graph and remove then
   * add up to remove_edges # of edges on each call to {@link #remove},
   *
   * @param graph The graph to act on
   * @param remove_edges The number of edges to remove/add at a time, on each
   *                     call to remove. This may be specified either as
   *                     an absolute number of edges (&gt; 1), or else as
   *                     a proportion of the number of nodes, (0 &lt; x &lt; 1).
   * @param max_perturbs Limit the number of times groups of edges will be
   *                     removed. A value &lt;= 0 means no limit.
   */
  public RandomRemove (Graph<N,E> graph,
                       float remove_edges,
                       int max_perturbs) {
    this.network = graph;
    this.maxperturbs = max_perturbs;
    this.remove_edges
      = (int) (remove_edges < 1 ? Math.max (network.size () * remove_edges, 1)
                                : remove_edges);
    setup ();
  }
  /**
   * Randomly remove edges from the graph, up to the specified number of edges
   * for the class.
   *
   * @return An unmodifiable list of the edges removed in this particular
   *         iteration.
   */
  public List<Edge<N,E>> remove () {
    List<Edge<N,E>> removed_edges = new ArrayList <> ();
    Iterator<N> ith = network.random_node_iterable ().iterator ();
    int num = remove_edges;

    while (ith.hasNext () && num > 0) {
      N h = ith.next ();

      Iterator<Edge<N,E>> ite = network.random_edge_iterable (h).iterator ();

      if (!ite.hasNext ())
        break;

      Edge<N,E> e = ite.next ();
      removed_edges.add (e);

      network.remove (h, e.to ());
      num--;
    }
    removed_remembered_edges.addAll (removed_edges);
    return Collections.unmodifiableList (removed_edges);
  }

  /**
   * Restore edges removed previously, up until the last call to
   * {@link #clear_removed_edges}.
   */
  public int restore () {
    int size = removed_remembered_edges.size ();
    for (Edge<N,E> e : removed_remembered_edges)
      network.set (e.from (), e.to (), e.label ());
    removed_remembered_edges.clear ();
    return size;
  }

  /**
   * @return An unmodifiable list view of the remembered, removed edges.
   */
  public List<Edge<N,E>> removed_edges () {
    return Collections.unmodifiableList (removed_remembered_edges);
  }
  
  /**
   * Clear state held for the removed edges. Future {@link #restore} calls will
   * not be able to add back any of the edges held before the call to this
   * method.
   */
  public int clear_removed_edges () {
    int size = this.removed_remembered_edges.size ();
    removed_remembered_edges.clear ();
    return size;
  }
  
  /**
   * Carry out another iteration of random-removes by restoring any previously
   * removed edges, clearing the removed_edge set, and then removing the next
   * random set of edges.
   * @return
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<Edge<N, E>> perturb () {
    int num = restore ();
    clear_removed_edges ();

    if (!combine && num > 0)
      return Collections.EMPTY_LIST;
    
    if (maxperturbs > 0 && runs >= maxperturbs)
      return Collections.EMPTY_LIST;
    runs++;
    return remove ();
  }
}
