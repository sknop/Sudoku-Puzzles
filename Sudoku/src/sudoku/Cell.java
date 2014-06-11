package sudoku;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;
import sudoku.unit.Unit;

public class Cell 
{
	private Integer value = null;
	private Point location;
	private List<Unit> belongsTo = new ArrayList<>();
	
	public Cell(Point location) {
		this.location = location;
	}
	
	public void setValue(Integer value) throws CellContentException {
		this.value = value;
		
		for (Unit u : belongsTo) {
			u.update(this);
		}
	}
	
	public void addToUnit(Unit unit) throws IllegalCellPositionException {
		belongsTo.add(unit);
		unit.addCell(this);
	}
	
	public Integer getValue() {
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
