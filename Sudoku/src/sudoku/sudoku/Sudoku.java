package sudoku.sudoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sudoku.Cell;
import sudoku.Point;
import sudoku.exceptions.AddCellException;
import sudoku.unit.Nonet;

public class Sudoku
{
	private final Map<Point, Cell> cells = new HashMap<>();
	private final List<Nonet> rows = new ArrayList<>();
	private final List<Nonet> columns = new ArrayList<>();
	private final List<Nonet> boxes = new ArrayList<>();
	
	
	public Sudoku() {
		initialize();
	}
	
	private void initialize() {
		try {
			// create the Cells first, easier to see this way
			
			for (int x = 1; x <= 9; x++) {
				for (int y = 1; y <= 9; y++) {
					Point p = new Point(x,y);
					Cell cell = new Cell(p);
					cells.put(p, cell);
				}
			}
			
			for (int x = 1; x <= 9; x++) {
				Nonet row = new Nonet(String.format("Row %d", x));
				rows.add(row);
				for (int y = 1; y <= 9; y++) {
					Point p = new Point(x,y);
					Cell cell = cells.get(p);
					row.addCell(cell);					
				}
			}

			for (int y = 1; y <= 9; y++) {
				Nonet column = new Nonet(String.format("Column %d", y));
				columns.add(column);
				for (int x = 1; x <= 9; x++) {
					Point p = new Point(x,y);
					Cell cell = cells.get(p);
					column.addCell(cell);					
				}
			}
			
			for (int x1 = 0; x1 < 3; x1++) {
				for (int y1 = 0; y1 < 3; y1++) {
					Nonet box = new Nonet(String.format("Box %d/%d", x1+1, y1+1));
					boxes.add(box);
					
					for (int x2 = 1; x2 < 4; x2++) {
						int x = x1 * 3 + x2;
						for (int y2 = 1; y2 < 4; y2++) {
							int y = y1 * 3 + y2;
							Point p = new Point(x,y);
							Cell cell = cells.get(p);
							box.addCell(cell);
						}
					}
				}
			}
		}
		catch(AddCellException e) {
			System.err.println("Should never happen:" + e);
		}
	}
	
	public Integer getValue(int x, int y) {
		return cells.get(new Point(x,y)).getValue();
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("Sudoku\n");
		addNonet("Rows", rows, b);
		addNonet("Columns", columns, b);
		addNonet("Boxes", boxes, b);
		
		return b.toString();
	}

	private void addNonet(String name, List<Nonet> list, StringBuilder b) {
		b.append(name);
		b.append("\n");
		
		for (Nonet n : list) {
			b.append(n);
			b.append("\n");
		}		
	}
	
	public static void main(String args[]) {
		Sudoku sudoku = new Sudoku();
		
		System.out.println(sudoku);
	}
}
