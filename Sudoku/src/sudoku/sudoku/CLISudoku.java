package sudoku.sudoku;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import sudoku.Point;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;
import sudoku.exceptions.IllegalFileFormatException;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.internal.HelpScreenException;

public class CLISudoku extends Sudoku implements Runnable
{
	static final String BigBorder = "  +-----------------------+";
	static final String LittleBorder = "  |-------+-------+-------|";
	static final String Front = " |";
	static final String Section = " %s %s %s |";

	private Map<String, Runnable> commands = new HashMap<>();
	
	public CLISudoku(String args[]) 
			throws ArgumentParserException, 
				   IOException,
				   IllegalFileFormatException,
				   CellContentException {
		ArgumentParser parser = ArgumentParsers.newArgumentParser("CLI Sudoku",true);
		parser.addArgument("-i", "--input");
		
		Namespace options = parser.parseArgs(args);

		String fileName = options.get("input");
		if (fileName != null) {
			Path path = FileSystems.getDefault().getPath(fileName);
			this.importFile(path);
		}
		
		populateCommands();
	}

	private void populateCommands() {
		commands.put("h", () -> help());
		commands.put("p", () -> put());
		commands.put("d", () -> delete());
		commands.put("m", () -> showMarkUp());
		commands.put("s", () -> save());
		commands.put("l", () -> load());
		commands.put("q", () -> quit());
	}

	private void getCommand() {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Command (h,q,p,d,m,s,l) : ");
		
		String s = scanner.next();
		String command = s.substring(0, 1).toLowerCase();
		
		if (commands.containsKey(command)) {
			commands.get(command).run();
		}
		else {
			System.out.println("Unknown command \"" + s + "\"");
		}
	}
	
	private void help() {
		System.out.println("h : help");
		System.out.println("p : put");
		System.out.println("d : delete");
		System.out.println("m : markUp");
		System.out.println("q : quit");
		System.out.println("s : save");
		System.out.println("l : load");
		System.out.println();
	}

	private void put() {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

		System.out.print("row : ");
		int x = scanner.nextInt();
		System.out.print("col : ");
		int y = scanner.nextInt();
		
		try {
			Point p = Point.createChecked(x, y, 1, 9);
			
			System.out.print("val : ");
			int value = scanner.nextInt();
			
			this.setValue(p, value);
		} catch (IllegalCellPositionException | CellContentException e) {
			System.out.println(e);
		}
		
	}
	
	private void delete() {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

		System.out.print("row : ");
		int x = scanner.nextInt();
		System.out.print("col : ");
		int y = scanner.nextInt();
		
		try {
			Point p = Point.createChecked(x, y, 1, 9);
			
			this.setValue(p, 0);
		} catch (IllegalCellPositionException | CellContentException e) {
			System.out.println(e);
		}
	}
	
	private void save() {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

		System.out.print("filename : ");
		String fileName = scanner.next();
		
		Path path = FileSystems.getDefault().getPath(fileName);
		try {
			this.exportFile(path);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	private void load() {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

		System.out.print("filename : ");
		String fileName = scanner.next();
		
		Path path = FileSystems.getDefault().getPath(fileName);
		try {
			this.importFile(path);
		} catch (IOException | IllegalFileFormatException | CellContentException e) {
			System.out.println(e);
		}
	}
	
	private void quit() {
		System.exit(0);
	}
	
	public void draw() {
		System.out.println(this.toString());
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		
		b.append("  ");
		for (int i = 0; i < 3; i++) {
			b.append("  ");
			for (int j = 1; j <= 3; j++) {
				b.append(i*3 + j);
				b.append(" ");
			}
		}
		b.append("\n");
		
		b.append(BigBorder); b.append("\n");
		for (int r = 0; r < 2; r++) {
			b.append(drawBigRow(r));
		}
		
		for (int i = 1; i <= 2; i++) {
			b.append(drawRow(2,i));
		}
		b.append(drawRow(2,3));

		b.append(BigBorder); b.append("\n");
		
		return b.toString();
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
		
		b.append(bigRow * 3 + row);
		b.append(Front);
		for (int section = 0; section < 3; section++) {
			b.append(drawOneSection(bigRow, row, section));
		}
		b.append("\n");
		
		return b.toString();
	}
	
	private String drawOneSection(int bigRow, int row, int section) {
		int x = bigRow * 3 + row;
		int y = section * 3;
		
		String x1 = getValueAsString(x, y + 1);
		String x2 = getValueAsString(x, y + 2);
		String x3 = getValueAsString(x, y + 3);
		
		return String.format(Section, x1, x2, x3);
	}
	
	private String getValueAsString(int x, int y) {
		int val = getValue(x, y);
		if (val == 0) {
			return " ";
		}
		return Integer.toString(val);
	}
	
	public void run() {
		while (true) {
			draw();
			getCommand();
		}
	}
	
	private void showMarkUp() {
		for (int x = 1; x <= 9; x++) {
			for (int y = 1; y <= 9; y++) {
				Point p = new Point(x,y);
				
				Set<Integer> markUp = getMarkUp(p);
				
				System.out.println(String.format("(%s, %s) : %s", x, y, markUp));
			}
		}
		System.out.println();
	}

	public static void main(String args[]) {
		CLISudoku sudoku;
		try {
			sudoku = new CLISudoku(args);
			
			sudoku.run();
		
		}
		catch (HelpScreenException e) {
			// prints out the Help screen already. Simply stop here
		}
		catch (ArgumentParserException | IOException
				| IllegalFileFormatException | CellContentException e) {
			e.printStackTrace();
		}
	}
}
