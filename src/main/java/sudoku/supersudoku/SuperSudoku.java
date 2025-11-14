package sudoku.supersudoku;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import sudoku.*;
import sudoku.exceptions.AddCellException;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalFileFormatException;
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

	private void initialize() {
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

	public void importArray(int[][] values) throws CellContentException {
		for (int row = 0; row < 16; row++) {
			for (int col = 0; col < 16; col++) {
				Point p = new Point(row + 1,col + 1);
				int value = values[row][col];
				if (value > 0)
					getCells().get(p).setInitValue(value);
			}
		}
	}

	/**
	 * Imports a Sudoku puzzle from a file.
	 * The expected format is
	 * <p>
	 * CSV in 16 rows
	 * Empty Cells are signalled by a 0
	 * For example
	 * <p>
	 *  1,0,3,0,0,6,7,8,9,A,B,C,D,E,F,G
	 *  0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
	 *  
	 * @param path : Path
	 * @throws IOException, IllegalFileFormatException, CellContentException 
	 */
	@Override
	public void importFile (Path path) 
			throws IOException, IllegalFileFormatException, CellContentException {
		int[][] values = new int[16][16];

		try( BufferedReader br = Files.newBufferedReader(path) ) {			
			String line;
			int row = 0;
			
			while ( (line = br.readLine()) != null) {
				String[] lineValues = line.split(",");
				if (lineValues.length != 16) {
					throw new IllegalFileFormatException("Illegal entry in file " + path + " : " + line);
				}
				
				for (int col = 0; col < 16; col++) {
					values[row][col] = Integer.parseInt(lineValues[col]);
				}
				
				row++;
			}
		}
		
		reset();
		
		importArray(values);
	}

	@Override
	public void exportFile(Path path) throws IOException {
		OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.WRITE};
		
		try (BufferedWriter writer = Files.newBufferedWriter(path, options )) {
			
			for (int row = 1; row <= 16; row++) {
				writer.append(Integer.toString(getValue(row,1)));
				for (int col = 2; col <= 16; col++) {
					writer.append(",");
					writer.append(Integer.toString(getValue(row,col)));
				}
				writer.append("\n");
			}
		}
	}

	@Override
	public void showMarkUp() {
		showHints(0);
	}

	@Override
	public void showHints(int level) {
		for (int x = 1; x <= 16; x++) {
			for (int y = 1; y <= 16; y++) {
				Point p = new Point(x,y);
				
				if (!isReadOnly(p)) {
					MarkUp markUp = getHints(p, level);
					if (level == 0) {
						System.out.printf("(%s, %s) : %s%n", x, y, markUp);
					}
					else {
						System.out.printf("(%s, %s) : %s [%s]%n", x, y, markUp, getHints(p, 0));
					}
				}
			}
		}
		System.out.println();
	}

	@Override
	public String toCLIString() {
		StringBuilder b = new StringBuilder();
		
		b.append("    1 2 3 4   5 6 7 8   9 A B C   D E F G\n");
		
		b.append(getBigBorder(4)); b.append("\n");

		for (int row = 1; row <= 16; row++) {
			b.append(NumberConverter.getValueAsString(row)); // prepend each row with its number
			
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
		reset(); // first, clear out any existing entries
		
		// independent boxes : I can set any number in any of these boxes without constraint
		
		Random random = new Random();
		
		int[][] independentBoxes = {
				{ 1, 6, 11, 16 },
				{ 1, 6, 12, 15 },
				{ 1, 7, 10, 16 },
				{ 1, 7, 12, 14 },
				{ 1, 8, 10, 15 },
				{ 1, 8, 11, 14 },

				{ 2, 5, 11, 16 },
				{ 2, 5, 12, 15 },
				{ 2, 7,  9, 16 },
				{ 2, 7, 12, 13 },
				{ 2, 8,  9, 14 },
				{ 2, 8, 11, 13 },
				
				{ 3, 5, 10, 16 },
				{ 3, 5, 12, 14 },
				{ 3, 6,  9, 16 },
				{ 3, 6, 12, 13 },
				{ 3, 8,  9, 14 },
				{ 3, 8, 10, 13 },

				{ 4, 5, 10, 15 },
				{ 4, 5, 11, 14 },
				{ 4, 6,  9, 15 },
				{ 4, 6, 11, 13 },
				{ 4, 7,  9, 14 },
				{ 4, 7, 10, 13 },
		};
		
		int[] boxSeeds = independentBoxes[ random.nextInt(independentBoxes.length)];
		
		for (int i : boxSeeds) {
			Sexdectet box = boxes.get(i - 1);
			List<Cell> cells = box.getCells();
			
			List<Integer> seed = Arrays.asList( 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16 );
			Collections.shuffle(seed);

			for (int c = 0; c < 16; c++) {
				try {
					Cell cell = cells.get(c);
					cell.setValue(seed.get(c));
				} catch (CellContentException e) {
					System.err.println("Shouldn't happen " + e);
				} 
			}			
		}
		
		// fill in the missing entries
		
		solveBruteForce();

		// now try to remove entries until the solution is not unique anymore
		
		// first, we get all Cells and shuffle them
		
		List<Cell> allCells = new ArrayList<>(getCells().values());
		Collections.shuffle(allCells);
		
		int counter = 0;
		
		for (Cell c : allCells) {
			int value = c.getValue();
			c.reset();

			// for now, need to solve performance problems
			
			if (isUnique() > 1) {
				try {
					// does not produce unique puzzle, reset this value
					c.setValue(value);
				} catch (CellContentException e) {
					System.err.println("Should not happen " + e);
				}
			}

            // System.out.println("Removed " + counter + " entry");

            // only check if we reached the limit after checking for uniqueness
            if (counter++ > 200) break;
        }
		
		for (Cell c : allCells) {
			c.makeReadOnly();
		}
	}

    static void main() {
		SuperSudoku s = new SuperSudoku();
		System.out.println(s);
	}
}
