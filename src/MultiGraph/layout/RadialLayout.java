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

import MultiGraph.Graph;
import MultiGraph.debug;

public class RadialLayout<N extends PositionableNode, L> 
                          extends Layout<N, L> {

  
  public RadialLayout (Graph<N, L> graph, Dimension bound, int maxiterations) {
    super (graph, bound, maxiterations);
  }

  public boolean layout (float interval) {
    /* 10% gap left to border */
    double radius = Math.min (bound.width, bound.height)/2.0;
    double sweep = 2 * Math.PI / graph.size ();
    Vector2D v = new Vector2D (0, radius);
    
    for (N node : graph) {
      node.getPosition ().setLocation (v);
      v.rotate (sweep);
      debug.println ("v: " + v);
    }
    return false;
  }
  
}
