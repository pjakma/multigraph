/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.nongnu.multigraph;

import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author paul
 */
public class SyncGraph<N,E> implements Graph<N,E> {
  private Graph<N,E> graph;

  public SyncGraph (Graph<N,E> graph) {
    this.graph = graph;
  }

  @Override
  public synchronized boolean is_directed () {
    return graph.is_directed ();
  }

  @Override
  public synchronized boolean is_simple () {
    return graph.is_simple ();
  }

  @Override
  public synchronized void set (N from, N to, E label) {
    graph.set (from, to, label);
  }

  @Override
  public synchronized void set (N from, N to, E label, int weight) {
    graph.set (from, to, label, weight);
  }

  @Override
  public synchronized boolean add (N node) {
    return graph.add (node);
  }

  @Override
  public synchronized boolean remove (N from, N to, E label) {
    return graph.remove (from, to, label);
  }

  @Override
  public synchronized boolean remove (N from, N to) {
    return graph.remove (from, to);
  }

  @Override
  public synchronized void clear_all_edges () {
    graph.clear_all_edges ();
  }

  @Override
  public synchronized int edge_outdegree (N node) {
    return graph.edge_outdegree (node);
  }

  @Override
  public synchronized int nodal_outdegree (N node) {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized float avg_nodal_degree () {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized long link_count () {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized int max_nodal_degree () {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized Set<N> successors (N from) {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized Set<Edge<N, E>> edges (N from) {
    throw new UnsupportedOperationException ("Not supported yet.");
  }
  
  @Override
  public synchronized Stream<Edge<N,E>> stream (N from) {
    return graph.stream (from);
  }
  
  @Override
  public synchronized Collection<Edge<N, E>> edges (N from, N to) {
    throw new UnsupportedOperationException ("Not supported yet.");
  }
  
  @Override
  public synchronized Edge<N, E> edge (N from, N to) {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized boolean is_linked (N from, N to) {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized Edge<N, E> edge (N from, N to, E label) {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized Iterable<N> random_node_iterable () {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized Iterable<Edge<N, E>> random_edge_iterable (N n) {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized void addObserver (Observer o) {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized int countObservers () {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized void deleteObserver (Observer o) {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized void deleteObservers () {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized boolean hasChanged () {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized void notifyObservers () {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized void notifyObservers (Object arg) {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized void plugObservable () {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized void unplugObservable () {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized int size () {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized boolean isEmpty () {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized boolean contains (Object o) {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized Iterator<N> iterator () {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized Object[] toArray () {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized <T> T[] toArray (T[] ts) {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized boolean remove (Object o) {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized boolean containsAll (Collection<?> clctn) {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized boolean addAll (Collection<? extends N> clctn) {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized boolean retainAll (Collection<?> clctn) {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized boolean removeAll (Collection<?> clctn) {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public synchronized void clear () {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  public PluggableObservable edge_events () {
    return graph.edge_events ();
  }
}
