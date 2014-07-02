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

import org.junit.Test;

import sudoku.Cell;
import sudoku.exceptions.CellContentException;

public class TestCell
{

	@Test
	public void testSetValue() {
		Cell cell = new Cell(1,1);
		assertTrue("Cell is not empty", cell.getValue() == 0);
		
		try {
			cell.setValue(1);
		}
		catch(Exception e) {
			fail("Not supposed to throw an exception yet");
		}
		
		assertTrue("Cell value not expected", cell.getValue() == 1);
	}

	@Test(expected=CellContentException.class)
	public void testReadOnly() throws CellContentException {
		Cell cell = new Cell(1,1);
		assertTrue("Cell is not empty", cell.getValue() == 0);
		
		try {
			cell.setInitValue(1);
		}
		catch(Exception e) {
			fail("Not supposed to throw an exception yet");
		}
		
		cell.setValue(0);
	}

}
