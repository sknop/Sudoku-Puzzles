package sudoku.sudoku;

import javax.swing.table.AbstractTableModel;

import sudoku.Cell;
import sudoku.CellWrapper;
import sudoku.Point;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;
import sudoku.unit.Unit;

@SuppressWarnings("serial")
class SudokuTableModel extends AbstractTableModel
{
	/**
	 * 
	 */
	private final SwingSudoku swingSudoku;
	private final int rows;
	private final int cols;
	
	public SudokuTableModel(SwingSudoku swingSudoku, int rows, int cols) {
		this.swingSudoku = swingSudoku;
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
	public CellWrapper getValueAt(int rowIndex, int columnIndex) {
		Point p = new Point(rowIndex + 1, columnIndex + 1);
		Cell cell = this.swingSudoku.getCells().get(p);
		
		if (cell.getValue() == 0) {
			int illegal = 0;
			
			if (this.swingSudoku.illegalEntries.containsKey(p)) {
				illegal = this.swingSudoku.illegalEntries.get(p);
			}
			return new CellWrapper(cell, illegal);
		}
		else {
			return new CellWrapper(cell, 0);
		}
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (this.swingSudoku.isSolved()) 
			return false;
		
        Point p = new Point(rowIndex + 1, columnIndex + 1);
		boolean isReadOnly = this.swingSudoku.isReadOnly(p);
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

        try {
			if (this.swingSudoku.illegalEntries.containsKey(p)) {
				this.swingSudoku.illegalEntries.remove(p);
			}
			this.swingSudoku.setValue(p, intValue);
		} catch (IllegalCellPositionException e) {
			System.err.println("Should never happen " + e);
		} catch (CellContentException e) {
			this.swingSudoku.illegalEntries.put(p, intValue);
			try {
				this.swingSudoku.setValue(p, 0);
			} catch (IllegalCellPositionException | CellContentException e1) {
				System.err.println("Should never happen " + e);
			}
		}
        
        fireTableCellUpdated(row, col);
        
        Cell cell = this.swingSudoku.getCells().get(p);
        for (Unit u : cell.getUnits()) {
        	for (Cell c : u.getCells()) {
        		Point point = c.getLocation();
        		// Sudoku is 1 based, JTable is 0 based, so need to remove 1
        		fireTableCellUpdated(point.getX() - 1, point.getY() - 1);
        	}
        }
        
        setStatus();
    }

	void setStatus() {
		if (this.swingSudoku.isSolved()) {
        	this.swingSudoku.solved.setText("Solved!");
        }
        else {
        	int solutions = this.swingSudoku.isUnique();
        	if (solutions == 1) {
        		this.swingSudoku.solved.setText("Unsolved");
        	}
        	else if (solutions == 0) {
        		this.swingSudoku.solved.setText("No solutions");
        	}
        	else {
        		this.swingSudoku.solved.setText("Not unique");
        	}
        }
	}
}