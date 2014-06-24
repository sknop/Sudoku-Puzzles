package sudoku.unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sudoku.Cell;
import sudoku.exceptions.CellContentException;

public class Nonet extends AbstractUnit
{
	Set<Integer> numbers = new HashSet<>();
	
	/**
	 * Complement of numbers, these are the potential values still available
	 */
	Set<Integer> markUp = new HashSet<>(Arrays.asList(1,2,3,4,5,6,7,8,9));
	
	public Nonet(String position) {
		super(9, position); // a Nonet has exactly 9 cells
	
	}

	@Override
	public void update(int oldValue, int newValue) throws CellContentException {
		if ( oldValue != 0 ) {
			// need to remove the old value
			numbers.remove(oldValue);
			markUp.add(oldValue);
		}
		
		if ( newValue != 0) {
			if ( numbers.contains(newValue) ) {
				throw new CellContentException("Value " + newValue + " already exists in " + this);
			}
			else {
				numbers.add(newValue);
				markUp.remove(newValue);
			}
		}
	}

	@Override 
	public String toString() {
		return "Nonet " + super.toString() + " : " + numbers + " : " + markUp;
	}

	// #############################################################
	
	public static void main(String[] args) throws Exception {
		Nonet nonet = new Nonet("1st row");
		Nonet nonet2 = new Nonet("2nd row");
		
		List<Cell> cells = new ArrayList<>();
		int x = 1;
		for (int y = 1; y <= 9; y++) {
			Cell cell = new Cell(x,y);
			cells.add(cell);
			nonet.addCell(cell);
			nonet2.addCell(cell);
		}
		System.out.println(nonet);
		
		for (int i = 0; i < 9; i++) {
			cells.get(i).setValue(i + 1);
		}
		System.out.println(nonet);
		System.out.println(nonet2);
	}

	@Override
	public Set<Integer> getMarkUp() {
		return Collections.unmodifiableSet(markUp);
	}
}
