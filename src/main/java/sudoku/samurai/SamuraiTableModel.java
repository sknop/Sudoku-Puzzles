/*
 * ******************************************************************************
 *  * Copyright (c) 2015 Sven Erik Knop.
 *  * Licensed under the EUPL V.1.1
 *  *
 *  * This Software is provided to You under the terms of the European
 *  * Union Public License (the "EUPL") version 1.1 as published by the
 *  * European Union. Any use of this Software, other than as authorized
 *  * under this License is strictly prohibited (to the extent such use
 *  * is covered by a right of the copyright holder of this Software).
 *  *
 *  * This Software is provided under the License on an "AS IS" basis and
 *  * without warranties of any kind concerning the Software, including
 *  * without limitation merchantability, fitness for a particular purpose,
 *  * absence of defects or errors, accuracy, and non-infringement of
 *  * intellectual property rights other than copyright. This disclaimer
 *  * of warranty is an essential part of the License and a condition for
 *  * the grant of any rights to this Software.
 *  *
 *  * For more details, see http://joinup.ec.europa.eu/software/page/eupl.
 *  *
 *  * Contributors:
 *  *     2015 - Sven Erik Knop - initial API and implementation
 *  ******************************************************************************
 */

package sudoku.samurai;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import sudoku.*;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;
import sudoku.swing.PuzzleTableModel;
import sudoku.swing.StatusListener;
import sudoku.unit.Constraint;

@SuppressWarnings("serial")
public class SamuraiTableModel extends PuzzleTableModel
{

    public SamuraiTableModel(Puzzle puzzle, int rows, int cols) {
        super(puzzle, rows, cols);
    }

    public static boolean isVisible(int rowIndex, int columnIndex) {
        // for sanity and to keep the code identical to Cell-based counting (starting at 1)

        rowIndex += 1;
        columnIndex += 1;

        if ((rowIndex > 9) && (rowIndex < 13)) {
            if ((columnIndex<7) || (columnIndex > 15)) {
                return false;
            }
        }
        if ((columnIndex > 9) && (columnIndex < 13)) {
            if ((rowIndex<7) || (rowIndex > 15)) {
                return false; // left and right gaps
            }
        }

        return true;
    }

    @Override
    public CellWrapper getValueAt(int rowIndex, int columnIndex) {
        if (isVisible(rowIndex, columnIndex)) {
            Point p = new Point(rowIndex + 1, columnIndex + 1);
            Cell cell = puzzle.getCells().get(p);

            if (cell.getValue() == 0) {
                int illegal = 0;

                if (illegalEntries.containsKey(p)) {
                    illegal = illegalEntries.get(p);
                }
                return new CellWrapper(cell, illegal);
            } else {
                return new CellWrapper(cell, 0);
            }
        }

        return null;
    }
}