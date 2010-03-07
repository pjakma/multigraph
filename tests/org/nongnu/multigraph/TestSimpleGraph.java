/* This file is part of 'MultiGraph'
 *
 * Copyright (C) 2010 Paul Jakma
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
package org.nongnu.multigraph;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestSimpleGraph {
  Graph<String,String> g = new MultiDiGraph<String,String> ();
  int start_size;
  
  @BeforeClass
  public static void setUpBeforeClass () throws Exception {}
  
  @Before
  public void setUp () throws Exception {
    /* setup a chain of weakly connected */
    for (int i = 2; i < 10; i++) {
      String n1 = ("Node" + (i - 1)).intern ();
      String n2 = ("Node" + i).intern ();
      String l = ("Edge" + (i - 1) + "-" + i).intern ();
      g.set (n1, n2, l);
      
    }
    assertTrue (g.size () == 9);
    
    /* some strongly connected */
    for (int i = 10; i < 20; i++) {
      String n1 = ("Node" + (i - 1)).intern ();
      String n2 = ("Node" + i).intern ();
      String l = ("Edge" + (i - 1) + "-" + i).intern ();
      g.set (n1, n2, l);
      g.set (n2, n1, l); 
    }
    
    /* some multiply connected */
    for (int i = 20; i < 30; i++) {
      String n1 = ("Node" + (i - 1)).intern ();
      String n2 = ("Node" + i).intern ();
      String l = ("Edge" + (i - 1) + "-" + i).intern ();
      String l2 = (l + "-2").intern ();
      g.set (n1, n2, l);
      g.set (n1, n2, l2);
      g.set (n2, n1, l); 
    }
    
    start_size = g.size ();
    System.out.println ("graph:\n" + g);
  }
  
  private void testRemoveNNL1 (String n1, String n2, String l, boolean expect) {
    System.out.printf ("RemoveNNL %s, %s, %s %s\n", n1, n2, l, expect);
    int s1 = g.size ();
    
    int [] outdegrees = new int [] {
      g.edge_outdegree (n1),
      g.edge_outdegree (n2),
    };
    
    assertTrue (g.remove (n1, n2, l) == expect);
    assertTrue (g.contains (n1));
    assertTrue (g.contains (n2));
    assertTrue (g.edge_outdegree (n2) == outdegrees[1]);
    assertTrue (g.size () == s1);
    assertTrue (g.edges (n1).contains (l) == false);
    
    if (expect) {
      assertTrue (g.edge_outdegree (n1) == outdegrees[0] - 1);
    } else {
      assertTrue (g.edge_outdegree (n1) == outdegrees[0]); 
    }
  }
  @Test
  public void testRemoveNNL () {
    /* weakly connected */
    System.out.println ("weak");
    testRemoveNNL1 ("Node1".intern (), "Node2".intern (),
                    "Edge1-2".intern (), true);
    testRemoveNNL1 ("Node1".intern (), "Node2".intern (),
                    "Edge1-2".intern (), false);
    testRemoveNNL1 ("Node1".intern (), "Node2".intern (),
                    "Edge2-3".intern (), false);
    
    /* strongly connected */
    System.out.println ("strong");
    testRemoveNNL1 ("Node12".intern (), "Node13".intern (),
                    "Edge12-13".intern (), true);
    testRemoveNNL1 ("Node13".intern (), "Node12".intern (),
                    "Edge12-13".intern (), true);
    testRemoveNNL1 ("Node12".intern (), "Node13".intern (),
                    "Edge12-13".intern (), false);
    testRemoveNNL1 ("Node13".intern (), "Node12".intern (),
                    "Edge12-13".intern (), false);
    
    /* multi connected */
    System.out.println ("multi");
    testRemoveNNL1 ("Node22".intern (), "Node23".intern (),
                    "Edge22-23".intern (), true);
    testRemoveNNL1 ("Node23".intern (), "Node22".intern (),
                    "Edge22-23".intern (), true);
    testRemoveNNL1 ("Node22".intern (), "Node23".intern (),
                    "Edge22-23-2".intern (), true);
    testRemoveNNL1 ("Node22".intern (), "Node23".intern (),
                    "Edge22-23".intern (), false);
    testRemoveNNL1 ("Node22".intern (), "Node23".intern (),
                    "Edge22-23".intern (), false);
    testRemoveNNL1 ("Node23".intern (), "Node22".intern (),
                    "Edge22-23".intern (), false);
  }
  
//  @Test
//  public void testRemoveNN () {
//    fail ("Not yet implemented");
//  }
//  
//  @Test
//  public void testEdgesN () {
//    fail ("Not yet implemented");
//  }
//  
//  @Test
//  public void testEdgesNN () {
//    fail ("Not yet implemented");
//  }
//  
//  @Test
//  public void testEdge () {
//    fail ("Not yet implemented");
//  }
//  
//  @Test
//  public void testSuccessors () {
//    fail ("Not yet implemented");
//  }
//  
//  @Test
//  public void testEdge_outdegree () {
//    fail ("Not yet implemented");
//  }
//  
//  @Test
//  public void testNodal_outdegree () {
//    fail ("Not yet implemented");
//  }
//  
//  @Test
//  public void testRemoveObject () {
//    fail ("Not yet implemented");
//  }
  
}
