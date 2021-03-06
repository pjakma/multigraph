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

/**
 * An edge, identified immutably by {@literal <from,to,label>}, of some mutable weight.
 *
 * @author Paul Jakma
 *
 * @param N The type of the Node's in the graph.
 * @param E The type of the Edges in the graph
 */
public class Edge<N,E> {
  private int weight;
  private final E label;
  private final N from, to;
  
  /* Constructor is deliberately left as package-scope. This object is meant
   * only to be viewed by users outside of MultiGraph - not created.
   */
  Edge (N from, N to, int weight, E label) {
    assert (weight > 0);
    this.weight = weight;
    this.label = label;
    this.from = from;
    this.to = to;
  }
  
  public E label () {
    return label;
  }
  public N from () {
    return from;
  }
  public N to () {
    return to;
  }
  public int weight () {
    return weight;
  }
  void set_weight (int w) {
      weight = w;
  }
  @Override
  public String toString () {
    return "<" + from + " -> " + to + ": "
           + label
           + ", " + weight
           + ">";
  }
}
