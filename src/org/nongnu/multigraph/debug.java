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

import java.io.PrintStream;
import java.util.Formatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.MemoryHandler;

/**
 * Custom debug class with support for various print statements, different debug levels
 * and a ring-buffer to store debug message history that is only printed out if
 * a message of at least a certain level (e.g. an error) is logged.
 * 
 * This is intended as a convenience wrapper around java.util.logging.
 * 
 * @author paul.
 */
public class debug {
  public enum levels {
    NONE(Level.OFF),
    ERROR(Level.SEVERE),
    WARNING(Level.WARNING),
    INFO(Level.INFO),
    DEBUG(Level.FINEST);
    
    private final Level level;
    
    private levels (Level l) { level = l; }
    
    Level toLevel () { return level; }
    
    static levels tolevel (Level l) {
      for (levels ls : levels.values ())
        if (ls.level == l)
          return ls;
      return levels.NONE;
    }
  }
  
  public static PrintStream out = System.out;
  private static levels level = levels.WARNING;
  private static java.util.logging.Formatter fmter = new java.util.logging.Formatter () {
    @Override
    public String format (LogRecord record) {
      return record.getLevel () + ": " + record.getMessage ();
    }
  };
  
  private static ConsoleHandler ch = new ConsoleHandler ();
  private static MemoryHandler mh
    = new MemoryHandler (ch, 4096000, level.level);
  public static Logger logger = Logger.getAnonymousLogger ();
  private static boolean once = true;
  
  /**
   * The current debug level. Messages below this level will not be captured.
   * @see #level.
   * @return Current debug.level.
   */
  public static levels level () {
    return level;
  }
  /**
   * Set the current debug level. Messages below this level will not be captured.
   * @see #level.
   * @param l The debug.level to set.
   */
  public static void level (levels l) {
    level = l;
    mh.setPushLevel (l.level);
  }
  /**
   * Set the current debug level. Messages below this level will not be captured.
   * @see #level.
   * @param l String representation of debug.level to set.
   */
  public static void level (String l) {
    level(levels.valueOf (l.toUpperCase ()));
  }
  /**
   * The current push-level. Debug messages that have been captured are buffered
   * in a ring-buffer in memory and not pushed out until a message of at least 
   * this level is seen. This defaults to the current debug level, and is reset
   * accordingly whenever the debug level is reset - the push-level must be
   * set after the debug level if a different level is desired. @see #level(levels).
   * <p>
   * E.g. Setting the debug-level to "debug", but the push-level to "error" would
   * print out the last X lines of "debug" output, but only if a "error" level message
   * was received.
   * 
   * @return
   */
  public static levels pushlevel () {
    return levels.tolevel (mh.getPushLevel ());
  }
  /**
   * Set the current push-level. @see #pushlevel and @see #level.
   * @param l the {@link #level} to set the push-level to.
   */
  public static void pushlevel (levels l) {
    mh.setPushLevel (l.level);
  }
  /**
   * Set the current push-level. @see #pushlevel and @see #level.
   * @param l String form of the {@link #level} to set the push-level to.
   */
  public static void pushlevel (String l) {
    pushlevel (levels.valueOf (l.toUpperCase ()));
  }
  
  /**
   * Change the size of the ring-buffer used to buffer log records
   * when the push-level is higher than the debug level. This
   * defaults to 4096000 (i.e. quite a lot). Setting this parameter
   * destroys any existing log messages.
   * @param s
   */
  public static void buffersize (int s) {
    MemoryHandler newmh = new MemoryHandler (ch, s, mh.getPushLevel ());
    newmh.setLevel (mh.getLevel ());
    mh = newmh;
  }
  
  public static boolean applies (levels d) {
    return (level == levels.NONE || d.ordinal () > level.ordinal ())
      ? false
      : true;
  }
  
  public static boolean applies () {
	  return applies (levels.DEBUG);
  }
  
  public static void printf (levels d, String s) {
    if (!applies (d))
      return;
    
    if (once) {
      ch.setFormatter (fmter);
      logger.setLevel (levels.DEBUG.level);
      ch.setLevel (debug.levels.DEBUG.level);
      logger.addHandler (mh);
      once = false;
    }
    
    logger.log (d.level, s);
  }
  
  public static void printf (levels d, String format, Object... args) {
    if (!applies (d))
      return;
    
    StringBuilder sb;
    
    new Formatter ((sb = new StringBuilder ())).format (format, args);
    
    printf (d, sb.toString ());
  }

  public static void println (levels d, String s) {
    if (!applies (d))
      return;
    
    printf (d, s + "\n");
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
