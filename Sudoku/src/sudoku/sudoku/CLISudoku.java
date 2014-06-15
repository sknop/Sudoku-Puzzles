package sudoku.sudoku;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CLISudoku extends Sudoku
{
	static final String BigBorder = "+-----------------------+";
	static final String LittleBorder = "|-------+-------+-------|";
	static final String Front = "|";
	static final String Section = " %s %s %s |";
	
	public CLISudoku() {
		// nothing to do for now
	}

	public void draw() {
		StringBuilder b = new StringBuilder();
		
		b.append(BigBorder); b.append("\n");
		for (int r = 0; r < 2; r++) {
			b.append(drawBigRow(r));
		}
		
		for (int i = 1; i <= 2; i++) {
			b.append(drawRow(2,i));
		}
		b.append(drawRow(2,3));

		b.append(BigBorder); b.append("\n");
		
		System.out.println(b.toString());
	}
	
	private String drawBigRow(int r) {
		// made of 3 rows, finished with a Big Border
		StringBuilder b = new StringBuilder();
		
		for (int i = 1; i <= 2; i++) {
			b.append(drawRow(r,i));
		}
		b.append(drawRow(r,3));
		
		b.append(LittleBorder); b.append("\n");
		
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
		
		String x1 = getValueAsString(x + 1, y);
		String x2 = getValueAsString(x + 2, y);
		String x3 = getValueAsString(x + 3, y);
		
		return String.format(Section, x1, x2, x3);
	}
	
	private String getValueAsString(int x, int y) {
		int val = getValue(x, y);
		if (val == 0) {
			return " ";
		}
		return Integer.toString(val);
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
		
		CLISudoku full = new CLISudoku();
		Path path = FileSystems.getDefault().getPath("sudoku.csv");
		
		int[][] values = {
				{ 1,2,3,4,5,6,7,8,9 },
				{ 4,5,6,7,8,9,1,2,3 },
				{ 7,8,9,1,2,3,4,5,6 },
				{ 2,3,4,5,6,7,8,9,1 },
				{ 5,6,7,8,9,1,2,3,4 },
				{ 8,9,1,2,3,4,5,6,7 },
				{ 3,4,5,6,7,8,9,1,2 },
				{ 6,7,8,9,1,2,3,4,5 },
				{ 9,1,2,3,4,5,6,7,8 }
		};
		
		try {
			OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.WRITE};
			BufferedWriter writer = Files.newBufferedWriter(path, options );
			
			for (int row = 0; row < 9; row++) {
				writer.append(Integer.toString(values[row][0]));
				for (int col = 1; col < 9; col++) {
					writer.append(",");
					writer.append(Integer.toString(values[row][col]));
				}
				writer.append("\n");
			}
			
			writer.close();
			
			full.importFile(path);
		}
		catch(Exception e) {
			System.err.println("Should not happen : " + e);
		}
		
		full.draw();

		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
