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
import java.util.Random;

import org.nongnu.multigraph.Edge;
import org.nongnu.multigraph.Graph;
import org.nongnu.multigraph.debug;

/**
 * See "Graph Drawing by Force-directed Placement", Fruchterman & Reingold.
 * <p>
 * This algorithm tries to layout a graph as if the nodes are repelled by each 
 * other, exponentially more so as they get closer to each other, while at the 
 * same time the edges act like springs to pull nodes together. These are
 * combined to give each node a velocity and momentum, acting in tension on
 * other nodes. If node's have a mass, this is taken into account.
 * <p>
 * The algorithm takes a number of iterations to reach equilibrium, presuming 
 * there are no other forces acting on the graph.
 * <p>
 * The behaviour of the algorithm is can be tuned by a number of parameters.
 * Unfortunately, different graphs may require different values for certain
 * parameters for best effect. 
 */
public class ForceLayout<N extends PositionableNode, L> extends Layout<N, L> {
  private double k;
  private double mintemp = 0.001;
  private double C = 0.5;
  private double minkve = 0.001;
  private double jiggle = 0.1;

  private double temperature = 1.2;
  private double decay = 0.96;
  
  private void _setk () {
    double min = Math.min (bound.getWidth (), bound.getHeight ());
    k = C * Math.sqrt (min*min/graph.size ());
    debug.println ("k: " + k);
  }
  
  public ForceLayout (Graph<N, L> graph, Dimension bound, int maxiterations) {
    super (graph, bound, maxiterations);
    
    _setk ();
  }
  
  /**
   * Create a ForceLayout instance, with the initial temperature set as given.
   * 
   * @param graph The Graph to act on
   * @param bound The boundary to apply to the layout algorithm
   * @param maxiterations The maximum number of iterations to run for.
   * @param initial_temperature The initial temperature scale factor to apply.
   * @see #setMintemp(double) for further discussion of temperature.
   */
  public ForceLayout (Graph<N, L> graph, Dimension bound, int maxiterations,
                      double initial_temperature) {
    super (graph, bound, maxiterations);
    temperature = initial_temperature;
    _setk ();
  }
  /**
   * Create a ForceLayout instance, with the initial temperature set as given,
   * and using the specific C scalar for the k parameter of the algorithm.
   * <p>
   * C defaults to 1.
   * 
   * @param graph
   * @param bound
   * @param maxiterations
   * @param initial_temperature
   * @param C scalar to apply to the k constant, which is used to calculate
   *          the attractive and repulsive forces.
   */
  public ForceLayout (Graph<N, L> graph, Dimension bound, int maxiterations,
                      double initial_temperature, double C) {
    super (graph, bound, maxiterations);
    temperature = initial_temperature;
    _setk ();
  }
  private double attraction (double delta) {
    return (delta * delta) / k;
  }
  private double repulsion (double delta) {
    return (k * k) / delta;
  }
  
  private double decay (double temperature) {
    return temperature * decay;
  }
  
  /**
   * Set the minimum temperature possible.
   * <p>
   * The velocity calculated for a node on each iteration is scaled by a
   * 'temperature' factor. This temperature decays on each iteration, to
   * simulate the algorithm getting 'colder' - the idea being to allow the
   * algorithm to stabilise.
   * <p>
   *  Generally, you want this parameter set so that in later iterations of
   * the algorithm there is still some amount of energy attainable to allow
   * nodes to move around, while being low enough to damp out any wild movement
   * of nodes.
   * <p>
   * On dense graphs, and/or graphs with very well connected nodes, you will
   * want this value to be lower, to prevent oscillations. On more evenly
   * distributed graphs, this value can be set higher.
   * <p>
   * The default is 0.001
   * @param mintemp The minimum temperature that can apply. Generally this
   *                should be 1 or less. 
   */
  public ForceLayout<N,L> setMintemp (double mintemp) {
    this.mintemp = mintemp;
    return this;
  }

  /**
   * The velocity on each iteration is scaled by a 'temperature' factor, which
   * is decayed according to temperature(t+1) = temperature(t) * decay, or
   * temperature (t) = decay^t.
   * <p>
   * This defaults to 0.94.
   *
   * @param decay The decay factor to apply to the temperature. Generally it
   *               should be <= 1. Higher values lead to a more linear decay.
   *               Lower values to a more exponential and initially rapid
   *               decay.
   * @see setMintemp
   */
  public ForceLayout<N,L> setDecay (double decay) {
    this.decay = decay;
    return this;
  }
  
  /**
   * @param minkve Sets the minimum sum of kinetic energy values, below which
   *               the algorithm will be considered to no longer have any
   *               movement. 
   * <p>
   * Default: 0.1
   */
  public ForceLayout<N,L> setMinkve (double minkve) {
    this.minkve = minkve;
    return this;
  }
  
  /**
   * @param C Sets the C parameter of the algorithm, which is used to scale 
   * the k factor of the algorithm. Increasing this factor will magnify the
   * repulsive and attractive forces, decreasing will minimise them. Values
   * around 1 will try balance nodes around the area. Values above will
   * tend to force many nodes against the boundary. Values below 1 will
   * tend to cause nodes to cluster more toward centre of the area.
   * <p>
   * Default: 1
   */
  public ForceLayout<N,L> setC (double C) {
    this.C = C;
    _setk ();
    return this;
  }
  
  /**
   * @param jiggle Adds some entropy to the algorithm. Higher values
   *               add more jiggle. Recommended value is between
   *               0 and 1. Defaults to 0.1. 
   *               
   *               Note that increasing this value will increase the
   *               background 'heat' of the algorithm, and you may need
   *               increase minkve if you're depending on it rather
   *               than maxiterations.
   * 
   */
  public void setJiggle (double jiggle) {
    this.jiggle = jiggle;
  }
  
  public boolean layout (float interval) {
    double sumkve = 0;
    double maxkve = 0;
    Random r = new Random ();
    
    debug.println ("force-layout start");
    
    if (!super.layout (interval))
      return false;
    
    for (N n : graph) {
      Vector2D disp = n.getVelocity ();
      disp.setLocation (0, 0);
      
      debug.printf ("node: %s, pos: %s\n", n, n.getPosition ());
      
      for (N other : graph) {
        if (other == n)
          continue;
        
        /* if the other is too close, bump it away in a random direction,
         * to minimise huge repulsion effects of overlapping nodes
         */
        if (other.getPosition ().distanceSq (n.getPosition ()) <= 1) {
          Vector2D vrandom = new Vector2D (2, 0);
          vrandom.rotate (r.nextInt (360));
          other.getPosition ().plus (vrandom);
        }
        
        debug.printf ("\trepulsion with %s, %s\n",
                       other, other.getPosition ());
        
        Vector2D delta = new Vector2D (n.getPosition ());
        delta.minus (other.getPosition ());
        
        debug.println ("\t\tdelta1: " + delta);
        
        double repf = repulsion (delta.length ());
        
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
        
        debug.printf ("\tattraction with %s\n", e.to ());
        
        Vector2D delta = new Vector2D (n.getPosition ());
        delta.minus (e.to ().getPosition ());
        
        debug.println ("\t\tdelta1: " + delta + ", len " + delta.length ());
        
        double attrf = attraction (delta.length ());
        /* add a little entropy, to try avoid perfectly balancing
         * forces on a node keeping it stable in a local optimum, when
         * ideally we'd like things to jiggle a little so it can maybe 'pop'
         * out and move to the next optimum, hopefully.
         */
        attrf *= Math.max (0, r.nextGaussian ())*jiggle + 1;
        
        debug.println ("\t\tattrf: " + attrf);
        
        delta.normalise ();
        delta.times (attrf);
        
        debug.println ("\t\tdelta2: " + delta);
        
        disp.minus (delta);
        debug.println ("\tdisp: " + disp);
      }
      
      /* Apply a little bit of entropy to the direction of the 
       * resultant velocity, see above. 
       * 
       * The std.dev is 1, so /3 keeps the value between -1 and 1 for
       * ~99% of results. Then multiply by range of +-/ degrees you want.
       */
      disp.rotate (Math.toRadians (r.nextGaussian ()/3 * 10 * jiggle));
      debug.println ("\tresultant v: " + disp);
      
      Vector2D pos = n.getPosition ();
      Vector2D v = n.getVelocity ();
      
      debug.printf ("node pos: %s\n", pos);
      debug.printf ("\tv: %s\n", v);
      
      temperature = Math.max (decay (temperature), mintemp);
      v.times (interval * temperature);
      
      debug.printf ("\tv2: %s\n", v);
      
      pos.plus (v);
      
      debug.printf ("\tp2: %s\n", pos);
      
      /* XXX: Lose lots of energy by dumbly clipping at the boundary, leads
       * to nodes happily clumping together at the walls, particularly with
       * forests of disconnected graphs.
       * 
       * TODO: Implement reflection.
       */
      pos.x = Math.min (Math.max (-bound.width/2, pos.x), bound.width/2);
      pos.y = Math.min (Math.max (-bound.height/2, pos.y), bound.height/2);
      
      double mag = v.magnitude ();    
      double kve = n.getMass () * mag * mag;
      sumkve += kve;
      maxkve = Math.max (maxkve, kve);
      
      debug.println ("\tresult: " + pos);
    }
    
    debug.println ("kve:    " + sumkve);
    debug.println ("maxkve: " + maxkve);
    
    return sumkve > minkve;
  }
}
