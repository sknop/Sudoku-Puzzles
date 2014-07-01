package sudoku;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;
import sudoku.exceptions.ValueOutsideRangeException;

public abstract class Puzzle
{
	protected int maxValue;
	protected final Map<Point, Cell> cells = new HashMap<>();

	public Puzzle(int maxValue) {
		this.maxValue = maxValue;
	}

	public int getValue(int x, int y) {
		return getValue(new Point(x,y));
	}

	public int getValue(final Point p) {
		return cells.get(p).getValue();
	}

	public Set<Integer> getMarkUp(int x, int y) {
		return getMarkUp(new Point(x,y));
	}

	public Set<Integer> getMarkUp(Point point) {
		return cells.get(point).getMarkUp();
	}

	/**
	 * Returns true if every cell in this Sudoku 
	 * has a value different than 0
	 * 
	 * @return true if this Sudoku is solved
	 */
	public boolean isSolved() {
		for (Cell cell : cells.values()) {
			if (cell.empty()) {
				return false;
			}
		}
		
		return true;
	}

	public void setValue(int x, int y, int value)
			throws CellContentException, IllegalCellPositionException {
		setValue(new Point(x,y),value);
	}

	public void setValue(final Point p, int value)
			throws CellContentException, IllegalCellPositionException {	
		if ( value < 0 || value > maxValue ) {
			String error = String.format("Value %d not in range (1-%d)", value, maxValue);
			throw new ValueOutsideRangeException(error);
		}
		if ( cells.containsKey(p))
			cells.get(p).setValue(value);
		else
			throw new IllegalCellPositionException("No cell at " + p);
	}

	/**
	 * Solves the Sudoku, trying out every combination
	 */
	public boolean solveBruteForce() {
		Deque<Cell> emptyCells = cells.values()
				.stream()
				.filter( c -> c.empty() )
				.sorted( (a,b) -> a.getLocation().compareTo(b.getLocation()) )
				.collect( Collectors.toCollection(LinkedList::new) );
				
		return solveRecursive(emptyCells);
	}

	private boolean solveRecursive(Deque<Cell> empties) {
		// recursive bottom
		if (empties.size() == 0) 
			return true;
	
		// separate into head and tail
		LinkedList<Cell> tail = new LinkedList<Cell>(empties);
		Cell head = tail.remove();
		
		for (int i : head.getMarkUp()) {
			try {
				head.setValue(i);
				
				if (solveRecursive(tail)) {
					return true;
				}
	
				head.reset();
			} catch (CellContentException e) {
				// now violates a condition, therefore incorrect entry
				// try next value
				continue;
			}
		}
		return false; // did not find a solution here, back track
	}

}