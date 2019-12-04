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
package sudoku;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import sudoku.exceptions.CellContentException;

public class CellTest
{

	@Test
	public void testSetValue() {
		Cell cell = new Cell(1, 1,1);
		assertEquals(0, cell.getValue(),"Cell is not empty");
		
		try {
			cell.setValue(1);
		}
		catch(Exception e) {
			fail("Not supposed to throw an exception yet");
		}

		assertEquals(1, cell.getValue(),"Cell value not expected");
	}

	@Test
	public void testReadOnly() throws CellContentException {
		Cell cell = new Cell(1, 1,1);
		assertEquals(0, cell.getValue(),"Cell is not empty");
		
		try {
			cell.setInitValue(1);
		}
		catch(Exception e) {
			fail("Not supposed to throw an exception yet");
		}

		assertThrows(CellContentException.class, () -> cell.setInitValue(0));
	}

    @Test
    public void testRange() {
        Cell cell = new Cell(16, 1,1);
        assertTrue(cell.empty(),"Cell is not empty");

        try {
            cell.setValue(16);
        }
        catch(Exception e) {
            fail("Not supposed to throw an exception yet");
        }

				assertEquals(16, cell.getValue(),"Cell value not expected");
        assertEquals("(1,1) : G", cell.toString(),"Incorrect String conversion");
    }

}
