package sudoku.unit;

import java.util.ArrayList;
import java.util.List;

import sudoku.Cell;
import sudoku.exceptions.AddCellException;
import sudoku.exceptions.TooManyCellsException;;

public abstract class AbstractUnit implements Unit
{
	protected List<Cell> cells;
	protected int maxCells;
	protected String position;
	
	protected AbstractUnit(int size, String position) {
		this.maxCells = size;
		this.cells = new ArrayList<>(size);
		this.position = position;
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
		return String.format("(%s) %s", position, cells.toString());
	}
}
