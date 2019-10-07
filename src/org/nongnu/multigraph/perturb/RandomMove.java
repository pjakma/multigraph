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

import java.awt.Dimension;
import java.util.Random;

import org.nongnu.multigraph.Graph;
import org.nongnu.multigraph.debug;
import org.nongnu.multigraph.layout.PositionableNode;
import org.nongnu.multigraph.layout.Vector2D;
import org.nongnu.multigraph.rewire.Rewire;
import org.nongnu.multigraph.rewire.CartesianRewire;
import org.nongnu.multigraph.EdgeLabeler;

/**
 * A graph perturbing class which emulates a mobile network, by moving
 * PositionableNode nodes according to their velocity, within the giving
 * bounding area, and then applying cartesian rewire.
 *
 * Nodes that reach the bound have their velocity changed by 180° ± a random
 * amount in a Gaussian distribution.
 *
 * This perturb class is intended to be called in a loop.
 *
 * @param <N>
 * @param <L>
 */
public class RandomMove<N extends PositionableNode,L>
       extends Rewire<N,L> {
  private final CartesianRewire<N,L> cl;
  private final Dimension bound;
  private final float default_speed;
  private Random r = new Random ();
  private float maxrange;
  private final int default_angle_dev = 75;
  private final int angle_dev;
  
  EdgeLabeler<N, L> probel = new EdgeLabeler<N, L> () {
    @Override
    public L getEdge (N from, N to) {
      double range = maxrange - (maxrange * Math.abs (r.nextGaussian ()));
      
      debug.printf ("moverewire: consider %s, %s\n", from, to);
      debug.printf ("            range %f (out of %f)\n", range, maxrange);
      
      if (from.getPosition ().distance (to.getPosition ()) > range)
        return null;
      return el.getLabel (from, to);
    }
    public L getLabel (N from, N to) {
      return getEdge (from, to);
    }
  };
  public RandomMove (Graph<N,L> graph, EdgeLabeler<N,L> el, 
                     Dimension bound, float default_speed,
                     float maxrange) {
    super (graph, el);
    this.maxrange = maxrange;
    cl = new CartesianRewire<N,L> (graph, el, bound, maxrange);
    this.bound = bound;
    this.default_speed = default_speed;
    angle_dev = default_angle_dev;
    
    cl.rewire ();
  }
  public RandomMove (Graph<N,L> graph, EdgeLabeler<N,L> el, 
                     Dimension bound, float default_speed,
                     float maxrange, int angle_dev) {
    super (graph, el);
    cl = new CartesianRewire<N,L> (graph, el, bound, maxrange);
    this.bound = bound;
    this.default_speed = default_speed;
    this.angle_dev = angle_dev;
    
    cl.rewire ();
  }
  /* clip node position to be in bounds, return whether it needed clipping */
  private boolean clip (Vector2D pos) {
    double origx = pos.x;
    double origy = pos.y;
    
    pos.x = Math.min (Math.max (-bound.width/2, pos.x), bound.width/2);
    pos.y = Math.min (Math.max (-bound.height/2, pos.y), bound.height/2);
    
    return (pos.x != origx || pos.y != origy);
  }
  
  private void move (N node) {
    Vector2D v = node.getVelocity ();
    Vector2D pos = node.getPosition ();
    
    if (v.x == 0 && v.y == 0) {
      v.setLocation (0, default_speed);
      v.rotate (360 * r.nextDouble ());
    }
    
    pos.plus (v);
    
    if (clip (pos)) {
      v.rotate (Math.toRadians (180 + r.nextGaussian () * angle_dev));
    }
  }
  
  @Override
  public void rewire () {
    for (N node : graph)
      if (node.isMovable ())
        move (node);
    cl.rewire ();
  }
}
