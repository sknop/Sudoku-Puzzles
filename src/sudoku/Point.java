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

import sudoku.exceptions.IllegalCellPositionException;

public class Point implements Comparable<Point>
{
	private final int x;
	private final int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public static Point createChecked(int x, int y, int min, int max) 
			throws IllegalCellPositionException {
		if (x < min)
			throw new IllegalCellPositionException(String.format("%d less than %d", x, min));
		if (x > max)
			throw new IllegalCellPositionException(String.format("%d larger than %d", x, max));
		if (y < min)
			throw new IllegalCellPositionException(String.format("%d less than %d", y, min));
		if (y > max)
			throw new IllegalCellPositionException(String.format("%d larger than %d", y, max));
		
		return new Point(x,y);
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	

    @Override 
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof Point) {
            Point that = (Point) other;
            result = (this.getX() == that.getX() && this.getY() == that.getY());
        }
        return result;
    }

    @Override 
    public int hashCode() {
        return (41 * (41 + getX()) + getY());
    }

	@Override
	public String toString() {
		return String.format("(%d,%d)", getX(), getY());
	}

	@Override
	public int compareTo(Point o) {
		int result = 0;
		
		if (getX() == o.getX()) 
			result = getY() - o.getY();
		else {
			result = getX() - o.getX();
		}

		return result;
	}
}
