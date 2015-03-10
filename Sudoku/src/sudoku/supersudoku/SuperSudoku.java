package sudoku.supersudoku;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import sudoku.Cell;
import sudoku.Point;
import sudoku.Puzzle;
import sudoku.exceptions.AddCellException;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalFileFormatException;
import sudoku.unit.Nonet;
import sudoku.unit.Sexdectet;

public class SuperSudoku extends Puzzle
{
	private final List<Sexdectet> rows = new ArrayList<>();
	private final List<Sexdectet> columns = new ArrayList<>();
	private final List<Sexdectet> boxes = new ArrayList<>();
	
	public SuperSudoku() {
		super(16);
		initialize();
	}

	private final void initialize() {
		try {
			// create the Cells first, easier to see this way
			
			for (int x = 1; x <= maxValue; x++) {
				for (int y = 1; y <= maxValue; y++) {
					Point p = new Point(x,y);
					Cell cell = new Cell(maxValue, p);
					getCells().put(p, cell);
				}
			}
			
			for (int x = 1; x <= maxValue; x++) {
				Sexdectet row = new Sexdectet(String.format("Row %d", x));
				rows.add(row);
				for (int y = 1; y <= maxValue; y++) {
					Point p = new Point(x,y);
					Cell cell = getCells().get(p);
					row.addCell(cell);					
				}
			}

			for (int y = 1; y <= maxValue; y++) {
				Sexdectet column = new Sexdectet(String.format("Column %d", y));
				columns.add(column);
				for (int x = 1; x <= maxValue; x++) {
					Point p = new Point(x,y);
					Cell cell = getCells().get(p);
					column.addCell(cell);					
				}
			}
			
			for (int x1 = 0; x1 < 4; x1++) {
				for (int y1 = 0; y1 < 4; y1++) {
					Sexdectet box = new Sexdectet(String.format("Box %d/%d", x1+1, y1+1));
					boxes.add(box);
					
					for (int x2 = 1; x2 < 5; x2++) {
						int x = x1 * 4 + x2;
						for (int y2 = 1; y2 < 5; y2++) {
							int y = y1 * 4 + y2;
							Point p = new Point(x,y);
							Cell cell = getCells().get(p);
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

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("SuperSudoku\n");
		addSexdectet("Rows", rows, b);
		addSexdectet("Columns", columns, b);
		addSexdectet("Boxes", boxes, b);
		
		return b.toString();
	}

	private void addSexdectet(String name, List<Sexdectet> list, StringBuilder b) {
		b.append(name);
		b.append("\n");
		
		for( Sexdectet n : list ) {
			b.append(n);
			b.append("\n");
		}		
	}

	@Override
	public void importFile(Path path) throws IOException,
			IllegalFileFormatException, CellContentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exportFile(Path path) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showMarkUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showHints(int level) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toCLIString() {
		StringBuilder b = new StringBuilder();
		
		b.append("    1 2 3 4   5 6 7 8   9 A B C   D E F G\n");
		
		b.append(getBigBorder(4)); b.append("\n");

		for (int row = 1; row <= 16; row++) {
			b.append(formatAsString(row)); // prepend each row with its number
			
			b.append(Front);
			for (int section = 0; section < 4; section++) {
				drawOneSection(row, section, b);
			}
			b.append("\n");

			if ( row == 4 || row == 8 || row == 12) {
				b.append(getLittleBorder(4)); b.append("\n"); 				
			}
		}

		b.append(getBigBorder(4)); b.append("\n");
		
		return b.toString();
	}

	private String formatAsString(int value) {
        char c = '0';

        if (value < 10) {
            c = (char) ('0' + value);
        }
        else {
            c = (char) ('A' + value - 10);
        }

        return String.valueOf(c);
    }

	final String SuperSection = " %s %s %s %s |";
	
	private void drawOneSection(int row, int section, StringBuilder b) {
		int y = section * 4;
		
		String x1 = getValueAsString(row, y + 1);
		String x2 = getValueAsString(row, y + 2);
		String x3 = getValueAsString(row, y + 3);
		String x4 = getValueAsString(row, y + 4);
		
		b.append( String.format(SuperSection, x1, x2, x3, x4) );
	}

	@Override
	public int getLow() {
		return 1;
	}

	@Override
	public int getHigh() {
		return 16;
	}

	@Override
	public void createRandomPuzzle() {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		SuperSudoku s = new SuperSudoku();
		System.out.println(s);
	}
}
