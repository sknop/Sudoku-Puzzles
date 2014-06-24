package sudoku;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sudoku.exceptions.AddCellException;
import sudoku.exceptions.CellContentException;
import sudoku.unit.Unit;

public class Cell 
{
	private int value = 0;
	private Point location;
	private List<Unit> belongsTo = new ArrayList<>();
	
	public Cell(Point location) {
		this.location = location;
	}

	public Cell(int x, int y) {
		this.location = new Point(x,y);
	}

	public void setValue(int value) throws CellContentException {
		for (Unit u : belongsTo) {
			u.update(this.value, value);
		}
		
		this.value = value;		
	}
	
	public Set<Integer> getMarkUp() {
		Set<Integer> markUp = new HashSet<>();
		
		if (value == 0) {
			for (int i = 1; i <= 9; i++) {
				markUp.add(i);
			}
			
			for (Unit u : belongsTo) {
				markUp.retainAll(u.getMarkUp());
			}
		}
		
		return markUp;
	}
	
	public void addToUnit(Unit unit) throws AddCellException {
		belongsTo.add(unit);
	}
	
	public int getValue() {
		return value;
	}
	
	public Point getLocation() {
		return location;
	}
	
	@Override
	public String toString() {
		return location.toString() + " : " + value;
	}
}
