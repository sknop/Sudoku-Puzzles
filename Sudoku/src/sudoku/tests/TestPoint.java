/**
 * 
 */
package sudoku.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import sudoku.Point;

/**
 * @author Sven Erik Knop
 *
 */
public class TestPoint
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
