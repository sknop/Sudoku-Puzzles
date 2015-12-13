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
    protected String formatMarkup(BitSet set) {
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

    static class UpAction extends ArrowAction {

        public UpAction(JTable table, int row, int column) {
            super(table, row, column);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int newRow = row - 1;
            if (newRow < 0) newRow = 15;
            moveToCell(newRow, column);
        }
    }
    static class DownAction extends ArrowAction {
        public DownAction(JTable table, int row, int column) {
            super(table, row, column);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            int newRow = row + 1;
            if (newRow > 15) newRow = 0;
            moveToCell(newRow, column);
        }
    }
    static class LeftAction extends ArrowAction {
        public LeftAction(JTable table, int row, int column) {
            super(table, row, column);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            int newColumn = column - 1;
            if (newColumn < 0) newColumn = 15;
            moveToCell(row, newColumn);
        }
    }
    static class RightAction extends ArrowAction {
        public RightAction(JTable table, int row, int column) {
            super(table, row, column);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int newColumn = column + 1;
            if (newColumn > 15) newColumn = 0;
            moveToCell(row, newColumn);
        }
    }

    @Override
    public ArrowAction getUpAction(JTable table, int row, int column) {
        return new UpAction(table, row, column);
    }

    @Override
    public ArrowAction getDownAction(JTable table, int row, int column) {
        return new DownAction(table, row, column);
    }

    @Override
    public ArrowAction getLeftAction(JTable table, int row, int column) {
        return new LeftAction(table, row, column);
    }

    @Override
    public ArrowAction getRightAction(JTable table, int row, int column) {
        return new RightAction(table, row, column);
    }
}
