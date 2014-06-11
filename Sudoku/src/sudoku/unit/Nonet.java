package sudoku.unit;

import java.util.HashSet;
import java.util.Set;

import sudoku.Cell;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;

public class Nonet extends AbstractUnit
{
	Set<Integer> numbers = new HashSet<>();
	
	public Nonet() {
		super(9); // a Nonet has exactly 9 cells
		
		// TODO Would be useful to record the location of this Nonet
		// would require overriding add or setting up a check function
	}

	@Override
	protected void checkCellPosition(Cell cell) throws IllegalCellPositionException {
		
	}
	
	@Override
	public void update(Cell cell) throws CellContentException {
		// TODO Auto-generated method stub

	}

}
