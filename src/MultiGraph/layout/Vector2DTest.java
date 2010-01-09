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

import static org.junit.Assert.*;

import org.junit.Test;

public class Vector2DTest {
  private double accuracy = 0.1;
  
  @Test
  public void testNormalise () {
    Vector2D v1 = new Vector2D (3,4);
    Vector2D v2 = new Vector2D (v1);
    v1.normalise ();
    
    assertEquals (v1.times (5).distance (0,0),
                  v2.distance (0, 0), accuracy);
  }
  
  @Test
  public void testPlusPoint2D () {
    Vector2D v1 = new Vector2D (1,2);
    Vector2D v2 = new Vector2D (2,3);
    
    v1.plus (v2);
    
    assertEquals (3, v1.x, accuracy);
    assertEquals (5, v1.y, accuracy);
  }
  
  @Test
  public void testPlusDoubleDouble () {
    Vector2D v = new Vector2D (1,2);
    
    v.plus (2,3);
    
    assertEquals (3, v.x, accuracy);
    assertEquals (5, v.y, accuracy);
  }
  
  @Test
  public void testMinusPoint2D () {
    Vector2D v1 = new Vector2D (1,2);
    Vector2D v2 = new Vector2D (2,3);
    
    v1.minus (v2);
    
    assertEquals (-1, v1.x, accuracy);
    assertEquals (-1, v1.y, accuracy);
  }

  @Test
  public void testMinusPoint2D2 () {
    Vector2D v1 = new Vector2D (1,2);
    Vector2D v2 = new Vector2D (2,3);
    
    v2.minus (v1);
    
    assertEquals (1, v2.x, accuracy);
    assertEquals (1, v2.y, accuracy);  
  }
  @Test
  public void testMinusDoubleDouble () {
    Vector2D v = new Vector2D (1,2);
    
    v.minus (2,3);
    
    assertEquals (-1, v.x, accuracy);
    assertEquals (-1, v.y, accuracy);
  }
  
  @Test
  public void testTimes () {
    Vector2D v = new Vector2D (3,2);
    v.times (3);
    
    assertEquals (9, v.x, accuracy);
    assertEquals (6, v.y, accuracy);
  }
  
  @Test
  public void testRotate () {
    Vector2D v = new Vector2D (1,0);
    
    v.rotate (Math.toRadians (90));
    assertEquals (0, v.x, accuracy);
    assertEquals (1, v.y, accuracy);
    
    v.rotate (Math.toRadians (90));
    assertEquals (-1, v.x, accuracy);
    assertEquals (0, v.y, accuracy);
    
    v.rotate (Math.toRadians (90));
    assertEquals (0, v.x, accuracy);
    assertEquals (-1, v.y, accuracy);
    
    v.rotate (Math.toRadians (90));
    assertEquals (1, v.x, accuracy);
    assertEquals (0, v.y, accuracy);
    
    v = new Vector2D (2,0);
    
    v.rotate (Math.toRadians (90));
    assertEquals (0, v.x, accuracy);
    assertEquals (2, v.y, accuracy);
    
    v.rotate (Math.toRadians (90));
    assertEquals (-2, v.x, accuracy);
    assertEquals (0, v.y, accuracy);
    
    v.rotate (Math.toRadians (90));
    assertEquals (0, v.x, accuracy);
    assertEquals (-2, v.y, accuracy);
    
    v.rotate (Math.toRadians (90));
    assertEquals (2, v.x, accuracy);
    assertEquals (0, v.y, accuracy);
  }
}
