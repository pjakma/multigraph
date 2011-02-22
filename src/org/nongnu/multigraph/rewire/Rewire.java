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
package org.nongnu.multigraph.rewire;

import java.awt.Dimension;
import java.lang.reflect.Constructor;

import org.nongnu.multigraph.Graph;
import org.nongnu.multigraph.layout.Layout;
import org.nongnu.multigraph.layout.PositionableNode;

/**
 * Abstract interface for algorithms to rewire the edges of a graph.
 * 
 * All edges may be initially cleared from the graph.
 * 
 * @param <N> The type of the Nodes in the graph
 * @param <E> The type of the Edges in the graph
 * 
 * @author paul
 *
 */
public abstract class Rewire<N,E> {
  protected Graph<N,E> graph;
  protected EdgeLabeler<N,E> el;
  
  public Rewire (Graph<N,E> graph, EdgeLabeler<N,E> el) {
    this.graph = graph;
    this.el = el;
  }
  
  /**
   * Rewire the whole graph. All edges potentially are first cleared.
   * Then some edges added back, according to the specified algorithm.
   */
  public abstract void rewire ();
  
  /**
   * Add a single node to the graph.
   * <p>
   * This may (or may not) have different resource usage relative to rewire().
   * E.g. it may use less memory than rewire(), but more CPU over all if called 
   * for a significant number of nodes. 
   * <p>
   * This method is optional, and not all implementations support it.
   *
   * @throws UnsupportedOperationException If this method is not supported.
   * @param node
   */
  public void add (N node) { throw new UnsupportedOperationException ();};
  

  /* XXX: Below is copied from Layout. It's mostly identicaly bar s/Layout/Rewire/
   * but not completely trivial to factor out..
   */
  
  /* Turn algorithm name to fully qualified class name */
  private static String alg2class_name (String algorithm) {
    return Rewire.class.getPackage ().getName () + "." + algorithm + "Rewire";
  }
  
  /**
   * Query whether the given string is an implemented rewire algorithm.
   * @param name rewiring algorithm name
   * @return Whether a rewiring implementation exists for the given algorithm name
   */
  public static boolean isaRewire (String name) {
      try {
          Class.forName (alg2class_name (name));
      } catch (ClassNotFoundException e) {
          return false;
      }
      return true;
  }
  
  /* Factory methods to dynamically translate strings to concrete Layout classes */
  @SuppressWarnings("unchecked")
  private static <N,L>  Constructor<? extends Rewire<N,L>> factory_con (String classname) {
      Class<? extends Rewire<N,L>> client_class = null;
      Constructor<? extends Rewire<N,L>> c = null;

      try {
          client_class = (Class<? extends Rewire<N,L>>) Class.forName (classname);
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
          c = client_class.getConstructor (Graph.class, EdgeLabeler.class);
      } catch (Exception e) {
          System.out.println ("Error settting up client class: ");
          System.out.println (e.toString());
          System.exit (1);
      }
      
      if (c == null)
          return null;

      return c;
  }   
  public final static <N,L> Rewire<N,L> factory (String algname, Graph<N,L> graph, 
                                                 EdgeLabeler<N,L> el) {
      Rewire<N,L> dp = null;
      Constructor<? extends Rewire<N,L>> c;
      
      c = factory_con (alg2class_name (algname));
      
      try {
          dp = c.newInstance (graph, el);
      } catch (Exception e) {
          System.out.println ("Error creating instance of client class: ");
          System.out.println (e.toString());
          System.exit (1);
      }
      
      return dp;
  }
}
