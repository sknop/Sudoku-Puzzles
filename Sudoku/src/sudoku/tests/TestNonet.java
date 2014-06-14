package sudoku.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import sudoku.Cell;
import sudoku.exceptions.AddCellException;
import sudoku.unit.Nonet;

public class TestNonet
{

	@Test
	public void testBasics() {
		Nonet nonet = new Nonet("2nd row");
		
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
		Nonet nonet = new Nonet("1st row");
		
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
		Nonet nonet = new Nonet("1st row");
		
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
			cells.get(i).setValue(null);
		}
		for (int i = 0; i < 9; i++) {
			cells.get(i).setValue(i + 1);
		}
	}

}
