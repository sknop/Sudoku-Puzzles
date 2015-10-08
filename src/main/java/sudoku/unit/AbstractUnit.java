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
package sudoku.unit;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import sudoku.Cell;
import sudoku.exceptions.AddCellException;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.TooManyCellsException;;

public abstract class AbstractUnit implements Unit
{
	protected List<Cell> cells;
	protected int maxCells;
	protected String position;
	protected BitSet numbers;
	
	protected AbstractUnit(int size, String position) {
		this.maxCells = size;
		this.cells = new ArrayList<>(size);
		this.position = position;
		
		this.numbers = new BitSet(size);
	}
	
	@Override
	public void addCell(Cell cell) throws AddCellException {
		if ( cells.size() == maxCells ) {
			throw new TooManyCellsException("Exceeded " + maxCells + " entries");
		}
		cells.add(cell);
		cell.addToUnit(this);
	}

	@Override
	public List<Cell> getCells() {
		return Collections.unmodifiableList(cells);
	}
	
	@Override
	public String toString() {
		return String.format("(%s) %s", position, cells.toString());
	}

	@Override
	public void update(int oldValue, int newValue) throws CellContentException {
		if ( oldValue != 0 ) {
			// need to remove the old value
			numbers.clear(oldValue);
		}
		
		if ( newValue != 0) {
			if ( numbers.get(newValue) ) {
				throw new CellContentException("Value " + newValue + " already exists in " + this);
			}
			else {
				numbers.set(newValue);
			}
		}
	}

	@Override
	public BitSet getNumbers() {
		return (BitSet) numbers.clone();
	}
}
