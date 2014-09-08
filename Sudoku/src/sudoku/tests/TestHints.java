package sudoku.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import sudoku.Cell;
import sudoku.Point;
import sudoku.unit.AbstractUnit;

public class TestHints
{
	final List<Duet> rows = new ArrayList<>();
	final List<Duet> columns = new ArrayList<>();
	final Map<Point, Cell> cells = new HashMap<>();

	class Duet extends AbstractUnit 
	{
		protected Duet(String position) {
			super(2, position);
		}		
	}
	
	@Before
	public void setUp() throws Exception {
		for (int x = 1; x <= 2; x++) {
			for (int y = 1; y <= 2; y++) {
				Point p = new Point(x,y);
				Cell cell = new Cell(2, p);
				cells.put(p, cell);
			}
		}
		
		for (int x = 1; x <= 2; x++) {
			Duet row = new Duet(String.format("Row %d", x));
			rows.add(row);
			for (int y = 1; y <= 2; y++) {
				Point p = new Point(x,y);
				Cell cell = cells.get(p);
				row.addCell(cell);					
			}
		}

		for (int y = 1; y <= 2; y++) {
			Duet column = new Duet(String.format("Column %d", y));
			columns.add(column);
			for (int x = 1; x <= 2; x++) {
				Point p = new Point(x,y);
				Cell cell = cells.get(p);
				column.addCell(cell);					
			}
		}
		
		Cell cell = cells.get(new Point(1,1));
		cell.setValue(1);
	}

	@Test
	public void testMarkUp() {
		Cell topLeft = cells.get(new Point(1,1));
		assertEquals(1, topLeft.getValue());
		
		BitSet markUp;
		
		Cell topRight = cells.get(new Point(1,2));
		markUp = topRight.getMarkUp();
		assertEquals(1, markUp.cardinality());
		assertTrue(markUp.get(2));
		
		Cell bottomRight = cells.get(new Point(2,2));
		markUp = bottomRight.getMarkUp();
		assertEquals(2, markUp.cardinality());
		assertTrue(markUp.get(1));
		assertTrue(markUp.get(2));
	}
	
	@Test
	public void testHintsUnique() {
		BitSet hints;
		
		Cell topRight = cells.get(new Point(1,2));
		hints = topRight.removeUniques(topRight.getMarkUp());
		assertEquals(1, hints.cardinality());
		assertTrue(hints.get(2));
		
		Cell bottomRight = cells.get(new Point(2,2));
		hints = bottomRight.removeUniques(bottomRight.getMarkUp());
		assertEquals(1, hints.cardinality());
		assertFalse(hints.get(2));
		assertTrue(hints.get(1));
	}

}
