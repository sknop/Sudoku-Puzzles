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
import sudoku.swing.StatusListener;
import sudoku.unit.Constraint;

@SuppressWarnings("serial")
public class SamuraiTableModel extends AbstractTableModel implements UndoTableModel
{
    /**
     *
     */
    private final Puzzle puzzle;
    private final int rows;
    private final int cols;
    private final UndoManager undoManager = new UndoManager();
    private Map<Point, Integer> illegalEntries = new HashMap<>();
    private boolean isUndoAction = false;
    private List<StatusListener> listeners = new ArrayList<>();

    class SamuraiUndo extends AbstractUndoableEdit {
        private String value;
        private String previousValue;
        private int row;
        private int column;

        public SamuraiUndo(String value, String previousValue, int row, int column) {
            this.value = value;
            this.previousValue = previousValue;
            this.row = row;
            this.column = column;
        }

        public String getPresentationName() {
            return "Samurai '" + value + "'['" + previousValue + "'] at (" + row + "," + column + ")";
        }

        @Override
        public String toString() {
            return getPresentationName();
        }

        public void redo() throws CannotRedoException {
            super.redo();
//			if (swingSamurai.table.isEditing()) {
//				swingSamurai.table.getCellEditor().stopCellEditing();
//			}
            setValueAt(value, row,column);
        }

        // Undo by setting the button state to the opposite value.
        public void undo() throws CannotUndoException {
            super.undo();
//			if (swingSamurai.table.isEditing()) {
//				swingSamurai.table.getCellEditor().stopCellEditing();
//			}
            setValueAt(previousValue, row, column);
        }
    }

    public SamuraiTableModel(Puzzle puzzle, int rows, int cols) {
        this.puzzle = puzzle;
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public int getRowCount() {
        return rows;
    }

    @Override
    public int getColumnCount() {
        return cols;
    }

    @Override
    public void undo() {
        isUndoAction = true;
        try {
            undoManager.undo();
        }
        catch(CannotUndoException e) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    @Override
    public void redo() {
        isUndoAction = true;
        try {
            undoManager.redo();
        }
        catch(CannotRedoException e) {
            Toolkit.getDefaultToolkit().beep();
        }
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

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (puzzle.isSolved())
            return false;

        Point p = new Point(rowIndex + 1, columnIndex + 1);
        boolean isReadOnly = puzzle.isReadOnly(p);
        return !isReadOnly;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return CellWrapper.class;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        Point p = new Point(row + 1, col + 1);
        String stringValue = (String) value;
        int intValue = 0;

        if (!stringValue.isEmpty()) {
            intValue = Integer.parseInt(stringValue);
        }
        else {
            // for easier comparing to existing cell value below
            stringValue = "0";
        }

        CellWrapper wrapper = getValueAt(row, col);
        String previousValue = Integer.toString(wrapper.getVisibleValue());

        if (isUndoAction) {
            isUndoAction = false;
            // moveFocus = true;
        }
        else if (!stringValue.equals(previousValue)){
            SamuraiUndo undo = new SamuraiUndo(stringValue, previousValue, row, col);
            undoManager.addEdit(undo);
        }
        else if (stringValue.equals("0")) {
            // when gaining focus on an empty cell, Swing tries to reset the value. Ignore it
            return;
        }

        try {
            if (illegalEntries.containsKey(p)) {
                illegalEntries.remove(p);
            }
            puzzle.setValue(p, intValue);
        } catch (IllegalCellPositionException e) {
            System.err.println("Should never happen " + e);
        } catch (CellContentException e) {
            illegalEntries.put(p, intValue);
            try {
                puzzle.setValue(p, 0);
            } catch (IllegalCellPositionException | CellContentException e1) {
                System.err.println("Should never happen " + e);
            }
        }

        fireTableCellUpdated(row, col);

        Cell cell = wrapper.getCell();
        for (Constraint u : cell.getConstraints()) {
            for (Cell c : u.getCells()) {
                Point point = c.getLocation();
                // Sudoku is 1 based, JTable is 0 based, so need to remove 1
                fireTableCellUpdated(point.getX() - 1, point.getY() - 1);
            }
        }

        listeners.forEach(StatusListener::statusChanged);
    }


    public void addListener(StatusListener listener) {
        listeners.add(listener);
    }

    public boolean removeListener(StatusListener listener) {
        return listeners.remove(listener);
    }

    public void clearIllegal() {
        illegalEntries.clear();
    }

    public boolean anyIllegalValues() {
        return (illegalEntries.size() > 0);
    }

}