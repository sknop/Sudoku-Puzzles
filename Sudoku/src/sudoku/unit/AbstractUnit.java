package sudoku.unit;

import java.util.ArrayList;
import java.util.List;

import sudoku.Cell;
import sudoku.exceptions.AddCellException;
import sudoku.exceptions.TooManyCellsException;;

public abstract class AbstractUnit implements Unit
{
	protected List<Cell> cells;
	int maxCells;
	
	protected AbstractUnit(int size) {
		maxCells = size;
		cells = new ArrayList<>(size);
	}
	
	@Override
	public void addCell(Cell cell) throws AddCellException {
//		checkCellPosition(cell);
		
		if ( cells.size() == maxCells ) {
			throw new TooManyCellsException("Exceeded " + maxCells + " entries");
		}
		cells.add(cell);
		cell.addToUnit(this);
	}
	
//	private void checkCellPosition(Cell cell) throws IllegalCellPositionException {
//		
//	}

	@Override
	public String toString() {
		return cells.toString();
	}
}
