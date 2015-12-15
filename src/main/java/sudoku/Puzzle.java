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
package sudoku;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;
import sudoku.exceptions.IllegalFileFormatException;
import sudoku.exceptions.ValueOutsideRangeException;

public abstract class Puzzle
{
	protected static final String Front = " |";
	protected static final String Section = " %s %s %s |";

	protected int maxValue;
	
	public int getMaxValue() {
		return maxValue;
	}

	protected int tries = 0;
	
	private final Map<Point, Cell> cells = new HashMap<>();

	public Puzzle(int maxValue) {
		this.maxValue = maxValue;
	}

	public int getValue(int x, int y) {
		return getValue(new Point(x,y));
	}

	public int getValue(final Point p) {
		return getCells().get(p).getValue();
	}

	public BitSet getHints(int x, int y, int level) {
		return getHints(new Point(x,y), level);
	}

	public BitSet getHints(Point point, int level) {
		return getCells().get(point).getHints(level);
	}

	public boolean isReadOnly(int x, int y) {
		return isReadOnly(new Point(x,y));
	}
	
	public boolean isReadOnly(Point p) {
		return getCells().get(p).isReadOnly();
	}

	private void times(StringBuilder b, String what, int times) {
		for (int i = 0; i < times; i++) {
			b.append(what);
		}
	}
	
	protected String getBigBorder(int size) {
		StringBuilder b = new StringBuilder();
		b.append("  +");
		times(b, "-",  (2 * size + 1 ) * size + size - 1);
		b.append("+");
		
		return b.toString();
	}
	
	protected String getLittleBorder(int size) {
		StringBuilder b = new StringBuilder();
		b.append("  |");
		times(b, "-", size * 2 + 1);
		for (int i = 1; i < size; i++) {
			b.append("+");
			times(b, "-", size * 2 + 1);
		}
		b.append("|");
		return b.toString();
	}
	
	public abstract void importFile (Path path) 
			throws IOException, IllegalFileFormatException, CellContentException ;
	
	public abstract void exportFile (Path path) throws IOException;

	public abstract void showMarkUp();
	
	public abstract void showHints(int level);
	
	public abstract String toCLIString();
	
	public abstract int getLow();
	public abstract int getHigh();
	
	protected String getValueAsString(int x, int y) {
		int val = getValue(x, y);
		
		return NumberConverter.getValueAsString(val);
	}
	

	/**
	 * Returns true if every cell in this Sudoku 
	 * has a value different than 0
	 * 
	 * @return true if this Sudoku is solved
	 */
	public boolean isSolved() {
        return getCells().values().stream().allMatch(c -> ! c.empty());
	}

	public void setValue(int x, int y, int value)
			throws CellContentException, IllegalCellPositionException {
		setValue(new Point(x,y),value);
	}

	public void setValue(final Point p, int value)
			throws CellContentException, IllegalCellPositionException {	
		if ( value < 0 || value > maxValue ) {
			String error = String.format("Value %d not in range (1-%d)", value, maxValue);
			throw new ValueOutsideRangeException(error);
		}
		if ( getCells().containsKey(p)) {
            Cell cell = getCells().get(p);
            cell.setValue(value);
        }
		else
			throw new IllegalCellPositionException("No cell at " + p);
	}

	protected void reset() {
        getCells().values().forEach(Cell::reset);
	}

	protected void reset(int newMaxValue) {
        maxValue = newMaxValue;
        cells.clear();
    }

	/**
	 * Solves the Sudoku, trying out every combination
	 */
	public boolean solveBruteForce() {
		LinkedList<Cell> emptyCells = getEmpties();
		
		tries = 0;
		boolean result = solveRecursive(emptyCells);
		
		// System.out.println("Needed " + tries + " to solve.");
		return result;
	}

	private LinkedList<Cell> getEmpties() {
		return getCells().values()
				.stream()
				.filter(Cell::empty)
				.sorted( (a,b) -> a.getLocation().compareTo(b.getLocation()) )
				.collect( Collectors.toCollection(LinkedList::new) );
	}

	public abstract void createRandomPuzzle() ;
	
	protected boolean solveRecursive(LinkedList<Cell> empties) {
		// recursive bottom
		if (empties.size() == 0) 
			return true;
	
		tries++;
		LinkedList<Cell> tail = empties; //not a copy

		Cell head = empties.remove();
		
		for (int i : head) {
			try {
				head.setValue(i);
				
				// several orders of magnitude faster to sort Cells by number of remaining entries
				Collections.sort(tail, (Cell c1, Cell c2) -> c1.getMarkUp().cardinality() - c2.getMarkUp().cardinality());
				
				if (solveRecursive(tail)) {
					return true;
				}
	
				head.reset();
			} catch (CellContentException e) {
                System.err.println("Should never happen!");
				continue;
			}
		}
		
		empties.addFirst(head); // add back to the front of the queue
		
		return false; // did not find a solution here, back track
	}

	public int isUnique() {
		LinkedList<Cell> emptyCells = getEmpties();
		
		int solutions = uniqueRecursive(emptyCells, 0);
		
		return solutions;
	}
	
	private int uniqueRecursive(LinkedList<Cell> empties, int solutions) {
		// recursive bottom
		if (empties.size() == 0) {
			return solutions + 1;			
		}
	
		int result = solutions;

		LinkedList<Cell> tail = empties; //not a copy

		Cell head = tail.remove();
		
		for (int i : head) {
			try {
				head.setValue(i);
				
				// several orders of magnitude faster to sort Cells by number of remaining entries
				Collections.sort(tail, (Cell c1, Cell c2) -> c1.getMarkUp().cardinality() - c2.getMarkUp().cardinality());

				result = uniqueRecursive(tail, result);
				if (result > 1) {
					head.reset(); // need to reset, or the puzzle is solved
					return result;
				}
	
				head.reset();
			} catch (CellContentException e) {
				System.err.println("Should never happen!");
				continue;
			}
		}
		
		empties.addFirst(head); // add back to the front of the queue
		
		return result;
	}

	public void createLatinSquare() {

        // idea
        // get top row and left column
        // fill top row with random values
        // fill column with random values (minus the first entry)
        // bruteForce the rest
        List<Integer> seed = new ArrayList<>();
        for (int i = 1; i <= maxValue; i++)
            seed.add(i);

        Collections.shuffle(seed);

        Integer corner = seed.get(0);

        for (int c = 0; c < maxValue; c++) {
            Point p = new Point(c + 1, 1);

            try {
                Cell cell = cells.get(p);
                cell.setValue(seed.get(c));
            } catch (CellContentException e) {
                System.err.println("Shouldn't happen " + e);
            }
        }

        seed.clear();
        for (int i = 1; i <= maxValue; i++) {
            if (i != corner) {
                seed.add(i);
            }
        }

        Collections.shuffle(seed);


        for (int r = 1; r < maxValue; r++) {
            Point p = new Point(1, r + 1);

            try {
                Cell cell = cells.get(p);
                cell.setValue(seed.get(r - 1));
            } catch (CellContentException e) {
                System.err.println("Shouldn't happen " + e);
            }

        }

        solveBruteForce();
    }

	public Map<Point, Cell> getCells() {
		return cells;
	}
}
