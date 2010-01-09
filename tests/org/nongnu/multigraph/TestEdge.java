/* This file is part of 'MultiGraph'
 *
 * Copyright (C) 2009 Aidan Delaney
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

import org.junit.* ;
import static org.junit.Assert.* ;

public class TestEdge {
    @Test public void testNullCreate() {
	Edge<String, String> e
	    = new Edge<String, String> (null, null, 1, null);
	assertTrue(null == e.from);
	assertTrue(null == e.to);
	assertTrue(null == e.label);
    }

    @Test(expected=AssertionError.class)
    public void testAssertFailCreate () {
	Edge<String, String> e
	    = new Edge<String, String> (null, null, 1, null);
	fail();
    }
}
