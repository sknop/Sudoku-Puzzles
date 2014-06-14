package sudoku.unit;

import sudoku.Cell;
import sudoku.exceptions.AddCellException;
import sudoku.exceptions.CellContentException;

public interface Unit
{
	public void addCell(Cell cell) throws AddCellException;

	public void update(Integer oldValue, Integer newValue) throws CellContentException;
}
