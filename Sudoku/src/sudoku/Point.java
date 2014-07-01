package sudoku;

import sudoku.exceptions.IllegalCellPositionException;

public class Point implements Comparable<Point>
{
	private final int x;
	private final int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public static Point createChecked(int x, int y, int min, int max) 
			throws IllegalCellPositionException {
		if (x < min)
			throw new IllegalCellPositionException(String.format("%d less than %d", x, min));
		if (x > max)
			throw new IllegalCellPositionException(String.format("%d larger than %d", x, max));
		if (y < min)
			throw new IllegalCellPositionException(String.format("%d less than %d", y, min));
		if (y > max)
			throw new IllegalCellPositionException(String.format("%d larger than %d", y, max));
		
		return new Point(x,y);
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	

    @Override 
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof Point) {
            Point that = (Point) other;
            result = (this.getX() == that.getX() && this.getY() == that.getY());
        }
        return result;
    }

    @Override 
    public int hashCode() {
        return (41 * (41 + getX()) + getY());
    }

	@Override
	public String toString() {
		return String.format("(%d,%d)", getX(), getY());
	}

	@Override
	public int compareTo(Point o) {
		int result = 0;
		
		if (getX() == o.getX()) 
			result = getY() - o.getY();
		else {
			result = getX() - o.getX();
		}

		return result;
	}
}
