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

import java.awt.geom.Point2D;


public class Vector2D extends Point2D.Double {
  
  public void normalise () {
    double dist = distance (0, 0);
    x /= dist;
    y /= dist;
  }
  
  public Vector2D () {
    super ();
  }
  public Vector2D (double arg0, double arg1) {
    super (arg0, arg1);
  }
  public Vector2D (Point2D p) {
    super (p.getX (), p.getY ());
  }
  
  public double magnitude () {
    double d = distance (0,0);
    return d;
  }
  public Vector2D normalise (Vector2D v) {
    v.x = x;
    v.y = y;
    v.normalise ();
    return this;
  }
  public double length () {
    return distance (0, 0);
  }
  
  public Vector2D plus (Point2D v) {
    x += v.getX ();
    y += v.getY ();
    return this;
  }
  public Vector2D plus (double x, double y) {
    this.x += x;
    this.y += y;
    return this;
  }
  public Vector2D minus (Point2D v) {
    x -= v.getX ();
    y -= v.getY ();
    return this;
  }
  public Vector2D minus (double x, double y) {
    this.x -= x;
    this.y -= y;
    return this;
  }
  public Vector2D times (double n) {
    x *= n;
    y *= n;
    return this;
  }
  
  public Vector2D rotate (double n) {
    double rx,ry;
    rx = (Math.cos (n)*x) - (Math.sin (n)*y);
    ry = (Math.sin (n)*x) + (Math.cos (n)*y);
    
    x = rx;
    y = ry;
    
    return this;
  }
}
