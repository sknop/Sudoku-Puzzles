package sudoku.exceptions;

public class ValueOutsideRangeException extends CellContentException
{
	private static final long serialVersionUID = -445994557148385322L;

	public ValueOutsideRangeException(String message) {
		super(message);
	}


}
