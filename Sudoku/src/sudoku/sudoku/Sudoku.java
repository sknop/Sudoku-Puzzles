/*******************************************************************************
 * Copyright (c) 2014 Sven Erik Knop.
 * Licensed under the EUPL V.1.1
 *
 * This Software is provided to You under the terms of the European 
 * Union Public License (the "EUPL") version 1.1 as published by the 
 * European Union. Any use of this Software, other than as authorized 
 * under this License is strictly prohibited (to the extent such use 
 * is covered by a right of the copyright holder of this Software).
 *
 * This Software is provided under the License on an "AS IS" basis and 
 * without warranties of any kind concerning the Software, including 
 * without limitation merchantability, fitness for a particular purpose, 
 * absence of defects or errors, accuracy, and non-infringement of 
 * intellectual property rights other than copyright. This disclaimer 
 * of warranty is an essential part of the License and a condition for 
 * the grant of any rights to this Software.
 *
 * For more details, see http://joinup.ec.europa.eu/software/page/eupl.
 *
 * Contributors:
 *     2014 - Sven Erik Knop - initial API and implementation
 *******************************************************************************/
package sudoku.sudoku;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import sudoku.Cell;
import sudoku.Point;
import sudoku.Puzzle;
import sudoku.exceptions.AddCellException;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalFileFormatException;
import sudoku.unit.Nonet;

public class Sudoku extends Puzzle
{
	private final List<Nonet> rows = new ArrayList<>();
	private final List<Nonet> columns = new ArrayList<>();
	private final List<Nonet> boxes = new ArrayList<>();
	
	
	public Sudoku() {
		super(9);
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
	@Override
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
	
	@Override
	public void showMarkUp() {
		for (int x = 1; x <= 9; x++) {
			for (int y = 1; y <= 9; y++) {
				Point p = new Point(x,y);
				
				BitSet markUp = getMarkUp(p);
				
				System.out.println(String.format("(%s, %s) : %s", x, y, markUp));
			}
		}
		System.out.println();
	}

	@Override
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

	@Override
	public String toCLIString() {
		StringBuilder b = new StringBuilder();
		
		b.append("  ");
		for (int i = 0; i < 3; i++) {
			b.append("  ");
			for (int j = 1; j <= 3; j++) {
				b.append(i*3 + j);
				b.append(" ");
			}
		}
		b.append("\n");
		
		b.append(BigBorder); b.append("\n");
		for (int r = 0; r < 2; r++) {
			b.append(drawBigRow(r));
		}
		
		for (int i = 1; i <= 2; i++) {
			b.append(drawRow(2,i));
		}
		b.append(drawRow(2,3));

		b.append(BigBorder); b.append("\n");
		
		return b.toString();
	}
	
	private String drawBigRow(int r) {
		// made of 3 rows, finished with a Big Border
		StringBuilder b = new StringBuilder();
		
		for (int i = 1; i <= 2; i++) {
			b.append(drawRow(r,i));
		}
		b.append(drawRow(r,3));
		
		b.append(LittleBorder); b.append("\n");
		
		return b.toString();
	}
	
	private String drawRow(int bigRow, int row) {
		// made up of one front and 3 sections
		
		StringBuilder b = new StringBuilder();
		
		b.append(bigRow * 3 + row);
		b.append(Front);
		for (int section = 0; section < 3; section++) {
			b.append(drawOneSection(bigRow, row, section));
		}
		b.append("\n");
		
		return b.toString();
	}
	
	private String drawOneSection(int bigRow, int row, int section) {
		int x = bigRow * 3 + row;
		int y = section * 3;
		
		String x1 = getValueAsString(x, y + 1);
		String x2 = getValueAsString(x, y + 2);
		String x3 = getValueAsString(x, y + 3);
		
		return String.format(Section, x1, x2, x3);
	}
	
	private void addNonet(String name, List<Nonet> list, StringBuilder b) {
		b.append(name);
		b.append("\n");
		
		for( Nonet n : list ) {
			b.append(n);
			b.append("\n");
		}		
	}
	
	@Override
	public int getLow() {
		return 1;
	}

	@Override
	public int getHigh() {
		return 9;
	}

	@Override
	public void createRandomPuzzle() {
		reset(); // first, clear out any existing entries
		
		// let's try a different approach
		// independent boxes : I can set any number in any of these boxes without constraint
		
		Random random = new Random();
		
		int[][] independentBoxes = {
				{ 1, 5, 9 },
				{ 1, 6, 8 },
				{ 2, 4, 9 },
				{ 2, 6, 7 },
				{ 3, 4, 8 },
				{ 3, 5, 7 }
		};
		
		int[] boxSeeds = independentBoxes[ random.nextInt(independentBoxes.length)];
		for (int i : boxSeeds) {
			Nonet box = boxes.get(i - 1);
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
		
		// fill in the missing entries
		
		solveBruteForce();
		
		// now try to remove entries until the solution is not unique anymore
		
		// first, we get all Cells and shuffle them
		
		List<Cell> allCells = cells.values().stream().collect(Collectors.toList());
		Collections.shuffle(allCells);
		
		for (Cell c : allCells) {
			int value = c.getValue();
			c.reset();
			if (!isUnique()) {
				try {
					// does not produce unique puzzle, reset this value
					c.setValue(value);
				} catch (CellContentException e) {
					System.err.println("Should not happen " + e);
				}
			}
		}
	}

	public static void main(String args[]) {
		Sudoku sudoku = new Sudoku();
		
		System.out.println(sudoku);
	}

}
