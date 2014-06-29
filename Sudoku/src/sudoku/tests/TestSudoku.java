package sudoku.tests;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import sudoku.Point;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.ValueOutsideRangeException;
import sudoku.sudoku.Sudoku;

public class TestSudoku
{
	Sudoku sudoku;
	
	@Before
	public void setUp() throws Exception {
		sudoku = new Sudoku();
	}

	@Test
	public void testSetValue() {
		Point p = new Point(1,1);
		try {
			sudoku.setValue(p, 1);
		}
		catch( Exception e) {
			fail("Do not expect exception here");
		}
	}

	@Test
	public void testGetValue() {
		Point p = new Point(1,1);
		try {
			sudoku.setValue(p, 1);
			
			assertTrue("Correct value", sudoku.getValue(p) == 1);
		}
		catch( Exception e) {
			fail("Do not expect exception here");
		}
	}

	@Test
	public void testResetValue()
	{
		Point p1 = new Point(1,1);
		Point p2 = new Point(1,9);
		try {
			sudoku.setValue(p1, 1);
			sudoku.setValue(p2, 9);

			sudoku.setValue(p2, 0);
			assertTrue("Correct value", sudoku.getValue(p2) == 0);
			sudoku.setValue(p1, 9);
			
			assertTrue("Correct value", sudoku.getValue(p1) == 9);
		}
		catch( Exception e) {
			fail("Do not expect exception here");
		}
	}

	@Test(expected=ValueOutsideRangeException.class)
	public void testValueOutSideRange() throws CellContentException
	{
		Point p1 = new Point(1,1);
		sudoku.setValue(p1, -1);
	}
	
	@Test(expected=CellContentException.class)
	public void testDoubleValue() throws CellContentException {
		Point p1 = new Point(1,1);
		Point p2 = new Point(2,1);

		sudoku.setValue(p1, 1);
		sudoku.setValue(p2, 1);
	}
	
	private boolean checkMarkup(Point p, int value) {
		Set<Integer> markUp = sudoku.getMarkUp(p);
		
		return markUp.contains(value);
	}
	
	@Test
	public void testMarkUp() {
		try {
			Point p = new Point(1,1);
			sudoku.setValue(p, 1);

			
			for (int row = 2; row <= 9; row++) {
				Point t = new Point(row, 1);
				
				assertTrue(String.format("Found 1 in %s", t), ! checkMarkup(t, 1));
				assertTrue(String.format("No 2 in %s", t), checkMarkup(t, 2));
			}

			for (int col = 2; col <= 9; col++) {
				Point t = new Point(1, col);
				
				assertTrue(String.format("Found 1 in %s", t), ! checkMarkup(t,1));
				assertTrue(String.format("No 2 in %s", t), checkMarkup(t, 2));
			}

			assertTrue("Markup not empty", sudoku.getMarkUp(p).isEmpty());
		}
		catch( Exception e) {
			fail("Do not expect exception here: " + e);
		}

	}

	@Test
	public void testBruteForce() {
		try {
			assertTrue("Cannot solve empty Sudoku brute force", sudoku.solveBruteForce());
			assertTrue("Not solved after brute force", sudoku.isSolved());
		}
		catch( Exception e) {
			fail("Do not expect exception here " + e);
		}
	}
}
