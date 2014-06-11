package sudoku.exceptions;

public class CellContentException extends Exception
{
	private static final long serialVersionUID = -6742216600583422750L;

	public CellContentException(String message) {
		super(message);
	}
}
