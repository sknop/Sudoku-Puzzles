package sudoku.exceptions;

public class DuplicateValueException extends CellContentException
{

	public DuplicateValueException(String message) {
		super(message);
	}

}
