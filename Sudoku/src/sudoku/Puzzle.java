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

import java.util.BitSet;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;
import sudoku.exceptions.ValueOutsideRangeException;

public abstract class Puzzle
{
	protected int maxValue;
	protected final Map<Point, Cell> cells = new HashMap<>();

	public Puzzle(int maxValue) {
		this.maxValue = maxValue;
	}

	public int getValue(int x, int y) {
		return getValue(new Point(x,y));
	}

	public int getValue(final Point p) {
		return cells.get(p).getValue();
	}

	public BitSet getMarkUp(int x, int y) {
		return getMarkUp(new Point(x,y));
	}

	public BitSet getMarkUp(Point point) {
		return cells.get(point).getMarkUp();
	}

	/**
	 * Returns true if every cell in this Sudoku 
	 * has a value different than 0
	 * 
	 * @return true if this Sudoku is solved
	 */
	public boolean isSolved() {
		for (Cell cell : cells.values()) {
			if (cell.empty()) {
				return false;
			}
		}
		
		return true;
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
		if ( cells.containsKey(p))
			cells.get(p).setValue(value);
		else
			throw new IllegalCellPositionException("No cell at " + p);
	}

	protected void reset() {
		for (Cell c : cells.values()) {
			c.reset();
		}
	}

	/**
	 * Solves the Sudoku, trying out every combination
	 */
	public boolean solveBruteForce() {
		Deque<Cell> emptyCells = cells.values()
				.stream()
				.filter( c -> c.empty() )
				.sorted( (a,b) -> a.getLocation().compareTo(b.getLocation()) )
				.collect( Collectors.toCollection(LinkedList::new) );
				
		return solveRecursive(emptyCells);
	}

	public boolean createRandomPuzzle() {
		reset(); // first, clear out any existing entries
		
		LinkedList<Cell> random = cells.values()
				.parallelStream()
				.collect( Collectors.toCollection(LinkedList::new));
		Collections.shuffle(random);
		
		return solveRecursive(random);
	}
	
	private boolean solveRecursive(Deque<Cell> empties) {
		// recursive bottom
		if (empties.size() == 0) 
			return true;
	
		Deque<Cell> tail = empties; //not a copy
		
		Cell head = tail.remove();
		
		BitSet markUp = head.getMarkUp();
		for (int i = markUp.nextSetBit(0); i >= 0; i = markUp.nextSetBit(i+1)) {
			try {
				head.setValue(i);
				
				if (solveRecursive(tail)) {
					return true;
				}
	
				head.reset();
			} catch (CellContentException e) {
				// now violates a condition, therefore incorrect entry
				// try next value
				continue;
			}
		}
		
		empties.addFirst(head); // add back to the front of the queue
		
		return false; // did not find a solution here, back track
	}

}