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
package sudoku.sudoku;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.BitSet;

import sudoku.Point;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;
import sudoku.exceptions.ValueOutsideRangeException;

public class SudokuTest
{
	Sudoku sudoku;
	
	@BeforeEach
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
			
			assertEquals(sudoku.getValue(p), 1, "Correct value");
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
			assertEquals(sudoku.getValue(p2),0, "Correct value");
			sudoku.setValue(p1, 9);
			
			assertEquals(sudoku.getValue(p1),9,"Correct value");
		}
		catch( Exception e) {
			fail("Do not expect exception here");
		}
	}

	@Test
	public void testValueOutSideRange() throws CellContentException, IllegalCellPositionException
	{
		Point p1 = new Point(1,1);
		assertThrows(ValueOutsideRangeException.class, () -> sudoku.setValue(p1, -1));
	}
	
	@Test
	public void testDoubleValue() throws CellContentException, IllegalCellPositionException {
		Point p1 = new Point(1,1);
		Point p2 = new Point(2,1);

		sudoku.setValue(p1, 1);
		assertThrows(CellContentException.class, () -> sudoku.setValue(p2, 1));
	}

	@Test
	public void testIllegalCell() throws CellContentException, IllegalCellPositionException {
		Point p1 = new Point(9,10);

		assertThrows(IllegalCellPositionException.class, () -> sudoku.setValue(p1, 1));
	}

	private boolean checkMarkup(Point p, int value) {
		BitSet markUp = sudoku.getHints(p,0);
		
		return markUp.get(value);
	}
	
	@Test
	public void testMarkUp() {
		try {
			Point p = new Point(1,1);
			sudoku.setValue(p, 1);

			
			for (int row = 2; row <= 9; row++) {
				Point t = new Point(row, 1);
				
				assertTrue(! checkMarkup(t, 1),String.format("Found 1 in %s", t));
				assertTrue(checkMarkup(t, 2),String.format("No 2 in %s", t));
			}

			for (int col = 2; col <= 9; col++) {
				Point t = new Point(1, col);
				
				assertTrue(! checkMarkup(t,1),String.format("Found 1 in %s", t));
				assertTrue(checkMarkup(t, 2),String.format("No 2 in %s", t));
			}

			assertTrue(sudoku.getHints(p,0).isEmpty(),"Markup not empty");
		}
		catch( Exception e) {
			fail("Do not expect exception here: " + e);
		}

	}

	@Test
	public void testBruteForce() {
		try {
			assertTrue(sudoku.solveBruteForce(),"Cannot solve empty Sudoku brute force");
			assertTrue(sudoku.isSolved(),"Not solved after brute force");
		}
		catch( Exception e) {
			fail("Do not expect exception here " + e);
		}
	}
}
