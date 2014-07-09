package sudoku;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Supplier;

import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;
import sudoku.exceptions.IllegalFileFormatException;
import sudoku.samurai.Samurai;
import sudoku.sudoku.Sudoku;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.internal.HelpScreenException;

public class CLI implements Runnable 
{
	protected Puzzle puzzle;
	private Map<String, Runnable> commands = new HashMap<>();
	
	private static Map<String, Supplier<Puzzle>> puzzles = new HashMap<>();
	private static final String SUDOKU = "Sudoku";
	private static final String SAMURAI = "Samurai";
	
	static {
		puzzles.put(SUDOKU, Sudoku::new);
		puzzles.put(SAMURAI, Samurai::new);
	}
	
	public CLI(Namespace options) 
			throws IOException, IllegalFileFormatException, CellContentException {

		String puzzleType = options.get("puzzle");
		puzzle = puzzles.get(puzzleType).get();
		
		String fileName = options.get("input");
		if (fileName != null) {
			Path path = FileSystems.getDefault().getPath(fileName);
			puzzle.importFile(path);
		}
		
		populateCommands();

	}
	
	private void populateCommands() {
		commands.put("h", () -> help());
		commands.put("p", () -> put());
		commands.put("d", () -> delete());
		commands.put("m", () -> showMarkUp());
		commands.put("s", () -> save());
		commands.put("b", () -> bruteForce());
		commands.put("c", () -> createNewPuzzle());
		commands.put("u", () -> unique());
		commands.put("l", () -> load());
		commands.put("q", () -> quit());
	}

	private void getCommand() {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Command (h,q,p,d,m,b,c,u,s,l) : ");
		
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
		System.out.println("b : bruteForce");
		System.out.println("c : create");
		System.out.println("u : unique");
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
			Point p = Point.createChecked(x, y, puzzle.getLow(), puzzle.getHigh());
			
			System.out.print("val : ");
			int value = scanner.nextInt();
			
			puzzle.setValue(p, value);
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
			Point p = Point.createChecked(x, y, puzzle.getLow(), puzzle.getHigh());
			
			puzzle.setValue(p, 0);
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
			puzzle.exportFile(path);
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
			puzzle.importFile(path);
		} catch (IOException | IllegalFileFormatException | CellContentException e) {
			System.out.println(e);
		}
	}
	
	private void bruteForce() {
		if (!puzzle.solveBruteForce()) {
			System.out.println("This Sudoku does not have a solution");
		}
	}
	
	private void unique() {
		int unique = puzzle.isUnique();
		if (unique > 1) {
			System.out.println("This puzzle has more than one solution");
		}
		else if (unique == 0) {
			System.out.println("This puzzle has no solutions");
		}
		else {
			System.out.println("This puzzle is unique");
		}
	}
	
	private void showMarkUp() {
		puzzle.showMarkUp();
	}
	
	private void createNewPuzzle() {
		puzzle.createRandomPuzzle();
	}
	
	private void quit() {
		System.exit(0);
	}
	
	public void draw() {
		System.out.println(puzzle.toCLIString());
	}
	
	@Override
	public void run() {
		while (true) {
			draw();
			
			if (puzzle.isSolved()) {
				System.out.println("Done.");
				return;
			}
			getCommand();
		}
	}


	public static void main(String[] args) {
		ArgumentParser parser = ArgumentParsers.newArgumentParser("CLI", true);
		parser.addArgument("-i", "--input")
			.help("Input file, if not set, create empty puzzle");
		parser.addArgument("-p", "--puzzle")
			.required(true)
			.help("Puzzle, can be Sudoku or Samurai")
			.choices(SUDOKU, SAMURAI);
		
		try {
			Namespace options = parser.parseArgs(args);

			CLI cli = new CLI(options);
			
			cli.run();
		
		}
		catch (HelpScreenException e) {
			// prints out the Help screen already. Simply stop here
		}
		catch (ArgumentParserException e) {
			System.err.println(e.getMessage());
			System.err.println();
			System.err.println(parser.formatHelp());
		}
		catch ( IOException	| IllegalFileFormatException | CellContentException e) {
			System.err.println("Error " + e);
		}
	}

}
