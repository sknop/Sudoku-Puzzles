package sudoku.unit;

import java.util.Set;

import sudoku.Cell;
import sudoku.exceptions.AddCellException;
import sudoku.exceptions.CellContentException;

public interface Unit
{
	public void addCell(Cell cell) throws AddCellException;

	public void update(int oldValue, int newValue) throws CellContentException;
	
	public Set<Integer> getMarkUp();
}
