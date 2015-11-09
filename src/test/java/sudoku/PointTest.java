/*******************************************************************************
 * Copyright (c) 2014 Sven Erik Knop.
 * Licensed under the EUPL V.1.1
 *
 * This Software is provided to You under the terms of the European 
 * Union Public License (the "EUPL") version 1.1 as published by the 
 * European Union. Any use of this Software, other than as authorized 
 * under this License is strictly prohibited (to the extent such use 
 * is covered by a right of the copyright holder of this Software).
 *
 * This Software is provided under the License on an "AS IS" basis and 
 * without warranties of any kind concerning the Software, including 
 * without limitation merchantability, fitness for a particular purpose, 
 * absence of defects or errors, accuracy, and non-infringement of 
 * intellectual property rights other than copyright. This disclaimer 
 * of warranty is an essential part of the License and a condition for 
 * the grant of any rights to this Software.
 *
 * For more details, see http://joinup.ec.europa.eu/software/page/eupl.
 *
 * Contributors:
 *     2014 - Sven Erik Knop - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package sudoku;

import static org.junit.Assert.*;

import org.junit.Test;

import sudoku.Point;

/**
 * @author Sven Erik Knop
 *
 */
public class PointTest
{

	/**
	 * Test method for {@link sudoku.Point#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		Point p1 = new Point(2,1);
		Point p2 = new Point(2,1);
		Point p3 = new Point(1,2);
		
		assertTrue("Point p1 and p2 are not the same", p1.hashCode() == p2.hashCode());
		assertFalse("Point p2 and p3 are the same", p2.hashCode() == p3.hashCode());
	}

	/**
	 * Test method for {@link sudoku.Point#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		Point p1 = new Point(2,1);
		Point p2 = new Point(2,1);
		Point p3 = new Point(1,2);
		
		assertTrue("Point p1 and p2 are not the same", p1.equals(p2));
		assertFalse("Point p2 and p3 are the same", p2.equals(p3));
	}

}
