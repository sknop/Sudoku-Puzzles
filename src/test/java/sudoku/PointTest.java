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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

		assertEquals(p1.hashCode(), p2.hashCode(),"Point p1 and p2 are not the same");
		assertNotEquals(p2.hashCode(), p3.hashCode(),"Point p2 and p3 are the same");
	}

	/**
	 * Test method for {@link sudoku.Point#equals(java.lang.Object)}.
	 */

	@Test
	public void testEqualsObject() {
		Point p1 = new Point(2,1);
		Point p2 = new Point(2,1);
		Point p3 = new Point(1,2);

		assertEquals(p1, p2,"Point p1 and p2 are not the same");
		assertNotEquals(p2, p3,"Point p2 and p3 are the same");
	}

}
