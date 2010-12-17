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

/**
 * Interface that is required to be implemented by nodes of a graph, if
 * any Layout algorithm is to be able to act on them.
 * @author paul
 *
 */
public interface PositionableNode {
  Vector2D getPosition ();
  void setPosition (Vector2D pos);
  Vector2D getVelocity ();
  
  float getSize ();
  void setSize (float s);
  
  float getMass ();
  void setMass (float m);
  
  boolean isMovable (); 
}
