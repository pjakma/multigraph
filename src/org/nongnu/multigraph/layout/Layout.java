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
package org.nongnu.multigraph.layout;

import java.awt.Dimension;
import java.lang.reflect.Constructor;

import org.nongnu.multigraph.Graph;

/**
 * Abstract implementation of a layout algorithm. To be used something like:
 * <p>
 * <code><pre>
 * new Layout l = Layout.factory ("layout_name", graph, bound_dimension, 10);
 * while (l.layout (time_passed));
 *   &lt;do whatever other work&gt;
 * </pre></code>
 * <p>
 * In order to be able to apply a layout algorithm to a graph, the graph's N-type
 * nodes must implement the {@link PositionableNode} interface.
 * 
 * @author paul
 *
 * @param <N> The type of the Nodes in the graph, which must implement PositionableNode
 * @param <E> The type of the Edges in the graph
 */
public abstract class Layout<N extends PositionableNode, E> {
  Graph<N, E> graph;
  int maxiterations;
  int iterations = 0;
  Dimension bound;
  int border; /* border to leave, in points */
  
  public int maxiterations () {
    return maxiterations;
  }

  public Layout<N, E> maxiterations (int maxiterations) {
    this.maxiterations = maxiterations;
    return this;
  }

  public Layout (Graph<N, E> graph, Dimension bound) {
    this.graph = graph;
    this.maxiterations = 0;
    this.bound = bound;
  }
  
  public Layout (Graph<N, E> graph, Dimension bound, int maxiterations) {
    this.graph = graph;
    this.maxiterations = maxiterations;
    this.bound = bound;
  }
  /** 
   * returns true if layout can still change. If false, then there is no more
   * the layout can do to the graph.
   * 
   * This default implementation does nothing except enforce the maximum 
   * iterations constraint.
   */
  public boolean layout (float interval) {
    return maxiterations > 0 ? iterations++ < maxiterations : true;
  }
  
  /* Turn algorithm name to fully qualified class name */
  private static String alg2class_name (String algorithm) {
    return Layout.class.getPackage ().getName () + "." + algorithm + "Layout";
  }
  
  /**
   * Query whether the given string is an implemented layout algorithm.
   * @param name Layout algorith name
   * @return Whether a layout implementation exists for the given algorithm name
   */
  public static boolean isaLayout (String name) {
      try {
          Class.forName (alg2class_name (name));
      } catch (ClassNotFoundException e) {
          return false;
      }
      return true;
  }
  
  /* Factory methods to dynamically translate strings to concrete Layout classes */
  @SuppressWarnings("unchecked")
  private static <N extends PositionableNode,L> 
    Constructor<? extends Layout<N,L>> factory_con (String classname) {
      Class<? extends Layout<N,L>> client_class = null;
      Constructor<? extends Layout<N,L>> c = null;

      try {
          client_class = (Class<? extends Layout<N,L>>) Class.forName (classname);
      } catch (Exception e) {
          System.out.println ("Client class doesn't exist: ");
          System.out.println (e);
          System.exit (1);
      }
      /* Eclipse complains that it's possible to get through the above try/catch
       * with client_class still not initialised. I think because its too dumb
       * to realise what System.exit means. So this is just to shut up Eclipse :(
       */
      if (client_class == null)
          return null;

      try {
          c = client_class.getConstructor (Graph.class, Dimension.class, int.class);
      } catch (Exception e) {
          System.out.println ("Error settting up client class: ");
          System.out.println (e.toString());
          System.exit (1);
      }
      
      if (c == null)
          return null;

      return c;
  }   
  public final static <N extends PositionableNode,L>
    Layout<N,L> factory (String algname, Graph<N,L> graph, Dimension bound,
                         int maxiterations) {
      Layout<N,L> dp = null;
      Constructor<? extends Layout<N,L>> c;
      
      c = factory_con (alg2class_name (algname));
      
      try {
          dp = c.newInstance (graph, bound, maxiterations);
      } catch (Exception e) {
          System.out.println ("Error creating instance of client class: ");
          System.out.println (e.toString());
          System.exit (1);
      }
      
      return dp;
  }
}
