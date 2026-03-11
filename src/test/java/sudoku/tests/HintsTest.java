package sudoku.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sudoku.Cell;
import sudoku.MarkUp;
import sudoku.Point;
import sudoku.exceptions.CellContentException;
import sudoku.unit.AbstractConstraint;

public class HintsTest
{
	final List<Trio> rows = new ArrayList<>();
	final List<Trio> columns = new ArrayList<>();
	final Map<Point, Cell> cells = new HashMap<>();

	final int maxSize = 3;
	
	class Trio extends AbstractConstraint
	{
		protected Trio(String position) {
			super(maxSize, position);
		}		
	}

	@BeforeEach
	public void setUp() throws Exception {
		for (int x = 1; x <= maxSize; x++) {
			for (int y = 1; y <= maxSize; y++) {
				Point p = new Point(x,y);
				Cell cell = new Cell(maxSize, p);
				cells.put(p, cell);
			}
		}
		
		for (int x = 1; x <= maxSize; x++) {
			Trio row = new Trio(String.format("Row %d", x));
			rows.add(row);
			for (int y = 1; y <= maxSize; y++) {
				Point p = new Point(x,y);
				Cell cell = cells.get(p);
				row.addCell(cell);					
			}
		}

		for (int y = 1; y <= maxSize; y++) {
			Trio column = new Trio(String.format("Column %d", y));
			columns.add(column);
			for (int x = 1; x <= maxSize; x++) {
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
		
		MarkUp markUp;
		
		Cell topRight = cells.get(new Point(1,2));
		markUp = topRight.getMarkUp();
		assertEquals(2, markUp.cardinality());
		assertTrue(markUp.get(2));
		assertTrue(markUp.get(3));
		
		Cell bottomRight = cells.get(new Point(3,3));
		markUp = bottomRight.getMarkUp();
		assertEquals(3, markUp.cardinality());
		assertTrue(markUp.get(1));
		assertTrue(markUp.get(2));
		assertTrue(markUp.get(3));
	}
	
	@Test
	public void testHintsUnique() throws CellContentException {
		MarkUp hints;
		
		Cell topMiddle = cells.get(new Point(1,2));
		topMiddle.setValue(2);
		
		Cell topRight = cells.get(new Point(1,3));
		hints = topRight.removeUniques(topRight.getMarkUp());
		assertEquals(1, hints.cardinality());
		assertTrue(hints.get(3));
		
		Cell bottomRight = cells.get(new Point(3,3));
		hints = bottomRight.removeUniques(bottomRight.getMarkUp());
		assertEquals(2, hints.cardinality());
		assertFalse(hints.get(3));
		assertTrue(hints.get(2));
		assertTrue(hints.get(1));
	}

	@Test
	public void testHintsPairs() throws CellContentException {
		MarkUp hints;
		
		Cell topMiddle = cells.get(new Point(2,2));
		topMiddle.setValue(1);
		
		Cell bottomRight = cells.get(new Point(3,3));
		hints = bottomRight.removePairs(bottomRight.getMarkUp());
		assertEquals(1, hints.cardinality());
		assertTrue(hints.get(1));
		assertFalse(hints.get(2));
		assertFalse(hints.get(3));
	}

    @Test
    public void testHintsUniquePairs() throws CellContentException {
        MarkUp hints;

		Cell middleTop =  cells.get(new Point(2,1));

		// Nothing else set, so we expect 2 and 3 still available

		hints = middleTop.getHints(4);

		assertEquals(2, hints.cardinality());
		assertTrue(hints.get(2));
		assertTrue(hints.get(3));

		Cell cell2 = cells.get(new Point(1,2));
		cell2.setValue(2);

		Cell cell3 = cells.get(new Point(3,3));
		cell3.setValue(2);

		// Nothing has changed so far
		assertEquals(2, middleTop.getHints(0).cardinality());
		// assertEquals(2, middleTop.getHints(1).cardinality());

		hints = middleTop.detectSingle(middleTop.getMarkUp());

		assertEquals(1, hints.cardinality());
    }
}
