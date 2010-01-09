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
import java.util.Random;

import MultiGraph.Edge;
import MultiGraph.Graph;
import MultiGraph.debug;

/* See "Graph Drawing by Force-directed Placement", Fruchterman & Reingold */
public class ForceLayout<N extends PositionableNode, L> extends Layout<N, L> {
  private double k;
  private double mintemp = 0.04;
  private double temperature = 0.08;
  
  public ForceLayout (Graph<N, L> graph, Dimension bound, int maxiterations) {
    super (graph, bound, maxiterations);
    
    k = 0.7 * Math.sqrt ((bound.getWidth () * bound.getHeight ())/graph.size ());
    
    debug.println ("k: " + k);
  }
  
  private double attraction (double delta) {
    return (delta * delta) / k;
  }
  private double repulsion (double delta) {
    return (k * k) / delta;
  }
  
  private double decay (double temperature) {
    return temperature * 0.7;
  }
  public boolean layout (float interval) {
    double kve = 0;
    
    debug.println ("force-layout start");
    
    for (N n : graph) {
      Vector2D disp = n.getVelocity ();
      disp.setLocation (0, 0);
      
      debug.println ("node: " + n + ", pos: " + n.getPosition ());
      
      for (N other : graph) {
        if (other == n)
          continue;
        
        if (other.getPosition ().x == n.getPosition ().x 
            && other.getPosition ().y == n.getPosition ().y) {
          Vector2D vrandom = new Vector2D (0.01, 0);
          Random r = new Random ();
          vrandom.rotate (r.nextInt (360));
          other.getPosition ().plus (vrandom);
        }
        
        debug.println ("\trepulsion with " + other 
                        + ", " + other.getPosition ());
        
        Vector2D delta = new Vector2D (n.getPosition ());
        delta.minus (other.getPosition ());
        
        debug.println ("\t\tdelta1: " + delta);
        
        double repf = repulsion (delta.length ());
        
        if (Double.isInfinite (repf))
          
        
        debug.println ("\t\trepf: " + repf);
        
        delta.normalise ();
        delta.times (repf);
        
        debug.println ("\t\tdelta2: " + delta);
        
        disp.plus (delta);
        
        debug.println ("\tdisp after repf: " + disp);
      }
      
      for (Edge<N, L> e : graph.edges (n)) {
        if (e.to () == e.from ())
          continue;
        
        debug.println ("\tattraction with " + e.to ());
        
        Vector2D delta = new Vector2D (n.getPosition ());
        delta.minus (e.to ().getPosition ());
        
        debug.println ("\t\tdelta1: " + delta + ", len " + delta.length ());
        
        double attrf = attraction (delta.length ());
        debug.println ("\t\tattrf: " + attrf);
        
        delta.normalise ();
        delta.times (attrf);
        
        debug.println ("\t\tdelta2: " + delta);
        
        disp.minus (delta);
        debug.println ("\tdisp: " + disp);
      }
      
      debug.println ("\tresultant v: " + disp);
      
      Vector2D pos = n.getPosition ();
      Vector2D v = n.getVelocity ();
      
      debug.println ("node pos: " + pos);
      debug.println ("\tv: " + v);
      
      temperature = Math.max (decay (temperature), mintemp);
      v.times (interval * temperature);
      
      debug.println ("\tv2: " + v);
      
      pos.plus (v);
      
      debug.println ("\tp2: " + pos);
      
      pos.x = Math.min (Math.max (-bound.width/2, pos.x), bound.width/2);
      pos.y = Math.min (Math.max (-bound.height/2, pos.y), bound.height/2);
      
      double mag = v.magnitude ();      
      kve += n.getMass () * mag * mag;
      
      debug.println ("\tresult: " + pos);
    }
    
    debug.println ("kve: " + kve);
    
    return kve > 1;
  }
}
