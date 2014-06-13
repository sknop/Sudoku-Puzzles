package sudoku.exceptions;

public class IllegalCellPositionException extends AddCellException
{
	private static final long serialVersionUID = -7955612804865817475L;

	public IllegalCellPositionException(String message) {
		super(message);
	}
}
