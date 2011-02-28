package org.nongnu.multigraph;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Output an adjacency matrix of a graph, in a form suitable for MatLab/Octave.
 * @author paul
 *
 */
public class AdjacencyMatrix {
  public static <N,E> void full (PrintStream out, Graph<N,E> graph) {
    @SuppressWarnings ("unchecked")
    N [] nodes = (N []) new Object[graph.size ()];
    graph.toArray (nodes);
    
    for (int i = 0; i < nodes.length; i++) {
      out.print (i == 0 ? "[ " : "  ");
      for (int j = 0; j < nodes.length; j++) {
        if (i == j)
          out.print ('0');
        else
          out.print (graph.edge (nodes[i], nodes[j]) != null ? '1' : '0');
        out.print (' ');
      }
      out.print (i != nodes.length - 1 ? "\n" : "]\n");
    }
  }
  
  public static <N,E> void sparse (PrintStream out, Graph<N,E> graph) {
    @SuppressWarnings ("unchecked")
    N [] nodes = (N []) new Object[graph.size ()];
    graph.toArray (nodes);
    
    out.print ("[\n");
    for (int i = 0; i < nodes.length; i++) {
      for (int j = 0; j < nodes.length; j++) {
        if (graph.edge (nodes[i], nodes[j]) != null)
          out.print (" " + (i + 1) + " " + (j+1) + " 1\n");
      }
    }
    out.print ("]\n");
  }
  
  public static <N,E> void parse (InputStream in, Graph<N,E> graph,
                                  NodeLabeler<N,E> nl, EdgeLabeler<N,E> el) {
    Scanner sc = new Scanner (in);
    Pattern startp = Pattern.compile ("\\[\\s*");
    
    while ((null == sc.findInLine (startp))) sc.nextLine ();
    sc.nextLine ();
    
    String t;
    while (!(t = sc.next ()).equals ("]")) {
      N from = nl.getNode (t);
      N to   = nl.getNode (sc.next ());
      int w = Integer.parseInt (sc.next ());
      
      debug.printf ("setting: %s -> %s (%d)\n", from, to, w);
      
      try {
        graph.set (from, to, el.getLabel (from, to), w);
      } catch (UnsupportedOperationException e) {
        debug.printf ("Couldn't not create edge %s -> %s (%d):\n%s\n",
                     from, to, w, e);
      }
    }
    sc.close ();
  }
}
