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

import java.util.Iterator;
import java.util.Objects;

public class MarkUp implements Iterable<Integer>, Cloneable
{
	private long bits = 0;
	private final int width;
	
	public MarkUp(int width) {
		this.width = width;
	}

    private MarkUp(int width, long bits) {
        this(width);
        this.bits = bits;
    }

    // equals, hashCode

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject instanceof MarkUp other) {
            return (width == other.width) && (bits == other.bits);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int  hash = 7;
        hash = 31 * hash + width;
        hash = 31 * hash + Objects.hashCode(bits);
        return hash;
    }

 	public void set(int value) {
		assertValue(value);
		
		bits |= 1L << value - 1;
	}

    public void unset(int value) {
        assertValue(value);

        bits &= ~(1L << value - 1);
    }

	public boolean get(int value) {
		assertValue(value);
		
		return (bits & 1L << (value - 1)) > 0;
	}

    // Operations

    public MarkUp or(MarkUp other) {
        return new MarkUp(width, bits | other.bits);
    }

    public MarkUp and(MarkUp other) {
        return new MarkUp(width, bits & other.bits);
    }

    public MarkUp xor(MarkUp other) {
        return new MarkUp(width, bits ^ other.bits);
    }


    public MarkUp complement() {
        return new MarkUp(width, bits ^ MarkUp.allSet(width).bits);
    }

    // Cleanup

    public void clear() {
        bits = 0;
    }

    public int cardinality() {
        long tempBits = bits;
        int count = 0;

        while (tempBits != 0) {
            tempBits =  tempBits & (tempBits - 1);
            count++;
        }
        return count;
    }

    public static MarkUp allSet(int width) {
        long bits = 0;
        for (int i = 0; i < width; i++) {
            bits |= 1L << i;
        }

        return new MarkUp(width, bits);
    }

    private void assertValue(int value) {
        if (value <= 0 || value > width) {
			throw new IllegalArgumentException("MarkUp value " + value + " outside of [1, " + width + "]");
		}
	}
	
	@Override
	public String toString() {
		String s = Long.toBinaryString(bits);

        return "0".repeat(width - s.length()) + s;
	}
	
	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<>() {
            long current = bits;

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

    @Override
    public MarkUp clone() {
        try {
            return (MarkUp) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
