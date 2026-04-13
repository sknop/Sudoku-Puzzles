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

package sudoku.samurai;

import sudoku.swing.PuzzleCellEditor;
import sudoku.swing.Options;
import sudoku.swing.PuzzleTableModel;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;

public class SamuraiCellEditor extends PuzzleCellEditor
{
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

    static class UpAction extends ArrowAction {
        public UpAction(JTable table, PuzzleTableModel tableModel) {
            super(table, tableModel);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            int col = table.getSelectedColumn();
            int newRow = row;
            for (int i = 0; i < 21; i++) {
                newRow = newRow - 1;
                if (newRow < 0) newRow = 20;
                if (!SamuraiTableModel.isVisible(newRow, col)) {
                    if (col < 9 || col > 11)
                        newRow -= 3;
                    else
                        newRow = 14;
                }
                if (tableModel.isCellEditable(newRow, col)) break;
            }
            moveToCell(newRow, col);
        }
    }

    static class DownAction extends ArrowAction {
        public DownAction(JTable table, PuzzleTableModel tableModel) {
            super(table, tableModel);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            int col = table.getSelectedColumn();
            int newRow = row;
            for (int i = 0; i < 21; i++) {
                newRow = newRow + 1;
                if (newRow > 20) newRow = 0;
                if (!SamuraiTableModel.isVisible(newRow, col)) {
                    if (col < 9 || col > 11)
                        newRow += 3;
                    else
                        newRow = 6;
                }
                if (tableModel.isCellEditable(newRow, col)) break;
            }
            moveToCell(newRow, col);
        }
    }

    static class LeftAction extends ArrowAction {
        public LeftAction(JTable table, PuzzleTableModel tableModel) {
            super(table, tableModel);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            int col = table.getSelectedColumn();
            int newColumn = col;
            for (int i = 0; i < 21; i++) {
                newColumn = newColumn - 1;
                if (newColumn < 0) newColumn = 20;
                if (!SamuraiTableModel.isVisible(row, newColumn)) {
                    if (row < 9 || row > 11)
                        newColumn -= 3;
                    else
                        newColumn = 14;
                }
                if (tableModel.isCellEditable(row, newColumn)) break;
            }
            moveToCell(row, newColumn);
        }
    }

    static class RightAction extends ArrowAction {
        public RightAction(JTable table, PuzzleTableModel tableModel) {
            super(table, tableModel);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            int col = table.getSelectedColumn();
            int newColumn = col;
            for (int i = 0; i < 21; i++) {
                newColumn = newColumn + 1;
                if (newColumn > 20) newColumn = 0;
                if (!SamuraiTableModel.isVisible(row, newColumn)) {
                    if (row < 9 || row > 11)
                        newColumn += 3;
                    else
                        newColumn = 6;
                }
                if (tableModel.isCellEditable(row, newColumn)) break;
            }
            moveToCell(row, newColumn);
        }
    }

    @Override
    public ArrowAction getUpAction(JTable table, PuzzleTableModel tableModel) {
        return new UpAction(table, tableModel);
    }

    @Override
    public ArrowAction getDownAction(JTable table, PuzzleTableModel tableModel) {
        return new DownAction(table, tableModel);
    }

    @Override
    public ArrowAction getLeftAction(JTable table, PuzzleTableModel tableModel) {
        return new LeftAction(table, tableModel);
    }

    @Override
    public ArrowAction getRightAction(JTable table, PuzzleTableModel tableModel) {
        return new RightAction(table, tableModel);
    }
}

