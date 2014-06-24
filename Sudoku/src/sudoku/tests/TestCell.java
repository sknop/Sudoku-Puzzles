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
