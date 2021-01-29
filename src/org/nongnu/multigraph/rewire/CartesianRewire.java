package org.nongnu.multigraph.rewire;

import java.awt.Dimension;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.nongnu.multigraph.EdgeLabeler;
import org.nongnu.multigraph.Graph;
import org.nongnu.multigraph.debug;
import org.nongnu.multigraph.layout.PositionableNode;

/**
 * Wire up nodes in the graph with each other according to their cartesian
 * distance from each other, applying a 'range' constraint. The user can apply
 * further constraints using the EdgeLabeler.
 * <p>
 * This rewiring algorithm only considers edges once, at this time. If the 
 * graph is directed, and the user wishes both directions to be set, they must
 * do so themselves in their EdgeLabeler.
 * <p>
 * @author Paul Jakma
 * @param <N> The type of the Nodes in the graph
 * @param <E> The type of the Edges in the graph
 */
public class CartesianRewire<N extends PositionableNode, E>
       extends Rewire<N, E> {
  private float range = 10;
  private LinkedList<N> [][] gridindex = null;
  /* number of divisions to make for the grid, on the shortest side of the
   * boundary */
  private final int divs = 10;
  /* translate from the centre-0,0 co-ords to top-left co-ords */
  private final int shiftx;
  private final int shifty;
  /* the length of a division */
  private final float divlen;
  /* the extent of the node range, in divisions */
  private final int rangediv;
  
  /**
   * Create a new CartesianRewire instance, for the given graph, wiring up
   * nodes that are within the given distance. Note that the EdgeLabeler
   * callback may apply its own, further constraints, by returning a null label.
   * This instance of the algorithm uses the supplied boundary to create a grid
   * index to speed things. Certain extreme cases may be slower with grid-indexing
   * e.g. where nodes are extremely tightly bunched, positionally.
   *  
   * @param graph The graph to rewire.
   * @param el The EdgeLaber to callback to create labels.
   * @param bound The positional boundary for nodes, used for grid-indexing.
   * @param range The maximum range for links between nodes. 
   */
  @SuppressWarnings ({"rawtypes","unchecked"})
  public CartesianRewire (Graph<N, E> graph, EdgeLabeler<N, E> el,
                          Dimension bound, float range) {
    super (graph, el);
    this.range = range;
    
    if (bound != null && range > 0) {
      shiftx = bound.width/2;
      shifty = bound.height/2;
      divlen = Math.min (bound.width, bound.height) / divs;
      rangediv = (int) Math.ceil (range/divlen);
      gridindex = new LinkedList [(int) Math.ceil (bound.getWidth ()/divlen)]
                                 [(int) Math.ceil (bound.getHeight ()/divlen)];
    } else {
      gridindex = new LinkedList [1][1];
      shiftx = shifty = rangediv = 0;
      divlen = 0;
    }
  }
  /**
   * Create a new CartesianRewire instance, for the given graph, wiring up
   * nodes that are within the given distance. Note that the EdgeLabeler
   * callback may apply its own, further constraints, by returning a null label.
   *  
   * @param graph The graph to rewire.
   * @param el The EdgeLaber to callback to create labels.
   * @param range The maximum range for links between nodes. 
   */
  @SuppressWarnings ({"rawtypes","unchecked"})
  public CartesianRewire (Graph<N, E> graph, EdgeLabeler<N, E> el,
                          float range) {
    super (graph, el);
    this.range = range;
    shiftx = shifty = rangediv = 0;
    divlen = 0;
    gridindex = new LinkedList [1][1];
  }

  @SuppressWarnings ({"rawtypes","unchecked"})
  public CartesianRewire (Graph<N, E> graph, EdgeLabeler<N, E> el) {
    super (graph, el);
    this.range = range;
    shiftx = shifty = rangediv = 0;
    divlen = 0;
    gridindex = new LinkedList [1][1];
  }
  
  private int _gridcalc (double pos, int shift, int alen) {
    return Math.min (alen - 1, (int) Math.floor ((shift + pos) / divlen));
  }
  private int togridx (N node) {
    if (gridindex.length > 1)
      return _gridcalc (node.getPosition ().x, shiftx, gridindex.length);
    return 0;
  }
  private int togridy (N node) {
    if (gridindex[0].length > 1)
      return _gridcalc (node.getPosition ().y, shifty, gridindex[0].length);
    return 0;
  }
  
  private void make_grid_index () {
    for (int i = 0; i < gridindex.length; i++)
      for (int j = 0; j < gridindex[0].length; j++)
        if (gridindex[i][j] != null)
          gridindex[i][j].clear ();
    
    for (N node : graph) {
      int x = togridx (node);
      int y = togridy (node);
      
      if (gridindex[x][y] == null)
        gridindex[x][y] = new LinkedList<N> ();
      
      gridindex[x][y].add (node);
    }
  }
  
  private void rewire_grid () {
    for (int i = 0; i < gridindex.length; i++)
      for (int j = 0; j < gridindex[0].length; j++)
        if (gridindex[i][j] != null)
          rewire_grid (i, j);
  }
  private void rewire_grid (int x, int y) {
    Set<N> targets = new HashSet<N> ();
    
    if (gridindex[x][y] == null || gridindex[x][y].size () == 0)
      return;
    
    /* scan the grid surrounding x,y, extending ceil(range/divlen) squares,
     * for targets. this surely can be refined further 
     */
    for (int i = Math.max (x - rangediv,0);
         i <= x + rangediv && i < gridindex.length;
         i++)
        for (int j = Math.max (y - rangediv,0);
             j <= y + rangediv && j < gridindex[i].length;
             j++)
          if (gridindex[i][j] != null)
            targets.addAll (gridindex[i][j]);
    
    for (N node : gridindex[x][y])
      targets.addAll (graph.successors (node));
    
    if (targets.size () > 1)
      rewire (gridindex[x][y], targets);
  }
  
  /* targets should be the union of all nodes potentially within range of
   * those in check, unioned with all nodes currently with edges from nodes
   * in check 
   */
  private void rewire (LinkedList<N> check, Set<N> targets) {
    N n1;
    while ((n1 = check.poll ()) != null)
      for (N n2 : targets) {
        E label;
        
        /* this lets it work efficiently when targets contains some of the
         * same nodes as check,
         * e.g. when the grid is [1][1], or there is no grid */
        if (n2 == n1)
          continue;
        //  targets.remove ();
        //  continue;
        //}
        
        double dist = n1.getPosition ().distance (n2.getPosition ());
        
        debug.printf ("Cartesian: %s -> %s = %f\n", n1, n2, dist);
        
        if (dist <= range && (label = el.getLabel (n1, n2)) != null) {
          if (!graph.successors (n1).contains (n2))
            graph.set (n1, n2, label);
        } else
          graph.remove (n1, n2);
      }
  }
  
  @Override
  public void rewire () {
    if (graph.size () < 2)
      return;
    if (range <= 0)
      return;
    
    make_grid_index ();
    
    if (graph.size () < 2)
      return;
    
    rewire_grid ();
  }
}
