/*
 * Copyright (c) 2015 Sven Erik Knop.
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
 *  Contributors:
 *      2015 - Sven Erik Knop - initial API and implementation
 *
 */

package sudoku.unit;

import sudoku.Cell;
import sudoku.exceptions.CellContentException;

import java.util.BitSet;

public class GreaterThan extends Relation
{
    public GreaterThan(Cell source, Cell target, int maxValue) {
        super(source, target, maxValue);
    }

    @Override
    public void update(int oldValue, int newValue) throws CellContentException {
        if (newValue != 0) {
            if (! (newValue > getMinValue()) ) {
                throw new CellContentException("Value " + newValue + " not more than " + getMinValue() + " in " + this);
            }
        }
    }

    @Override
    public BitSet getNumbers() {
        BitSet set = new BitSet(maxValue);

        for (int i = getMinValue() + 1; i < maxValue + 1; i++) {
            set.set(i);
        }

        set.flip(1, maxValue + 1); // we want to show the numbers already set, not the ones available
        return set;
    }

    private int getMinValue() {
        int min = 1;
        if (target.getValue() > 0) {
            min = target.getValue();
        }

        // returns one less than possible, easier for getNumbers and update
        return min;
    }

    @Override
    public String toString() {
        return "Relation ( > " + getMinValue() + " )";
    }

}
