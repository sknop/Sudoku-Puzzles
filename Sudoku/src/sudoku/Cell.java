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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import sudoku.exceptions.AddCellException;
import sudoku.exceptions.CellContentException;
import sudoku.unit.Unit;

public class Cell implements Iterable<Integer>
{
	private int value = 0;
	private Point location;
	private List<Unit> belongsTo = new ArrayList<>();
	private boolean readOnly = false;
	
	public boolean isReadOnly() {
		return readOnly;
	}

	public boolean empty() {
		return (value == 0);
	}
	
	public Cell(Point location) {
		this.location = location;
	}

	public Cell(int x, int y) {
		this.location = new Point(x,y);
	}

	public void setInitValue(int value) throws CellContentException {
		setValue(value);
		readOnly = true;
	}
	
	public List<Unit> getUnits() {
		return Collections.unmodifiableList(belongsTo);
	}
	
	public void makeReadOnly() {
		if (value != 0) {
			readOnly = true;
		}
	}
	
	public void setValue(int value) throws CellContentException {
		if (readOnly) {
			throw new CellContentException(this.toString() + " is read only");
		}
		else {
			for (Unit u : belongsTo) {
				u.update(this.value, value);
			}
			
			this.value = value;
		}
	}
	
	public void reset() {
		readOnly = false;
		try {
			setValue(0);
		} catch (CellContentException e) {
			System.err.println("Should not happen" + e);
		}
	}
	
	public BitSet getMarkUp() {
		BitSet markUp = new BitSet();
		
		if (value == 0) {			
			for (Unit u : belongsTo) {
				markUp.or(u.getNumbers());
			}
			markUp.flip(1, 10);
		}
		
		return markUp;
	}
	
	public BitSet getHints(int level) {
		BitSet markUp = getMarkUp();
		
		if (level > 0) {
			markUp = removeUniques(markUp, level);
		}
		return markUp;
	}
	
	private BitSet removeUniques(BitSet markUp, int level) {
		System.out.println("RemoveUniques " + level);
		for (Unit u : belongsTo) {
			for (Cell c : u.getCells()) {
				if (c != this) {
					BitSet itsMarkUp = c.getHints(level - 1);
					if (itsMarkUp.cardinality() == 1) {
						int uniqueValue = itsMarkUp.nextSetBit(0);
						System.out.println("Unique : " + uniqueValue + " @ " + c);
						markUp.clear(uniqueValue);
					}
				}
			}
		}
		return markUp;
	}
	
	public void addToUnit(Unit unit) throws AddCellException {
		belongsTo.add(unit);
	}
	
	public int getValue() {
		return value;
	}
	
	public Point getLocation() {
		return location;
	}
	
	@Override
	public String toString() {
		return location.toString() + " : " + value;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			BitSet  markUp = getMarkUp();
			int nextValue = markUp.nextSetBit(0);
			
			@Override
			public boolean hasNext() {
				return nextValue >= 0;
			}

			@Override
			public Integer next() {
				int result = nextValue;
				nextValue = markUp.nextSetBit(nextValue + 1);
				
				return result;
			}
			
		};
	}
}
