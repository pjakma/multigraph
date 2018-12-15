package org.nongnu.multigraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * Partition the nodes of the supplied graph.
 *  
 * Assignment of nodes to partitions is controlled by a user supplied
 * callback class, with the PartitionCallbacks<N,E> interface.
 * 
 * Updates to the underlying graph automatically keep the partition updated.
 *
 * @author Paul Jakma
 */
public class PartitionGraph<N,E> implements Graph<N,E>, Observer {
  private Graph<N,E> graph;
  private int num_partitions;
  private PartitionCallbacks<N,E> cb;
  private Set<N> [] partitions;
  
  public interface PartitionCallbacks<N,E> {
    /**
     * Create and return the graph to be wrapped and partitioned
     * @return The graph to be partitioned
     */
    public Graph<N,E> create_graph ();
    /**
     * @return The number of partitions, this must not change.
     */
    public int num_partitions ();
    /**
     * @return Map a node to a partition ID.
     */
    public int node2partition (N node);
  }
  
  @SuppressWarnings ("unchecked")
  public PartitionGraph (PartitionCallbacks<N,E> cb) {
    this.graph = cb.create_graph ();
    this.cb = cb;
    num_partitions = cb.num_partitions ();
    partitions = (Set<N>[]) new Object [num_partitions];
    for (int i = 0; i < partitions.length; i++)
      partitions[i] = new HashSet<N> ();
    graph.addObserver (this);
  }

  private int part_id (final N node) {
    return cb.node2partition (node) % num_partitions;
  }
  private void partition_add (final N node) {
    partitions[part_id (node)].add (node);
  }
  private void partition_remove (final N node) {
    if (!graph.contains (node))
      partitions[part_id (node)].remove (node);
  }

  /**
   * Retrieve a particular partition of the nodes.
   * @param id The partition id to retrieve
   * @return A Set<N> of the nodes in this partition,
   */
  public Set<N> partition (int id) {
    if (id >= partitions.length)
      throw new IllegalArgumentException ("Partition id out of bounds: " + id);
    
    return java.util.Collections.unmodifiableSet (partitions[id]);
  }
  public int partitions () {
    return partitions.length;
  }
  /* Graph overrides */
  @Override
  public boolean is_directed () {
    return graph.is_directed ();
  }

  @Override
  public boolean is_simple () {
    return graph.is_simple ();
  }

  @Override
  public void set (N from, N to, E label) {
    partition_add (from);
    partition_add (to);
    graph.set (from, to, label);
  }

  @Override
  public void set (N from, N to, E label, int weight) {
    partition_add (from);
    partition_add (to);
    graph.set (from, to, label, weight);
  }

  @Override
  public boolean add (N node) {
    partition_add (node);
    return graph.add (node);
  }

  @Override
  public boolean remove (N from, N to, E label) {
    boolean ret = graph.remove (from, to, label);
    partition_remove (from);
    partition_remove (to);
    return ret;
  }

  @Override
  public boolean remove (N from, N to) {
    boolean ret = graph.remove (from, to);
    partition_remove (from);
    partition_remove (to);
    return ret;
  }

  @Override
  public void clear_all_edges () {
    graph.clear_all_edges ();
  }

  @Override
  public int edge_outdegree (N node) {
    return graph.edge_outdegree (node);
  }
  
  @Override
  public int nodal_outdegree (N node) {
    return graph.nodal_outdegree (node);
  }

  @Override
  public float avg_nodal_degree () {
    return graph.avg_nodal_degree ();
  }

  @Override
  public long link_count () {
    return graph.link_count ();
  }

  @Override
  public int max_nodal_degree () {
    return graph.max_nodal_degree ();
  }

  @Override
  public Set<N> successors (N from) {
    return graph.successors (from);
  }

  @Override
  public Set<Edge<N,E>> edges (N from) {
    return graph.edges (from);
  }

  @Override
  public Collection<Edge<N,E>> edges (N from, N to) {
    return graph.edges (from, to);
  }

  @Override
  public Edge<N,E> edge (N from, N to) {
    return graph.edge (from, to);
  }

  @Override
  public boolean is_linked (N from, N to) {
    return graph.is_linked (from, to);
  }

  @Override
  public Edge<N,E> edge (N from, N to, E label) {
    return graph.edge (from, to, label);
  }

  @Override
  public Iterable<N> random_node_iterable () {
    return graph.random_node_iterable ();
  }

  @Override
  public Iterable<Edge<N,E>> random_edge_iterable (N n) {
    return graph.random_edge_iterable (n);
  }

  @Override
  public void addObserver (Observer o) {
    graph.addObserver (o);
  }

  @Override
  public int countObservers () {
    return graph.countObservers ();
  }

  @Override
  public void deleteObserver (Observer o) {
    graph.deleteObserver (o);
  }

  @Override
  public void deleteObservers () {
    graph.deleteObservers ();
  }

  @Override
  public boolean hasChanged () {
    return graph.hasChanged ();
  }

  @Override
  public void notifyObservers () {
    graph.notifyObservers ();
  }

  @Override
  public void notifyObservers (Object arg) {
    graph.notifyObservers (arg);
  }

  @Override
  public void plugObservable () {
    graph.plugObservable ();
  }

  @Override
  public void unplugObservable () {
    graph.unplugObservable ();
  }

  @Override
  public int size () {
    return graph.size ();
  }

  @Override
  public boolean isEmpty () {
    return graph.isEmpty ();
  }

  @Override
  public boolean contains (Object o) {
    return graph.contains (o);
  }

  @Override
  public Iterator<N> iterator () {
    return graph.iterator ();
  }

  @Override
  public Object[] toArray () {
    return graph.toArray ();
  }

  @Override
  public <T> T[] toArray (T[] ts) {
    return graph.toArray (ts);
  }

  @SuppressWarnings ("unchecked")
  @Override
  public boolean remove (Object o) {
    partition_remove ((N) o);
    return graph.remove (o);
  }

  @Override
  public boolean containsAll (Collection<?> clctn) {
    return graph.containsAll (clctn);
  }

  @Override
  public boolean addAll (Collection<? extends N> clctn) {
    return graph.addAll (clctn);
  }

  @Override
  public boolean retainAll (Collection<?> clctn) {
    return graph.retainAll (clctn);
  }

  @Override
  public boolean removeAll (Collection<?> clctn) {
    return graph.removeAll (clctn);
  }

  @Override
  public void clear () {
    graph.clear ();
  }

  @Override
  public void update (Observable o, Object o1) {
    if (o != graph)
      return;

    @SuppressWarnings ("unchecked")
    N node = (N) o1;

    Set<N> partition = partitions[part_id (node)];

    if (partition.contains (node) && !graph.contains (node))
      partition.remove (node);
    if (!partition.contains (node) && graph.contains (node))
      partition.add (node);
  }
}
