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
package org.nongnu.multigraph;

import java.util.*;

/**
 * Extension to Observable to coalesce updates, by allowing the Observable
 * to be 'plugged' and notificatons halted, until the Observable is again
 * 'unplugged'.
 */
public class PluggableObservable extends Observable {
  private boolean plugObservable = false;
  private boolean notifyNull = false;
  private Set<Object> notifyObjs = new HashSet<Object> ();
  
  public synchronized void plugObservable () {
    plugObservable = true;
  }
  
  public synchronized void unplugObservable () {
    if (!plugObservable)
      return;
    
    plugObservable = false;
    
    if (notifyNull) {
      setChanged ();
      super.notifyObservers ();
    }
    
    for (Object o : notifyObjs) {
      /* setChanged, or else notifyObservers will act only on first notify */
      setChanged ();
      super.notifyObservers (o);
    }
    
    notifyObjs.clear ();
    notifyNull = false;
  }
  
  @Override
  public synchronized void notifyObservers () {
    setChanged ();
    if (!plugObservable) {
      super.notifyObservers (null);
      return;
    }
    /* plugged */
    notifyNull = true;
  }

  @Override
  public synchronized void notifyObservers (Object arg) {
    setChanged ();
    if (!plugObservable) {
      super.notifyObservers (arg);
      return;
    }
    
    /* plugged */
    if (arg == null)
      notifyNull = true;
    else
      notifyObjs.add (arg);
  }
}
