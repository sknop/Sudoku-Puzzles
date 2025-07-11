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
package sudoku;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import sudoku.exceptions.CellContentException;
import sudoku.unit.Constraint;

public class Cell implements Iterable<Integer>, Cloneable
{
	private int value = 0;
	final private Point location;
	private List<Constraint> belongsTo = new ArrayList<>();
	private boolean readOnly = false;
	final private int limit;
	
	public boolean isReadOnly() {
        return readOnly;
	}

	public boolean empty() {
        return (value == 0);
	}
	
	public Cell(int limit, Point location) {
		this.location = location;
		this.limit = limit;
	}

	public Cell(int limit, int x, int y) {
        this(limit, new Point(x,y));
	}

	public void setInitValue(int value) throws CellContentException {
		setValue(value);
		readOnly = true;
	}
	
	public List<Constraint> getConstraints() {
		return Collections.unmodifiableList(belongsTo);
	}
	
	public void makeReadOnly() {
		if (value != 0) {
			readOnly = true;
		}
	}

	public void makeWritable() {
        if (value != 0) {
            readOnly = false;
        }
    }

	public void setValue(int value) throws CellContentException {
		if (readOnly) {
			throw new CellContentException(this + " is read only");
		}
		else if (value > limit) {
			throw new CellContentException("Value " + value + " larger than " + limit);
		}
		else {
            // first verify that the value can be placed at all, no need to know old value at this stage
            for (Constraint u : belongsTo) {
                u.checkUpdate(value);
            }

			for (Constraint u : belongsTo) {
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
			for (Constraint u : belongsTo) {
				markUp.or(u.getNumbers());
			}
			markUp.flip(1, limit + 1);
		}
		
		return markUp;
	}
	
	public BitSet getHints(int level) {
		BitSet markUp = getMarkUp();
		
		if (level > 0) {
			markUp = removeUniques(markUp);
		}
		if (level > 1) {
			markUp = removePairs(markUp);
		}
		if (level > 2) {
			markUp = detectSingle(markUp);
		}
		return markUp;
	}
	
	// Uniques are simple: their markup only contains one value
	// This value cannot show up in any other Cell in the Constraint - job done
	public BitSet removeUniques(BitSet markUp) {
		BitSet copyMarkUp = (BitSet) markUp.clone();
		for (Constraint u : belongsTo) {
			for (Cell c : u.getCells()) {
				if (c != this) {
					BitSet itsMarkUp = c.getMarkUp();
					if (itsMarkUp.cardinality() == 1) {
						int uniqueValue = itsMarkUp.nextSetBit(0);
						copyMarkUp.clear(uniqueValue);
					}
				}
			}
		}
		
		return copyMarkUp;
	}

	// So what exactly is a pair?
	// 	cardinality of 2
	//	an identical pair has to exist in the same Constraint
	public BitSet removePairs(BitSet markUp) {
		BitSet copyMarkUp = (BitSet) markUp.clone();
		for (Constraint u : belongsTo) {
			Cell secondFound = null;
			
			for (Cell c1 : u.getCells()) {
				if (c1 != this && c1 != secondFound) {
					BitSet m1 = c1.getMarkUp();
					
					if (m1.cardinality() == 2) {
						for (Cell c2 : u.getCells()) {
							if (c2 != this && c2 != c1) {
								BitSet m2 = c2.getMarkUp();
								
								if (m1.equals(m2)) {
									secondFound = c2;
									for (int i = m2.nextSetBit(0); i >= 0; i = m2.nextSetBit(i+1)) {
										copyMarkUp.clear(i);
									}
								}
							}
						}
						
					}
				}
			}
		}
		
		return copyMarkUp;
	}

	// Idea:
	// Find any value that is not in another constraint. If not there, it is unique
	public BitSet detectSingle(BitSet markup) {
		BitSet copyMarkup = (BitSet) markup.clone();
		// Find all set bits
		for (int i = copyMarkup.nextSetBit(0); i >= 0; i = copyMarkup.nextSetBit(i + 1)) {
			boolean found = false;

			// look through each constraint to see if that value exists there as well
			for (Constraint u: belongsTo) {
				for (Cell cell : u.getCells()) {
					if (cell != this) {
						if (cell.getMarkUp().get(i)) {
							found = true;
						}
					}
				}
			}

			if (! found) {
				BitSet newMarkup = new BitSet(limit);
				newMarkup.set(i);
				return newMarkup;
			}
		}
		return copyMarkup;
	}

	public void addConstraint(Constraint constraint) {
		belongsTo.add(constraint);
	}

    public boolean removeConstraintWithCheck(Constraint constraint) {
        return !belongsTo.remove(constraint);
    }

	public int getValue() {
        return value;
	}
	
	public Point getLocation() {
        return location;
	}

    public String getValueAsString() {
        char c;

        if (value < 10) {
            c = (char) ('0' + value);
        }
        else {
            c = (char) ('A' + value - 10);
        }

        return String.valueOf(c);
    }

	@Override
	public String toString() {
		return location.toString() + " : " + getValueAsString();
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<>() {
			final BitSet markUp = getMarkUp();
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

    @Override
    public Cell clone() {
        try {
            Cell clone = (Cell) super.clone();

			// reset the Cell, we will be setting the value explicitly

			clone.value = 0;
			clone.readOnly = false;
			clone.belongsTo = new ArrayList<>();

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
