package sudoku.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import sudoku.exceptions.CellContentException;
import sudoku.samurai.Samurai;

public class TestSamurai
{
	Samurai samurai;
	
	@Before
	public void setUp() {
		samurai = new Samurai();
	}

	@Test
	public void testSetValue() {
		assertTrue("Cell (1,1) not empty", samurai.getValue(1, 1) == 0);
		try {
			samurai.setValue(1, 1, 1);
		} catch (CellContentException e) {
			fail("Should not happen " + e);
		}
		
		assertTrue("Cell (1,1) is not 1", samurai.getValue(1, 1) == 1);
		
		assertFalse("Samurai claims to be finished", samurai.isSolved());
	}

}
