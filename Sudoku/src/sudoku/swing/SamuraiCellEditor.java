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
 *  *     2014 - Sven Erik Knop - initial API and implementation
 *  ******************************************************************************
 */

package sudoku.swing;

import sudoku.samurai.SamuraiTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SamuraiCellEditor extends CellEditor {
    public SamuraiCellEditor(Options options) {
        super(options);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column)
    {
        if (SamuraiTableModel.isVisible(row, column) ) {
            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        }

        return null;
    }

    class UpAction extends ArrowAction {

        public UpAction(JTable table, int row, int column) {
            super(table, row, column);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int newRow = row - 1;
            if (newRow < 0) newRow = 20;

            if (!SamuraiTableModel.isVisible(newRow, column)) {
                newRow -= 3;
            }
            moveToCell(newRow, column);
        }
    }
    class DownAction extends ArrowAction {
        public DownAction(JTable table, int row, int column) {
            super(table, row, column);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            int newRow = row + 1;
            if (newRow > 20) newRow = 0;

            if (!SamuraiTableModel.isVisible(newRow, column)) {
                newRow += 3;
            }
            moveToCell(newRow, column);
        }
    }
    class LeftAction extends ArrowAction {
        public LeftAction(JTable table, int row, int column) {
            super(table, row, column);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            int newColumn = column - 1;
            if (newColumn < 0) newColumn = 20;

            if (!SamuraiTableModel.isVisible(row, newColumn)) {
                newColumn -= 3;
            }
            moveToCell(row, newColumn);
        }
    }
    class RightAction extends ArrowAction {
        public RightAction(JTable table, int row, int column) {
            super(table, row, column);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int newColumn = column + 1;
            if (newColumn > 20) newColumn = 0;

            if (!SamuraiTableModel.isVisible(row, newColumn)) {
                newColumn += 3;
            }
            moveToCell(row, newColumn);
        }
    }
    @Override
    ArrowAction getUpAction(JTable table, int row, int column) {
        return new UpAction(table, row, column);
    }

    @Override
    ArrowAction getDownAction(JTable table, int row, int column) {
        return new DownAction(table, row, column);
    }

    @Override
    ArrowAction getLeftAction(JTable table, int row, int column) {
        return new LeftAction(table, row, column);
    }

    @Override
    ArrowAction getRightAction(JTable table, int row, int column) {
        return new RightAction(table, row, column);
    }

}

