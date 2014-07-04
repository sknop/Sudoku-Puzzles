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
import java.util.List;

import sudoku.Cell;
import sudoku.exceptions.CellContentException;

public class Nonet extends AbstractUnit
{
	BitSet numbers = new BitSet(9);
		
	public Nonet(String position) {
		super(9, position); // a Nonet has exactly 9 cells
	
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
	public String toString() {
		BitSet complement = (BitSet) numbers.clone();
		complement.flip(1, 10);
		return "Nonet " + super.toString() + " : " + numbers + " : " + complement;
	}

	// #############################################################
	
	public static void main(String[] args) throws Exception {
		Nonet nonet = new Nonet("1st row");
		Nonet nonet2 = new Nonet("2nd row");
		
		List<Cell> cells = new ArrayList<>();
		int x = 1;
		for (int y = 1; y <= 9; y++) {
			Cell cell = new Cell(x,y);
			cells.add(cell);
			nonet.addCell(cell);
			nonet2.addCell(cell);
		}
		System.out.println(nonet);
		
		for (int i = 0; i < 9; i++) {
			cells.get(i).setValue(i + 1);
		}
		System.out.println(nonet);
		System.out.println(nonet2);
	}

	@Override
	public BitSet getNumbers() {
		return (BitSet) numbers.clone();
	}
}
