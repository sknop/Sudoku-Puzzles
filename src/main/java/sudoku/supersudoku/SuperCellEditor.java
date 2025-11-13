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

package sudoku.supersudoku;


import sudoku.MarkUp;
import sudoku.NumberConverter;
import sudoku.swing.FieldLimit;
import sudoku.swing.Options;
import sudoku.swing.PuzzleCellEditor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.BitSet;

public class SuperCellEditor extends PuzzleCellEditor
{
    public SuperCellEditor(Options options) {
        super(options);
    }

    @Override
    protected FieldLimit createFieldLimit() {
        return new FieldLimit(16);
    }

    @Override
    protected String formatMarkup(MarkUp set) {
        StringBuilder b = new StringBuilder();
        if (set.cardinality() > 0) {
            b.append("<html>");
            for (int i = 1; i <= 16; i++) {
                b.append( set.get(i) ? NumberConverter.getValueAsString(i) : " ");
            }
            b.append("</html>");
        }
        return b.toString();
    }

    @Override
    public ArrowAction getUpAction(JTable table, int row, int column) {
        return new UpAction(table, row, column, 15);
    }

    @Override
    public ArrowAction getDownAction(JTable table, int row, int column) {
        return new DownAction(table, row, column, 15);
    }

    @Override
    public ArrowAction getLeftAction(JTable table, int row, int column) {
        return new LeftAction(table, row, column, 15);
    }

    @Override
    public ArrowAction getRightAction(JTable table, int row, int column) {
        return new RightAction(table, row, column, 15);
    }
}
