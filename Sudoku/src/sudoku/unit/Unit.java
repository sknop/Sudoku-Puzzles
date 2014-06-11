package sudoku.unit;

import sudoku.Cell;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;

public interface Unit
{
	public void addCell(Cell cell) throws IllegalCellPositionException;

	public void update(Cell cell) throws CellContentException;
}
