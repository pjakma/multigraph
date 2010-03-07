package org.nongnu.multigraph.layout;

import java.awt.Dimension;

import org.nongnu.multigraph.Graph;

public class NullLayout<N extends PositionableNode,L> extends Layout<N, L> {

  public NullLayout (Graph<N, L> graph, Dimension bound,
                     int maxiterations) {
    super (graph, bound, 1);
  }
  
}
