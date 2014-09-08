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
package sudoku.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import sudoku.Cell;
import sudoku.exceptions.AddCellException;
import sudoku.unit.AbstractUnit;
import sudoku.unit.Nonet;

public class TestNonet
{

	@Test
	public void testBasics() {
		AbstractUnit nonet = new Nonet("2nd row");
		
		int x = 1;
		for (int y = 1; y <= 9; y++) {
			Cell cell = new Cell(x,y);
			try {
				nonet.addCell(cell);
			}
			catch (AddCellException e) {
				fail("Caught exception " + e);
			}
			
		}
		
		try {
			Cell cell = new Cell(1,10);
			nonet.addCell(cell);
			fail("Did not throw exception for additional cell");
		}
		catch(AddCellException e) {
			// this is expected
		}
	}

	@Test
	public void testCellUpdate() throws Exception {
		AbstractUnit nonet = new Nonet("1st row");
		
		List<Cell> cells = new ArrayList<>();
		int x = 3;
		for (int y = 1; y <= 9; y++) {
			Cell cell = new Cell(x,y);
			cells.add(cell);
			nonet.addCell(cell);
		}
		
		// expected to work
		for (int i = 0; i < 9; i++) {
			cells.get(i).setValue(i + 1);
		}
	}

	@Test
	public void testCellClear() throws Exception {
		AbstractUnit nonet = new Nonet("1st row");
		
		List<Cell> cells = new ArrayList<>();
		int x = 3;
		for (int y = 1; y <= 9; y++) {
			Cell cell = new Cell(x,y);
			cells.add(cell);
			nonet.addCell(cell);
		}
		
		// expected to work
		for (int i = 0; i < 9; i++) {
			cells.get(i).setValue(i + 1);
		}
		for (int i = 0; i < 9; i++) {
			cells.get(i).setValue(0);
		}
		for (int i = 0; i < 9; i++) {
			cells.get(i).setValue(i + 1);
		}
	}

}
