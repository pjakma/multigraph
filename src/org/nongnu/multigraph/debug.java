/* 
 * Copyright (C) 2009 Paul Jakma
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

import java.io.*;

public class debug {
  // debug level, from 0 (none) on up.
  public static PrintStream out = System.out;
  public static levels level = levels.NONE;
  public enum levels {
    NONE,
    ERROR,
    WARNING,
    INFO,
    DEBUG,
  }
  
  public static boolean applies (levels d) {
    return (level == levels.NONE || d.ordinal () > level.ordinal ())
      ? false
      : true;
  }
  public static boolean applies () {
	  return applies (levels.DEBUG);
  }
  
  public static void printf (levels d, String format, Object... args) {
    if (!applies (d))
      return;
    out.printf (format, args);
  }
  public static void printf (levels d, String s) {
    if (!applies (d))
      return;
    out.printf (s);
  }

  public static void println (levels d, String s) {
    if (!applies (d))
      return;
      
    out.println (s);
  }

  // default debug level versions
  public static void printf (String format, Object... args) {
    printf (levels.DEBUG, format, args);
  }
  public static void printf (String s) {
    printf (levels.DEBUG, s);
  }

  public static void println (String s) {
    println (levels.DEBUG, s);
  }


}
