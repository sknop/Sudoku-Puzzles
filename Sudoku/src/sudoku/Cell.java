package sudoku;

import java.util.ArrayList;
import java.util.List;

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
