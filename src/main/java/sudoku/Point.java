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

public record Point(int x, int y) implements Comparable<Point> {

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

        return new Point(x, y);
    }


    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof Point(int x1, int y1)) {
            result = (this.x() == x1 && this.y() == y1);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return (41 * (41 + x()) + y());
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)", x(), y());
    }

    @Override
    public int compareTo(Point o) {
        int result = 0;

        if (x() == o.x())
            result = y() - o.y();
        else {
            result = x() - o.x();
        }

        return result;
    }
}
