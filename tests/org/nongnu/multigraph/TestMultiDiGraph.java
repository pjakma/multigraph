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
import org.nongnu.multigraph.Graph;

import static org.junit.Assert.*;

public class TestMultiDiGraph {
  Graph<String,String> g = new MultiDiGraph<String,String> ();
  int start_size;
  
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
    
    /* some multi connected */
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
    //System.out.println ("graph:\n" + g);
  }
  
  private void check_invariants () {
    int nodal_outdegree = 0;
    int edge_outdegree = 0;
    int max_nodal_degree = 0;
    int num = 0;

    System.out.println ("g: " + g);
    System.out.println ("Check invariants");
    for (String s : g) {
      int nd = g.nodal_outdegree (s);
      int ed = g.edge_outdegree (s);
      num++;

      if (g.successors (s) != null
          || g.edges (s) != null)
        assertTrue ("If any edge is valid, all must be",
                    g.successors (s) != null
                    && g.edges (s) != null);
      else
        assertTrue (s + ": nd " + nd
                    + ", successors: " + g.successors (s).size ()
                    + ", edges: " + g.edges (s).size (),
                    g.successors (s).size () == nd);

      nodal_outdegree += nd;
      if (nd > max_nodal_degree)
        max_nodal_degree = nd;
      
      assertTrue ("edge degree: " + g.edges (s).size (),
                  g.edges (s).size () == g.edge_outdegree (s));
      edge_outdegree += g.edge_outdegree (s);
    }
    
    assertTrue ("num " + num + ", == size: " + g.size (),
                num == g.size ());
    //assertTrue (((float)nodal_outdegree / num) == g.avg_nodal_degree ());
    assertTrue (max_nodal_degree == g.max_nodal_degree ());
  }
  private void testRemoveNNL1 (String n1, String n2, String l, boolean expect) {
    System.out.printf ("RemoveNNL %s, %s, %s %s\n", n1, n2, l, expect);
    int s1 = g.size ();
    Edge<String,String> e;
    
    int [] outdegrees = new int [] {
      g.edge_outdegree (n1),
      g.edge_outdegree (n2),
    };
    
    /* prior */
    assertTrue ("Contains before",
                ((e = g.edge (n1, n2, l)) != null) == expect);

    assertTrue ("Remove " + l,
                g.remove (n1, n2, l) == expect);
    /* post */
    assertTrue ("Still contains " + n1,
                g.contains (n1));
    assertTrue ("Still contains " + n2,
                g.contains (n2));
    assertTrue ("outdegree still same",
                g.edge_outdegree (n2) == outdegrees[1]);
    assertTrue ("graph size still same", g.size () == s1);
    assertTrue ("edge should be removed",
                g.edges (n1) != null
                  ? g.edges (n1).contains (e) == false
                  : true);
    assertTrue ("The label should be gone",
                g.edge (n1, n2, l) == null);
    
    if (expect) {
      assertTrue (g.edge_outdegree (n1) == outdegrees[0] - 1);
    } else {
      assertTrue (g.edge_outdegree (n1) == outdegrees[0]); 
    }
    check_invariants ();
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

  @Test
  public void testClear () {
    System.out.println ("test clear");
    g.clear ();
    check_invariants ();

    assertTrue (g.size () == 0);
    assertTrue (g.isEmpty ());
    assertTrue (g.successors ("Node21".intern ()) == null);
    assertTrue (g.avg_nodal_degree () == 0);
    assertTrue (g.edges ("Node3") == null);
    assertTrue (g.max_nodal_degree () == 0);
    assertTrue ("not cleared, graph: " + g,
                g.remove ("Node22") == false);
  }

  @Test
  public void test_clear_edges () {
    g.clear_all_edges ();
    check_invariants ();

    assertTrue (g.avg_nodal_degree () == 0);
    assertTrue (g.max_nodal_degree () == 0);
    
    for (String n : g) {
      assertTrue (g.successors (n).isEmpty ());
      assertTrue (g.edge_outdegree (n) == 0);
      assertTrue (g.edges (n).isEmpty ());
      assertTrue (g.edge_outdegree (n) == 0);
      assertTrue (g.nodal_outdegree (n) == 0);
    }
  }
  @Test
  public void testRemoveObject () {
    String n1 = "Node21".intern ();
    String n2 = "Node22".intern ();
    String n3 = "Node23".intern ();
    
    assertTrue (g.remove (n2));
    check_invariants ();

    assertTrue ("Node should be gone", g.contains (n2) == false);
    assertTrue ("In links should be gone, n1",
                (g.edge (n1, n2) == null) == true);
    assertTrue ("In links should be gone, n3",
                (g.edge (n3, n2) == null) == true);
  }
  
}
