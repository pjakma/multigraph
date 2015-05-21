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
import org.nongnu.multigraph.debug;

/**
 * Perturb the graph by removing and adding back a subset of edges from the
 * graph, on each call to {@link #perturb}, until all the edges have been
 * removed and added back.
 * @author Paul Jakma
 */
public class RemoveAddEach<N,E> implements perturber<N,E> {
  private Graph<N,E> network;
  private int remove_edges = 1;
  private int maxperturbs = 0;
  private long runs = 0;

  private Iterator<Edge<N,E>> run_perturb_edges_iterator = null;
  private List<Edge<N,E>> run_perturb_edges = null;
  private List<Edge<N,E>> last_removed_edges = new ArrayList<> ();
  public List<Edge<N,E>> last_removed_edges () {
    return Collections.unmodifiableList (last_removed_edges);
  }
  
  private void setup () {
    run_perturb_edges = new LinkedList<> ();
    for (N h : network)
      for (Edge<N,E> edge : network.edges (h))
        run_perturb_edges.add (edge);
    run_perturb_edges_iterator = run_perturb_edges.iterator ();
  }

  /**
   * Create new instance to iterate over the specified graph and remove then
   * add 1 edge on each call to perturb, until all edges have been removed
   * and added.
   *
   * @param graph The graph to act on.
   */
  public RemoveAddEach (Graph<N,E> graph) {
    this.network = graph;
    setup ();
  }
  /**
   * Create new instance to iterate over the specified graph and remove then
   * add up to remove_edges # of edges on each call to perturb, up to a maximum
   * number of perturbations.
   * @param graph The graph to act on
   * @param remove_edges The number of edges to remove/add at a time, on each
   *                     call to perturb. This may be specified either as
   *                     an absolute number of edges (&gt; 1), or else as
   *                     a proportion of the number of nodes, (0 &lt; x &lt; 1).
   * @param max_perturbs Limit the number of times groups of edges will be
   *                     removed. A value &lt;= 0 means no limit.
   */
  public RemoveAddEach (Graph<N,E> graph,
                          float remove_edges,
                          int max_perturbs) {
    this.network = graph;
    this.remove_edges
      = (int) (remove_edges < 1 ? Math.max (network.size () * remove_edges, 1)
                                : remove_edges);
    this.maxperturbs = max_perturbs;
    setup ();
  }

  /**
   * Carry out 1 perturbation of the graph, either removing the next group of
   * of edges from the graph, or adding back the group of edges removed in
   * the previous iteration.
   *
   * @return An unmodifiable list of the edges removed in this iteration.
   */
  public List<Edge<N,E>> perturb () {
    List<Edge<N,E>> removed = new ArrayList<> ();

    /* Add back edges removed before, otherwise remove some */
    if (last_removed_edges.size () > 0) {
      restore ();
      runs++;
    } else if ((maxperturbs <= 0) || (runs <= maxperturbs)) {
        perturb_remove_ordered (removed, remove_edges);
    }
    return Collections.unmodifiableList (removed);
  }

  private List<Edge<N,E>> perturb_remove_ordered (List<Edge<N,E>> removed,
                                                  int nremove) {
    while (removed.size () < nremove) {
      if (!run_perturb_edges_iterator.hasNext ())
        break;

      Edge<N,E> edge = run_perturb_edges_iterator.next ();
      debug.println ("Remove edge " + edge);
      removed.add (edge);
      run_perturb_edges_iterator.remove ();
      network.remove (edge.from (), edge.to ());
    }
    last_removed_edges.addAll (removed);
    return removed;
  }

  @Override
  public int restore () {
    int size = last_removed_edges.size ();
    for (Edge<N,E> e : last_removed_edges)
      network.set (e.from (), e.to (), e.label ());
    last_removed_edges.clear ();
    return size;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Edge<N, E>> removed_edges () {
    return Collections.unmodifiableList (last_removed_edges);
  }

  @Override
  public int clear_removed_edges () {
    int size = last_removed_edges.size ();
    last_removed_edges.clear ();
    return size;
  }
}
