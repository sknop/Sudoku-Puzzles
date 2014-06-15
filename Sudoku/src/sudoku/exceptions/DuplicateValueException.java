package sudoku.exceptions;

public class DuplicateValueException extends CellContentException
{
	private static final long serialVersionUID = -1925226277048899944L;

	public DuplicateValueException(String message) {
		super(message);
	}

}
