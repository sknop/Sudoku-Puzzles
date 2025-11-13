/*******************************************************************************
 * Copyright (c) 2014 Sven Erik Knop.
 * Licensed under the EUPL V.1.1
 * <p>
 * This Software is provided to You under the terms of the European 
 * Union Public License (the "EUPL") version 1.1 as published by the 
 * European Union. Any use of this Software, other than as authorized 
 * under this License is strictly prohibited (to the extent such use 
 * is covered by a right of the copyright holder of this Software).
 * <p>
 * This Software is provided under the License on an "AS IS" basis and 
 * without warranties of any kind concerning the Software, including 
 * without limitation merchantability, fitness for a particular purpose, 
 * absence of defects or errors, accuracy, and non-infringement of 
 * intellectual property rights other than copyright. This disclaimer 
 * of warranty is an essential part of the License and a condition for 
 * the grant of any rights to this Software.
 * <p>
 * For more details, see http://joinup.ec.europa.eu/software/page/eupl.
 * <p>
 * Contributors:
 *     2014 - Sven Erik Knop - initial API and implementation
 *******************************************************************************/
package sudoku.samurai;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import sudoku.Cell;
import sudoku.MarkUp;
import sudoku.Point;
import sudoku.Puzzle;
import sudoku.exceptions.AddCellException;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalFileFormatException;
import sudoku.unit.Nonet;

public class Samurai extends Puzzle implements Cloneable
{
	private List<Nonet> rows = new ArrayList<>();
	private List<Nonet> columns = new ArrayList<>();
	private List<Nonet> boxes = new ArrayList<>();
	
	public final int PUZZLE_WIDTH = 21;
	public final int removeLimit = 280;
	
	public Samurai() {
		super(9);
		initialize();
	}

    private interface CellAccess {
		void apply(int x, int y) throws CellContentException;
	}

	/**
	 * Iterates through all the cells and applies the CellAccess functor
	 * Will exclude the empty regions.
	 * @param access : CellAccess
	 * @throws CellContentException from access.apply().
	 */
	private void eachCell(CellAccess access) throws CellContentException {
		for (int x = 1; x <= PUZZLE_WIDTH; x++) {
			for (int y = 1; y <= PUZZLE_WIDTH; y++) {
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
				
				access.apply(x, y);
			}
		}
	}
	
	private void initialize() {
		// create Cells first
		// leave gaps where there are no cells

		try {
			eachCell((x, y) -> {
                Point p = new Point(x,y);
                Cell cell = new Cell(9, p);
                getCells().put(p, cell);
            });
		} catch (CellContentException e) {
			System.out.println("Should not happen " + e);
		}

		linkToConstraints();
	}

	private void linkToConstraints() {
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
				Cell cell = getCells().get(p);
				row.addCell(cell);					
			}
		}

		for (int y = 1; y <= 9; y++) {
			Nonet column = new Nonet(String.format("%s Column %d", name, y));
			columns.add(column);
			for (int x = 1; x <= 9; x++) {
				Point p = new Point(x + startX,y + startY);
				Cell cell = getCells().get(p);
				column.addCell(cell);					
			}
		}
		
		for (int x1 = 0; x1 < 3; x1++) {
			for (int y1 = 0; y1 < 3; y1++) {
				if (middle) {
					if ((x1 == 0 || x1 == 2) && (y1==0))
						continue;
					if ((x1 == 0 || x1 == 2) && (y1==2))
						continue;
				}
				
				Nonet box = new Nonet(String.format("%s Box %d/%d", name, x1+1, y1+1));
				boxes.add(box);
			
				for (int x2 = 1; x2 < 4; x2++) {
					int x = x1 * 3 + x2;
					for (int y2 = 1; y2 < 4; y2++) {
						int y = y1 * 3 + y2;
						
						Point p = new Point(x + startX,y + startY);
						Cell cell = getCells().get(p);
						box.addCell(cell);
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("Sudoku\n");
		addNonet("Rows", rows, b);
		addNonet("Columns", columns, b);
		addNonet("Boxes", boxes, b);
		
		addCells(b);
		
		return b.toString();
	}

	static String FULL_BORDER = "   +---------------+-------+-------+-------+---------------+";
	static String INNER_BORDER = "   |-------+-------+-------+-------+-------+-------+-------|";
	
	@Override
	public String toCLIString() {
		StringBuilder b = new StringBuilder();
		
		b.append("    0 0 0   0 0 0   0 0 0    1 1 1   1 1 1   1 1 1   1 2 2\n");
		b.append("    1 2 3   4 5 6   7 8 9    0 1 2   3 4 5   6 7 8   9 0 1\n");
		
		
		border(b, getBigBorder(3));
		
		for (int row = 1; row <= 21; row++) {
			b.append(String.format("%02d", row)); // prepend each row with its number
			
			if (row <= 6 || row >= 16) {
				drawOneBlankOne(row, b);
			}
			else if ( (row >= 7 && row <= 9) || (row >= 13 && row <= 15) ) {
				drawFull(row, b);				
			}
			else if ( row >= 10 && row <= 12 ) {
				drawBlankOneBlank(row, b);
			}
			
			b.append("\n");

			if ( row == 3 || row == 18) {
				border(b, getLittleBorder(3)); 				
			}
			if ( row == 9 || row == 12 ) {
				b.append(FULL_BORDER);
				b.append("\n");
			}
			if ( row == 6 || row == 15 ) {
				b.append(INNER_BORDER);
				b.append("\n");
			}
		}
		
		border(b, getBigBorder(3));

		return b.toString();
	}

	private void border(StringBuilder b, String theBorder) {
		b.append(" ");
		b.append(theBorder); 
		b.append("     ");
		b.append(theBorder); 
		
		b.append("\n");
	}
	
	private void drawOneBlankOne(int row, StringBuilder b) {
		b.append(Front);
		
		for (int section = 0; section < 3; section++) {
			drawOneSection(row, section, b);			
		}
		b.append("      ");
		b.append(Front);

		for (int section = 4; section < 7; section++) {
			drawOneSection(row, section, b);			
		}
	}
	
	private void drawFull(int row, StringBuilder b) {
		b.append(Front);

		for (int section = 0; section < 7; section++) {
			drawOneSection(row, section, b);			
		}

	}
	
	private void drawBlankOneBlank(int row, StringBuilder b) {
		b.append("                ");
		b.append(Front);
	
		for (int section = 2; section < 5; section++) {
			drawOneSection(row, section, b);			
		}
	}

	private void drawOneSection(int row, int section, StringBuilder b) {
		int y = section * 3;
		
		String x1 = getValueAsString(row, y + 1);
		String x2 = getValueAsString(row, y + 2);
		String x3 = getValueAsString(row, y + 3);
		
		b.append( String.format(Section, x1, x2, x3) );
	}

	private void addNonet(String name, List<Nonet> list, StringBuilder b) {
		b.append(name);
		b.append("\n");
		
		for( Nonet n : list ) {
			b.append(n);
			b.append("\n");
		}		
	}
	
	private void addCells(StringBuilder builder) {
		ArrayList<Cell> sortedCells = getCells().values()
			.stream()
			.sorted(Comparator.comparing(Cell::getLocation))
			.collect( Collectors.toCollection(ArrayList::new) );
		
		for (Cell c : sortedCells) {
			builder.append(c);
			builder.append("\n");
		}
	}
	
	public void importArray(int[][] values) throws CellContentException {
		eachCell((x, y) -> {
            int value = values[x-1][y-1];
            if (value > 0) {
                Point p = new Point(x,y);
                getCells().get(p).setInitValue(value);
            }
        });
	}
	

	/**
	 * Imports a Sudoku puzzle from a file.
	 * The expected format is
	 * <p>
	 * CSV in 9 rows
	 * Empty Cells are signaled by a 0
	 * For example
	 * <p>
	 *  1,0,3,0,0,6,7,8,9
	 *  0,0,0,0,0,0,0,0,0
	 *  
	 * @param path : Path
	 * @throws IOException, IllegalFileFormatException, CellContentException 
	 */
	@Override
	public void importFile (Path path) 
			throws IOException, IllegalFileFormatException, CellContentException {
		int[][] values = new int[PUZZLE_WIDTH][PUZZLE_WIDTH];

		try( BufferedReader br = Files.newBufferedReader(path) ) {			
			String line;
			int row = 0;
			
			while ( (line = br.readLine()) != null) {
				String[] lineValues = line.split(",");
				if (lineValues.length != PUZZLE_WIDTH) {
					throw new IllegalFileFormatException("Illegal entry in file " + path + " : " + line);
				}
				
				for (int col = 0; col < PUZZLE_WIDTH; col++) {
					values[row][col] = Integer.parseInt(lineValues[col]);
				}
				
				row++;
			}
		}
		
		reset();
		
		importArray(values);
	}
	
	@Override
	public void exportFile (Path path) throws IOException {
		OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.WRITE};

		int[][] values = new int[PUZZLE_WIDTH][PUZZLE_WIDTH];
		
		try {
			eachCell((x, y) -> values[x-1][y-1] = getValue(x,y));
		} catch (CellContentException e) {
			System.out.println("Should not happen: " + e);
		}

		try (BufferedWriter writer = Files.newBufferedWriter(path, options )) {
						
			for (int row = 0; row < PUZZLE_WIDTH; row++) {
				writer.append(Integer.toString(values[row][0]));
				for (int col = 1; col < PUZZLE_WIDTH; col++) {
					writer.append(",");
					writer.append(Integer.toString(values[row][col]));
				}
				writer.append("\n");
			}
		}
	}


	@Override
	public void createRandomPuzzle() {
//	    0 0 0   0 0 0   0 0 0    1 1 1   1 1 1   1 1 1   1 2 2
//	    1 2 3   4 5 6   7 8 9    0 1 2   3 4 5   6 7 8   9 0 1
//	   +-----------------------+       +-----------------------+
//	01 |       |       |       |       |       |       |       |
//	02 |   1   |   2   |   3   |       |   24  |   25  |   26  |
//	03 |       |       |       |       |       |       |       |
//	   |-------+-------+-------|       |-------+-------+-------|
//	04 |       |       |       |       |       |       |       |
//	05 |   4   |   5   |   6   |       |   27  |   28  |   29  |
//	06 |       |       |       |       |       |       |       |
//	   |-------+-------+-------+-------+-------+-------+-------|
//	07 |       |       |       |       |       |       |       |
//	08 |   7   |   8   |   9   |   19  |   30  |   31  |   32  |
//	09 |       |       |       |       |       |       |       |
//	   +---------------+-------+-------+-------+---------------+
//	10                 |       |       |       |
//	11                 |  20   |   21  |   22  |
//	12                 |       |       |       |
//	   +---------------+-------+-------+-------+---------------+
//	13 |       |       |       |       |       |       |       |
//	14 |   10  |   11  |   12  |   23  |   33  |   34  |   35  |
//	15 |       |       |       |       |       |       |       |
//	   |-------+-------+-------+-------+-------+-------+-------|
//	16 |       |       |       |       |       |       |       |
//	17 |   13  |   14  |   15  |       |   36  |   37  |   38  |
//	18 |       |       |       |       |       |       |       |
//	   |-------+-------+-------|       |-------+-------+-------|
//	19 |       |       |       |       |       |       |       |
//	20 |   16  |   17  |   18  |       |   39  |   40  |   41  |
//	21 |       |       |       |       |       |       |       |
//	   +-----------------------+       +-----------------------+

		reset(); // first, clear out any existing entries
		
		int[][] boxSeeds = { { 9, 21, 33 },
							 { 1, 5 },
							 { 14, 16 },
							 { 26, 28 },
							 { 37, 41 }
						   };
		int[][] solveThese = { { 19, 30, 20, 22, 12, 23}, 
							   { 2, 3, 4, 6, 7, 8 },
							   { 10, 11, 13, 15, 17, 18 },
							   { 24, 25, 27, 29, 31, 32 },
							   { 34, 35, 36, 38, 39, 40 }
							 };

		for (int b = 0; b < 5; b++) {
			for (int i : boxSeeds[b]) {
				Nonet box = boxes.get(i - 1); // indexed from 0, but written from 1 for easy debugging
				List<Cell> cells = box.getCells();
				
				List<Integer> seed = Arrays.asList( 1,2,3,4,5,6,7,8,9 );
				Collections.shuffle(seed);
	
				for (int c = 0; c < 9; c++) {
					try {
						Cell cell = cells.get(c);
						cell.setValue(seed.get(c));
					} catch (CellContentException e) {
						System.err.println("Shouldn't happen " + e);
					} 
				}			
			}
	
			LinkedList<Cell> smallSolve = new LinkedList<>();
			for (int i : solveThese[b]) {
				Nonet box = boxes.get(i - 1); // indexed from 0, but written from 1 for easy debugging
				smallSolve.addAll( box.getCells() );
			}
			solveRecursive(smallSolve);
		}

		List<Cell> allCells = new ArrayList<>(getCells().values());
		Collections.shuffle(allCells);
		
		int counter = 0;
		
		for (Cell c : allCells) {
			int value = c.getValue();
			c.reset();

			if (isUnique() > 1) {
				try {
					// does not produce unique puzzle, reset this value
					c.setValue(value);
				} catch (CellContentException e) {
					System.err.println("Should not happen " + e);
				}
			}
			
			if (counter++ > removeLimit) {
				break;
			}
		}

		for (Cell c : allCells) {
			c.makeReadOnly();
		}
	}


	@Override
	public int getLow() {
		return 1;
	}

	@Override
	public int getHigh() {
		return 21;
	}

	@Override
	public void showMarkUp() {
		showHints(0);
	}

	@Override
	public void showHints(int level) {
		try {
			eachCell((x, y) -> {
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
            });
		} catch (CellContentException e) {
			System.out.println("Shouldn't happen " + e);
		}
		System.out.println();
	}

	@Override
	public Samurai clone() {
		Samurai clone = (Samurai) super.clone();

		clone.rows = new ArrayList<>();
		clone.columns = new ArrayList<>();
		clone.boxes = new ArrayList<>();

		clone.linkToConstraints();

		copyCellContentToClone(clone);

		return clone;
	}

}
