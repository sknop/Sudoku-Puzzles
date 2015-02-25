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

    @Override
    UpAction getUpAction(JTable table, int row, int column) {
        return new UpAction(table, row, column);
    }

    @Override
    DownAction getDownAction(JTable table, int row, int column) {
        return new DownAction(table, row, column);
    }

    @Override
    LeftAction getLeftAction(JTable table, int row, int column) {
        return new LeftAction(table, row, column);
    }

    @Override
    RightAction getRightAction(JTable table, int row, int column) {
        return new RightAction(table, row, column);
    }

}

