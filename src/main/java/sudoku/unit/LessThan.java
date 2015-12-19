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

public class LessThan extends Relation
{
    public LessThan(Cell source, Cell target, int maxValue) {
        super(source, target, maxValue);
    }

    @Override
    public void update(int oldValue, int newValue) throws CellContentException {
        if (newValue != 0) {
            if (! (newValue < getMaxValue()) ) {
                throw new CellContentException("Value " + newValue + " not less than " + getMaxValue() + " in " + this);
            }
        }
    }

    @Override
    public String getRepresentation(Direction direction) {
        if (direction == Direction.Horizontal)
            return "<";
        else if (direction == Direction.Vertical)
            return "^";
        else {
            throw new RuntimeException("Should not be possible");
        }
    }

    @Override
    public BitSet getNumbers() {
        BitSet set = new BitSet(maxValue);

        for (int i = 1; i < getMaxValue(); i++) {
            set.set(i);
        }

        set.flip(1, maxValue + 1); // we want to show the numbers already set, not the ones available
        return set;
    }

    private int getMaxValue() {
        int max = maxValue;
        if (target.getValue() > 0) {
            max = target.getValue();
        }

        // returns one more than possible, easier for getNumbers and update
        return max;
    }

    @Override
    public String toString() {
        return "Relation ( < " + getMaxValue() + " )";
    }

}
