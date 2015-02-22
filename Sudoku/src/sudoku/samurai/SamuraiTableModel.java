package sudoku.samurai;

import java.awt.Toolkit;

import javax.swing.table.AbstractTableModel;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import sudoku.Cell;
import sudoku.CellWrapper;
import sudoku.Point;
import sudoku.UndoTableModel;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;
import sudoku.unit.Unit;

@SuppressWarnings("serial")
public class SamuraiTableModel extends AbstractTableModel implements UndoTableModel
{
    /**
     *
     */
    private final SwingSamurai swingSamurai;
    private final int rows;
    private final int cols;
    private final UndoManager undoManager = new UndoManager();
    private boolean isUndoAction = false;

    class SudokuUndo extends AbstractUndoableEdit {
        private String value;
        private String previousValue;
        private int row;
        private int column;

        public SudokuUndo(String value, String previousValue, int row, int column) {
            this.value = value;
            this.previousValue = previousValue;
            this.row = row;
            this.column = column;
        }

        public String getPresentationName() {
            return "Sudoku '" + value + "'['" + previousValue + "'] at (" + row + "," + column + ")";
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

    public SamuraiTableModel(SwingSamurai swingSamurai, int rows, int cols) {
        this.swingSamurai = swingSamurai;
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

    @Override
    public CellWrapper getValueAt(int rowIndex, int columnIndex) {
        Point p = new Point(rowIndex + 1, columnIndex + 1);
        Cell cell = this.swingSamurai.getCells().get(p);

        if (cell.getValue() == 0) {
            int illegal = 0;

            if (this.swingSamurai.illegalEntries.containsKey(p)) {
                illegal = this.swingSamurai.illegalEntries.get(p);
            }
            return new CellWrapper(cell, illegal);
        }
        else {
            return new CellWrapper(cell, 0);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (this.swingSamurai.isSolved())
            return false;

        Point p = new Point(rowIndex + 1, columnIndex + 1);
        boolean isReadOnly = this.swingSamurai.isReadOnly(p);
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
        // boolean moveFocus = false;

        if (!stringValue.isEmpty()) {
            intValue = Integer.parseInt(stringValue);
        }
        else {
            // for easier comparing to existing cell value below
            stringValue = "0";
        }

        CellWrapper wrapper = getValueAt(row, col);
        String previousValue = Integer.toString(wrapper.getVisibleValue());

//        System.out.println("setValueAt : " + value + " previous " + previousValue);

        if (isUndoAction) {
            isUndoAction = false;
            // moveFocus = true;
        }
        else if (!stringValue.equals(previousValue)){
            SudokuUndo undo = new SudokuUndo(stringValue, previousValue, row, col);
            undoManager.addEdit(undo);
//        	System.out.println("Added " + undo);
        }
        else if (stringValue.equals("0")) {
            // when gaining focus on an empty cell, Swing tries to reset the value. Ignore it
            return;
        }

        try {
            if (this.swingSamurai.illegalEntries.containsKey(p)) {
                this.swingSamurai.illegalEntries.remove(p);
            }
            this.swingSamurai.setValue(p, intValue);
        } catch (IllegalCellPositionException e) {
            System.err.println("Should never happen " + e);
        } catch (CellContentException e) {
            this.swingSamurai.illegalEntries.put(p, intValue);
            try {
                this.swingSamurai.setValue(p, 0);
            } catch (IllegalCellPositionException | CellContentException e1) {
                System.err.println("Should never happen " + e);
            }
        }

        fireTableCellUpdated(row, col);

        Cell cell = wrapper.getCell();
        for (Unit u : cell.getUnits()) {
            for (Cell c : u.getCells()) {
                Point point = c.getLocation();
                // Sudoku is 1 based, JTable is 0 based, so need to remove 1
                fireTableCellUpdated(point.getX() - 1, point.getY() - 1);
            }
        }

//        if (moveFocus) {
//        	swingSamurai.table.changeSelection(row, col, false, false);
//        	System.out.println("Moved focus");
//        }

        setStatus();
    }

    void setStatus() {
        if (this.swingSamurai.isSolved()) {
            this.swingSamurai.solved.setText("Solved!");
        }
        else {
            int solutions = this.swingSamurai.isUnique();
            if (solutions == 1) {
                this.swingSamurai.solved.setText("Unsolved");
            }
            else if (solutions == 0) {
                this.swingSamurai.solved.setText("No solutions");
            }
            else {
                this.swingSamurai.solved.setText("Not unique");
            }
        }
    }
}