package org.nongnu.multigraph.structure;

public class kshell_node_data {
  public int k;
  public boolean removed;
  
  public kshell_node_data () {
    reset ();
  }
  
  public void reset () {
    k = 0;
    removed = false;
  }
}