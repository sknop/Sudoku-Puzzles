package sudoku.unit;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import sudoku.Cell;
import sudoku.exceptions.AddCellException;
import sudoku.exceptions.CellContentException;

public class Sexdectet extends AbstractUnit
{

	public Sexdectet(String position) {
		super(16, position);
		// TODO Auto-generated constructor stub
	}

	@Override 
	public String toString() {
		BitSet complement = (BitSet) numbers.clone();
		complement.flip(1, 17);
		return "Sexdectet " + super.toString() + " : " + numbers + " : " + complement;
	}


	public static void main(String[] args) throws AddCellException, CellContentException {
		AbstractUnit sexdectet = new Sexdectet("1st row");
		AbstractUnit sextectet2 = new Sexdectet("2nd row");
		
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
