package sudoku.exceptions;

public class TooManyCellsException extends AddCellException
{
	private static final long serialVersionUID = 1359907216560759477L;

	public TooManyCellsException(String message) {
		super(message);
	}

}
