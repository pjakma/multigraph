package org.nongnu.multigraph;

import java.io.PrintStream;

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
}
