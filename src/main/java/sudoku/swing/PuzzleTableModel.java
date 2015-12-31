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

package sudoku.swing;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;
import javax.swing.undo.*;

import sudoku.*;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;
import sudoku.swing.StatusListener;
import sudoku.unit.Constraint;

@SuppressWarnings("serial")
public class PuzzleTableModel extends AbstractTableModel implements UndoTableModel
{
	protected final Puzzle puzzle;
	protected final int rows;
	protected final int cols;
	protected final UndoManager undoManager = new UndoManager();
	protected boolean isUndoAction = false;
	protected Map<Point, Integer> illegalEntries = new HashMap<>();
	protected List<StatusListener> listeners = new ArrayList<>();
	
	protected class PuzzleUndo extends AbstractUndoableEdit {
		private String value;
		private String previousValue;
		private int row;
		private int column;
		
		public PuzzleUndo(String value, String previousValue, int row, int column) {
			this.value = value;
			this.previousValue = previousValue;
			this.row = row;
			this.column = column;
		}
		
		public String getPresentationName() {
		    return "Puzzle '" + value + "'['" + previousValue + "'] at (" + row + "," + column + ")";
		}
		
		@Override
		public String toString() {
			return getPresentationName();
		}
		
		public void redo() throws CannotRedoException {
		    super.redo();
		    setValueAt(value, row,column);
		  }

		// Undo by setting the button state to the opposite value.
		public void undo() throws CannotUndoException {
			super.undo();
		    setValueAt(previousValue, row, column);
		}
	}
	
	public PuzzleTableModel(Puzzle puzzle, int rows, int cols) {
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
        	intValue = NumberConverter.getCharAsValue(stringValue.charAt(0));
        }
        else {
        	// for easier comparing to existing cell value below
        	stringValue = "0";
        }

        CellWrapper wrapper = getValueAt(row, col);
        String previousValue = Integer.toString(wrapper.getVisibleValue());

		if (previousValue.equals(stringValue)) {
            // nothing to do here, just walked in an out
            return;
        }

        if (isUndoAction) {
        	isUndoAction = false;
        }
        else if (!stringValue.equals(previousValue)){
        	PuzzleUndo undo = new PuzzleUndo(stringValue, previousValue, row, col);
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