package sudoku;

public class Point
{
	private final int x;
	private final int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
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
}
