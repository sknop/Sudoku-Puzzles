package sudoku;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Supplier;

import net.sourceforge.argparse4j.ArgumentParserBuilder;
import net.sourceforge.argparse4j.impl.Arguments;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;
import sudoku.exceptions.IllegalFileFormatException;
import sudoku.futoshiki.Futoshiki;
import sudoku.samurai.Samurai;
import sudoku.sudoku.Sudoku;
import sudoku.supersudoku.SuperSudoku;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class CLI implements Runnable
{
    protected Puzzle puzzle;
    private Map<String, Runnable> commands = new HashMap<>();

    private static Map<String, Supplier<Puzzle>> puzzles = new HashMap<>();
    private static final String SUDOKU = "Sudoku";
    private static final String SAMURAI = "Samurai";
    private static final String SUPER = "Super";
    private static final String FUTOSHIKI = "Futoshiki";

    private static Map<String, Function<Integer, Puzzle>> parameterizedPuzzles = new HashMap<>();

    static {
        puzzles.put(SUDOKU, Sudoku::new);
        puzzles.put(SAMURAI, Samurai::new);
        puzzles.put(SUPER, SuperSudoku::new);
        puzzles.put(FUTOSHIKI, Futoshiki::new);

        parameterizedPuzzles.put(FUTOSHIKI, Futoshiki::new);
    }

    public CLI(Namespace options)
            throws IOException, IllegalFileFormatException, CellContentException {

        String puzzleType = options.get("puzzle");
        Integer size = options.get("size");

        if (size != null) {
            puzzle = parameterizedPuzzles.get(puzzleType).apply(size);
        } else {
            puzzle = puzzles.get(puzzleType).get();
        }

        String fileName = options.get("input");
        if (fileName != null) {
            Path path = FileSystems.getDefault().getPath(fileName);
            puzzle.importFile(path);
        }

        populateCommands();

    }

    private void populateCommands() {
        commands.put("h", this::help);
        commands.put("p", this::put);
        commands.put("d", this::delete);
        commands.put("m", this::showMarkUp);
        commands.put("t", this::showHints);
        commands.put("s", this::save);
        commands.put("b", this::bruteForce);
        commands.put("c", this::createNewPuzzle);
        commands.put("u", this::unique);
        commands.put("l", this::load);
        commands.put("q", this::quit);
    }

    private void getCommand() {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);

        System.out.print("Command (h,q,p,d,m,t,b,c,u,s,l) : ");

        String s = scanner.next();
        String command = s.substring(0, 1).toLowerCase();

        if (commands.containsKey(command)) {
            commands.get(command).run();
        } else {
            System.out.println("Unknown command \"" + s + "\"");
        }
    }

    private void help() {
        System.out.println("h : help");
        System.out.println("p : put");
        System.out.println("d : delete");
        System.out.println("m : markUp");
        System.out.println("t : hints (tricks)");
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

        int limit = puzzle.getMaxValue() + 1;

        try {
            System.out.print("row : ");
            int x = scanner.nextInt(limit);
            System.out.print("col : ");
            int y = scanner.nextInt(limit);

            try {
                Point p = Point.createChecked(x, y, puzzle.getLow(), puzzle.getHigh());

                System.out.print("val : ");
                int value = scanner.nextInt(limit);

                puzzle.setValue(p, value);
            } catch (IllegalCellPositionException | CellContentException e) {
                System.out.println(e);
            }

        } catch (InputMismatchException e) {
            System.out.println("Illegal input : " + e);
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
            e.printStackTrace();
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
            System.out.println("This puzzle does not have a solution");
        }
    }

    private void unique() {
        int unique = puzzle.isUnique();
        if (unique > 1) {
            System.out.println("This puzzle has more than one solution");
        } else if (unique == 0) {
            System.out.println("This puzzle has no solutions");
        } else {
            System.out.println("This puzzle is unique");
        }
    }

    private void showMarkUp() {
        puzzle.showMarkUp();
    }

    private void showHints() {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);

        System.out.print("level : ");
        int level = scanner.nextInt();

        puzzle.showHints(level);
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
        ArgumentParserBuilder builder = ArgumentParsers.newFor("CLI").addHelp(true);

        ArgumentParser parser = builder.build();
        parser.addArgument("-i", "--input")
                .help("Input file, if not set, create empty puzzle");
        parser.addArgument("-p", "--puzzle")
                .required(true)
                .help("Puzzle, can be " + String.join(",", puzzles.keySet()))
                .choices(puzzles.keySet());
        parser.addArgument("-s", "--size")
                .help("Optional size of the puzzle")
                .type(Integer.class)
                .choices(Arguments.range(2,16));

        try {
            Namespace options = parser.parseArgs(args);

            CLI cli = new CLI(options);

            cli.run();
        } catch (ArgumentParserException e) {
            System.err.println(e.getMessage());
            System.err.println();
            System.err.println(parser.formatHelp());
        } catch (IOException | IllegalFileFormatException | CellContentException e) {
            System.err.println("Error " + e);
        }
    }

}
