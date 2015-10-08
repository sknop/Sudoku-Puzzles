package sudoku;

public class CellWrapper {
	Cell cell;
	int illegalValue = 0;
	
	public CellWrapper(Cell cell, int illegalValue) {
		this.cell = cell;
		this.illegalValue = illegalValue;
	}
	
	public Cell getCell() { 
		return cell;
	}
	
	public int getVisibleValue() {
		if (illegalValue > 0) {
			return illegalValue;
		}
		else {
			return cell.getValue();
		}
	}
	
	public int getIllegalValue() {
		return illegalValue;
	}
}
