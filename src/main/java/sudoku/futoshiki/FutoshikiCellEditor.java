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
package sudoku.futoshiki;

import sudoku.MarkUp;
import sudoku.swing.FieldLimit;
import sudoku.swing.Options;
import sudoku.swing.PuzzleCellEditor;

import javax.swing.*;
import java.awt.*;

public class FutoshikiCellEditor extends PuzzleCellEditor {

    // Updated before each render/edit call by reading from the table model
    private int maxValue = 5;

    public FutoshikiCellEditor(Options options) {
        super(options);
    }

    private void updateMaxValue(JTable table) {
        if (table.getModel() instanceof FutoshikiTableModel ftm) {
            this.maxValue = ftm.getMaxValue();
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        updateMaxValue(table);
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        if (!FutoshikiTableModel.isNumberCell(row, column)) return null;
        updateMaxValue(table);
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    protected FieldLimit createFieldLimit() {
        return new FieldLimit(maxValue);
    }

    @Override
    protected String formatMarkup(MarkUp set) {
        StringBuilder b = new StringBuilder();
        if (set.cardinality() > 0) {
            b.append("<html>");
            for (int i = 1; i <= maxValue; i++) {
                b.append(set.get(i) ? i : " ");
            }
            b.append("</html>");
        }
        return b.toString();
    }
}
