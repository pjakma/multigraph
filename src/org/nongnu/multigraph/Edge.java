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
 * An edge, identified immutably by &lt;from,to,label&gt;, of some mutable weight.
 *
 * @author Paul Jakma
 *
 * @param <N> The type of the Node's in the graph.
 * @param <L> The type of the edge Label's in the graph.
 */
public class Edge<N,L> {
  int weight;
  final L label;
  final N from, to;
  
  /* Constructor is deliberately left as package-scope. This object is meant
   * only to be viewed by users outside of MultiGraph - not created.
   */
  Edge (N from, N to, int weight, L label) {
    assert (weight > 0);
    this.weight = weight;
    this.label = label;
    this.from = from;
    this.to = to;
  }
  
  public L label () {
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
  
  public String toString () {
    return "<" + from + " -> " + to + ": "
           + label
           + ", " + weight
           + ">";
  }
}
