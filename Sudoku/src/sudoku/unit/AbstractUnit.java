package sudoku.unit;

import java.util.ArrayList;
import java.util.List;

import sudoku.Cell;
import sudoku.exceptions.IllegalCellPositionException;

public abstract class AbstractUnit implements Unit
{
	protected List<Cell> cells;

	protected AbstractUnit(int size) {
		cells = new ArrayList<>(size);
	}
	
	@Override
	public void addCell(Cell cell) throws IllegalCellPositionException {
		checkCellPosition(cell);
		
		cells.add(cell);
	}
	
	abstract protected void checkCellPosition(Cell cell) throws IllegalCellPositionException;

}
