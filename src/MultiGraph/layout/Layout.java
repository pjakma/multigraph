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
package MultiGraph.layout;

import java.awt.Dimension;
import java.lang.reflect.Constructor;

import MultiGraph.Graph;

public abstract class Layout<N extends PositionableNode, L> {
  Graph<N, L> graph;
  int maxiterations;
  Dimension bound;
  int border; /* border to leave, in points */
  
  public Layout (Graph<N, L> graph, Dimension bound, int maxiterations) {
    this.graph = graph;
    this.maxiterations = maxiterations;
    this.bound = bound;
  }
  /* returns true if layout can still change. If false, then there is no more
   * the layout can do to the graph.
   */
  public abstract boolean layout (float interval);
  
  /* factory related */
  protected static boolean isaLayout (String name) {
      try {
          Class.forName (name);
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
    Layout<N,L> factory (String classname, Graph<N,L> graph, Dimension bound,
                         int maxiterations) {
      Layout<N,L> dp = null;
      Constructor<? extends Layout<N,L>> c;
      
      c = factory_con (Layout.class.getPackage ().getName () 
                       + "." + classname + "Layout");
      
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
