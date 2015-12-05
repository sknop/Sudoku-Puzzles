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
import sudoku.exceptions.AddCellException;
import sudoku.exceptions.CellContentException;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public abstract class Relation implements Constraint
{
    protected Cell source;
    protected Cell target;
    protected int maxValue;

    public Relation(Cell source, Cell target, int maxValue) {
        this.source = source;
        this.target = target;
        this.maxValue = maxValue;
    }

    /**
     * We only report the target cell
     *
     * @return List containing the target cell
     */
    @Override
    public List<Cell> getCells() {
        ArrayList<Cell> array = new ArrayList<>();
        array.add(target);

        return array;
    }

    public Cell getSource() {
        return source;
    }
}
