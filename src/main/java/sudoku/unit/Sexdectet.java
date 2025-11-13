package sudoku.unit;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import sudoku.Cell;
import sudoku.MarkUp;
import sudoku.exceptions.AddCellException;
import sudoku.exceptions.CellContentException;

public class Sexdectet extends Unit
{

	public Sexdectet(String position) {
		super(16, position);
	}

	@Override 
	public String toString() {
		MarkUp complement = numbers.complement();
		return "Sexdectet " + super.toString() + " : " + numbers + " : " + complement;
	}


	public static void main(String[] args) throws AddCellException, CellContentException {
		AbstractConstraint sexdectet = new Sexdectet("1st row");
		AbstractConstraint sextectet2 = new Sexdectet("2nd row");
		
		List<Cell> cells = new ArrayList<>();
		int x = 1;
		for (int y = 1; y <= 16; y++) {
			Cell cell = new Cell(16, x,y);
			cells.add(cell);
			sexdectet.addCell(cell);
			sextectet2.addCell(cell);
		}
		System.out.println(sexdectet);
		
		for (int i = 0; i < 16; i++) {
			cells.get(i).setValue(i + 1);
		}
		System.out.println(sexdectet);
		System.out.println(sextectet2);
	}

}
