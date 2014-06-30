package sudoku.samurai;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sudoku.Cell;
import sudoku.Point;
import sudoku.Puzzle;
import sudoku.exceptions.AddCellException;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.ValueOutsideRangeException;
import sudoku.unit.Nonet;

public class Samurai extends Puzzle
{
	private final List<Nonet> rows = new ArrayList<>();
	private final List<Nonet> columns = new ArrayList<>();
	private final List<Nonet> boxes = new ArrayList<>();
	
	public Samurai() {
		super();
		initialize();
	}
	
	private final void initialize() {
		// create Cells first
		// leave gaps where there are no cells
		
		for (int x = 1; x <= 21; x++) {
			for (int y = 1; y <= 21; y++) {
				if ((x > 9) && (x < 13)) {
					if ((y<7) || (y > 15)) {
						continue; // top and bottom gaps
					}
				}
				if ((y > 9) && (y < 13)) {
					if ((x<7) || (x > 15)) {
						continue; // left and right gaps
					}
				}
				
				Point p = new Point(x,y);
				Cell cell = new Cell(p);
				cells.put(p, cell);
			}
		}
		
//		System.out.println(cells.values().stream()
//				.sorted( (a,b) -> a.getLocation().compareTo(b.getLocation()) )
//				.collect(Collectors.toCollection(ArrayList::new))
//				);
		
		Object[][] sections = { 
				{ "A", 0, 0, false },
				{ "B", 12, 0, false },
				{ "C", 6, 6, true },
				{ "D", 0, 12, false },
				{ "E", 12, 12, false }
		};
		
		for ( Object[] section : sections) {
			String name = (String) section[0];
			int x = (int) section[1];
			int y = (int) section[2];
			boolean middle = (boolean) section[3];
			
			try {
				createSection(name, x, y, middle);
			} catch (AddCellException e) {
				System.out.println("Should never happen: " + e);
			}
		}
	}
	
	private void createSection(String name, int startX, int startY, boolean middle) 
			throws AddCellException {
		for (int x = 1; x <= 9; x++) {
			Nonet row = new Nonet(String.format("%s Row %d", name, x));
			rows.add(row);
			for (int y = 1; y <= 9; y++) {
				Point p = new Point(x + startX,y + startY);
				Cell cell = cells.get(p);
				row.addCell(cell);					
			}
		}

		for (int y = 1; y <= 9; y++) {
			Nonet column = new Nonet(String.format("%s Column %d", name, y));
			columns.add(column);
			for (int x = 1; x <= 9; x++) {
				Point p = new Point(x + startX,y + startY);
				Cell cell = cells.get(p);
				column.addCell(cell);					
			}
		}
		
		for (int x1 = 0; x1 < 3; x1++) {
			for (int y1 = 0; y1 < 3; y1++) {
				if (middle)
					if ((x1 == 0 || x1 == 2) && (y1==0))
						continue;
					if ((x1 == 0 || x1 == 2) && (y1==2))
						continue;

					Nonet box = new Nonet(String.format("%s Box %d/%d", name, x1+1, y1+1));
				boxes.add(box);
				
				for (int x2 = 1; x2 < 4; x2++) {
					int x = x1 * 3 + x2;
					for (int y2 = 1; y2 < 4; y2++) {
						int y = y1 * 3 + y2;
							
						Point p = new Point(x + startX,y + startY);
						Cell cell = cells.get(p);
						box.addCell(cell);
					}
				}
			}
		}
	}

	public void setValue(int x, int y, int value)
			throws CellContentException {
		setValue(new Point(x,y),value);
	}

	public void setValue(final Point p, int value)
			throws CellContentException {	
		if ( value < 0 || value > 9 ) {
			throw new ValueOutsideRangeException("Value " + value + " not in range (0-9)");
		}
		cells.get(p).setValue(value);
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
		
		for( Nonet n : list ) {
			b.append(n);
			b.append("\n");
		}		
	}
	
	public static void main(String[] args) {
		Samurai samurai = new Samurai();
		
		System.out.println(samurai);
	}
}
