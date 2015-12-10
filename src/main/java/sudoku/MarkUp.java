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

import java.util.Iterator;

// TODO This class is not finished yet, still using BitMap

public class MarkUp implements Iterable<Integer>
{
	private long bitset = 0;
	private int width;
	
	public MarkUp(int width) {
		this.width = width;
	}
	
	public void set(int value) {
		assertValue(value);
		
		bitset |= 1L << value;
	}
	
	public boolean get(int value) {
		assertValue(value);
		
		return (bitset & 1L << value) > 0;
	}
	
	private void assertValue(int value) {
		if (value > width) {
			throw new RuntimeException("MarkUp value " + value + " exceeded width " + width);
		}
	}
	
	@Override
	public String toString() {
		return Long.toBinaryString(bitset);
	}
	
	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public Integer next() {
				return null;
			}
			
		};
	}

	public static void main(String[] args) {
		MarkUp m = new MarkUp(9);
		System.out.println(m);
		
		m.set(1);
		m.set(3);
		m.set(9);
		
		if (m.get(3)) {
			System.out.println("yep");
		}
		if (m.get(4)) {
			System.out.println("nope");
		}

		try {
			m.set(10);
		}
		catch (Throwable e) {
			System.out.println("Expected");
		}
		System.out.println(m);
		
	}

}
