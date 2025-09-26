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
	private final int width;
	
	public MarkUp(int width) {
		this.width = width;
	}

    private MarkUp(int width, long bitset) {
        this(width);
        this.bitset = bitset;
    }

	public void set(int value) {
		assertValue(value);
		
		bitset |= 1L << value - 1;
	}

    public void unset(int value) {
        assertValue(value);

        bitset &= ~(1L << value - 1);
    }

	public boolean get(int value) {
		assertValue(value);
		
		return (bitset & 1L << (value - 1)) > 0;
	}

    public void clear() {
        bitset = 0;
    }

    public MarkUp complement() {
        return new MarkUp(width, bitset ^ MarkUp.allSet(width).bitset);
    }

    public int cardinality() {
        long tempBitset = bitset;
        int count = 0;

        while (tempBitset != 0) {
            tempBitset =  tempBitset & (tempBitset - 1);
            count++;
        }
        return count;
    }

    public static MarkUp allSet(int width) {
        long bitset = 0;
        for (int i = 0; i < width; i++) {
            bitset |= 1L << i;
        }

        return new MarkUp(width, bitset);
    }

    private void assertValue(int value) {
        if (value <= 0 || value > width) {
			throw new IllegalArgumentException("MarkUp value " + value + " outside of [1, " + width + "]");
		}
	}
	
	@Override
	public String toString() {
		String s = Long.toBinaryString(bitset);

        return "0".repeat(width - s.length()) + s;
	}
	
	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<>() {
            long current = bitset;

			@Override
			public boolean hasNext() {
				return (current & -current) > 0;
			}

			@Override
			public Integer next() {
                long next = current & -current;
                int result = Long.numberOfTrailingZeros(next) + 1;
                current &= ~(1L << result - 1);

				return result;
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
