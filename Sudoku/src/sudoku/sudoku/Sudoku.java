package sudoku.sudoku;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import sudoku.Cell;
import sudoku.Point;
import sudoku.exceptions.AddCellException;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalFileFormatException;
import sudoku.exceptions.ValueOutsideRangeException;
import sudoku.unit.Nonet;

public class Sudoku extends Puzzle
{
	private final List<Nonet> rows = new ArrayList<>();
	private final List<Nonet> columns = new ArrayList<>();
	private final List<Nonet> boxes = new ArrayList<>();
	
	
	public Sudoku() {
		super();
		initialize();
	}
	
	private final void initialize() {
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
	
	public void importArray(int[][] values) throws CellContentException {
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				Point p = new Point(row + 1,col + 1);
				int value = values[row][col];
				if (value > 0)
					cells.get(p).setInitValue(value);
			}
		}
	}
	
	/**
	 * Imports a Sudoku puzzle from a file.
	 * The expected format is
	 * 
	 * CSV in 9 rows
	 * Empty Cells are signaled by a 0
	 * For example
	 * 
	 *  1,0,3,0,0,6,7,8,9
	 *  0,0,0,0,0,0,0,0,0
	 *  
	 * @param path : Path
	 * @throws IOException
	 * @author Sven Erik Knop
	 * @throws IOException, IllegalFileFormatException, CellContentException 
	 */
	public void importFile (Path path) 
			throws IOException, IllegalFileFormatException, CellContentException {
		int[][] values = new int[9][9];

		try( BufferedReader br = Files.newBufferedReader(path) ) {			
			String line;
			int row = 0;
			
			while ( (line = br.readLine()) != null) {
				String[] lineValues = line.split(",");
				if (lineValues.length != 9) {
					throw new IllegalFileFormatException("Illegal entry in file " + path + " : " + line);
				}
				
				for (int col = 0; col < 9; col++) {
					values[row][col] = Integer.parseInt(lineValues[col]);
				}
				
				row++;
			}
		}
		
		reset();
		
		importArray(values);
	}

	private void reset() {
		for (Cell c : cells.values()) {
			c.reset();
		}
	}
	
	public void exportFile (Path path) throws IOException {
		OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.WRITE};
		
		try (BufferedWriter writer = Files.newBufferedWriter(path, options )) {
			
			for (int row = 1; row <= 9; row++) {
				writer.append(Integer.toString(getValue(row,1)));
				for (int col = 2; col <= 9; col++) {
					writer.append(",");
					writer.append(Integer.toString(getValue(row,col)));
				}
				writer.append("\n");
			}
		}
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
	
	// instant test case to prove printing
	
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

	public static void main(String args[]) {
		Sudoku sudoku = new Sudoku();
		
		System.out.println(sudoku);
	}
}
