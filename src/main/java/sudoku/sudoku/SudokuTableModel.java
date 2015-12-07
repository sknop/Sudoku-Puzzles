package sudoku.sudoku;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLEngineResult;
import javax.swing.table.AbstractTableModel;
import javax.swing.undo.*;

import sudoku.*;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;
import sudoku.swing.StatusListener;
import sudoku.unit.Constraint;

@SuppressWarnings("serial")
public class SudokuTableModel extends AbstractTableModel implements UndoTableModel
{
	/**
	 * 
	 */
	private final Puzzle puzzle;
	private final int rows;
	private final int cols;
	private final UndoManager undoManager = new UndoManager();
	private boolean isUndoAction = false;
	private Map<Point, Integer> illegalEntries = new HashMap<>();
    private List<StatusListener> listeners = new ArrayList<>();
	
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
//			if (swingSudoku.table.isEditing()) {
//				swingSudoku.table.getCellEditor().stopCellEditing();
//			}
		    setValueAt(value, row,column);
		  }

		// Undo by setting the button state to the opposite value.
		public void undo() throws CannotUndoException {
			super.undo();
//			if (swingSudoku.table.isEditing()) {
//				swingSudoku.table.getCellEditor().stopCellEditing();
//			}
		    setValueAt(previousValue, row, column);
		}
	}
	
	public SudokuTableModel(Puzzle puzzle, int rows, int cols) {
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
	
	@Override
	public CellWrapper getValueAt(int rowIndex, int columnIndex) {
		Point p = new Point(rowIndex + 1, columnIndex + 1);
		Cell cell = puzzle.getCells().get(p);
		
		if (cell.getValue() == 0) {
			int illegal = 0;
			
			if (illegalEntries.containsKey(p)) {
				illegal = illegalEntries.get(p);
			}
			return new CellWrapper(cell, illegal);
		}
		else {
			return new CellWrapper(cell, 0);
		}
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
        }
        else if (!stringValue.equals(previousValue)){
        	SudokuUndo undo = new SudokuUndo(stringValue, previousValue, row, col);
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

}