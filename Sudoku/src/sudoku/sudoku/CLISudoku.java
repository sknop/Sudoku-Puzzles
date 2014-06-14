package sudoku.sudoku;

public class CLISudoku extends Sudoku
{
	static final String BigBorder = "=========================================";
	static final String LittleBorder = "-----------------------------------------";
	static final String Front = "||";
	static final String Section = " %s | %s | %s ||";
	
	public CLISudoku() {
		// nothing to do for now
	}

	public void draw() {
		StringBuilder b = new StringBuilder();
		
		b.append(BigBorder); b.append("\n");
		for (int r = 0; r < 3; r++) {
			b.append(drawBigRow(r));
		}
		
		System.out.println(b.toString());
	}
	
	private String drawBigRow(int r) {
		// made of 3 rows, finished with a Big Border
		StringBuilder b = new StringBuilder();
		
		for (int i = 1; i <= 2; i++) {
			b.append(drawRow(r,i));
			b.append(LittleBorder); b.append("\n");
		}
		b.append(drawRow(r,3));
		
		b.append(BigBorder); b.append("\n");
		
		return b.toString();
	}
	
	private String drawRow(int bigRow, int row) {
		// made up of one front and 3 sections
		
		StringBuilder b = new StringBuilder();
		
		b.append(Front);
		for (int section = 0; section < 3; section++) {
			b.append(drawOneSection(bigRow, row, section));
		}
		b.append("\n");
		
		return b.toString();
	}
	
	private String drawOneSection(int bigRow, int row, int section) {
		int y = bigRow * 3 + row;
		int x = section * 3;
		
		String x1 = getValueString(x + 1, y);
		String x2 = getValueString(x + 2, y);
		String x3 = getValueString(x + 3, y);
		
		return String.format(Section, x1, x2, x3);
	}
	
	private String getValueString(int x, int y) {
		Integer val = getValue(x, y);
		if (val == null) {
			return " ";
		}
		return val.toString();
	}
	
	public static void main(String args[]) {
		CLISudoku cli = new CLISudoku();
		
		try {
			cli.setValue(1, 1, 1);
			cli.setValue(9, 9, 9);
		}
		catch(Exception e) {
			System.err.println("Should not happen : " + e);
		}
		cli.draw();
	}
}
