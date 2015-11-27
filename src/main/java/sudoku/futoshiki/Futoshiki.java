/*
 * Copyright (c) 2015 Sven Erik Knop.
 * Licensed under the EUPL V.1.1
 *
 * This Software is provided to You under the terms of the European
 * Union Public License (the "EUPL") version 1.1 as published by the
 * European Union. Any use of this Software, other than as authorized
 * under this License is strictly prohibited (to the extent such use
 * is covered by a right of the copyright holder of this Software).
 *
 * This Software is provided under the License on an "AS IS" basis and
 * without warranties of any kind concerning the Software, including
 * without limitation merchantability, fitness for a particular purpose,
 * absence of defects or errors, accuracy, and non-infringement of
 * intellectual property rights other than copyright. This disclaimer
 * of warranty is an essential part of the License and a condition for
 * the grant of any rights to this Software.
 *
 * For more details, see http://joinup.ec.europa.eu/software/page/eupl.
 *
 *  Contributors:
 *      2015 - Sven Erik Knop - initial API and implementation
 *
 */

package sudoku.futoshiki;

import sudoku.Cell;
import sudoku.Point;
import sudoku.Puzzle;
import sudoku.exceptions.AddCellException;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalFileFormatException;
import sudoku.unit.Nonet;
import sudoku.unit.Relation;
import sudoku.unit.Unit;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Futoshiki extends Puzzle
{
    private final List<Unit> rows = new ArrayList<>();
    private final List<Unit> columns = new ArrayList<>();
    private List<Relation> relations = new ArrayList<>();

    // Default constructor for CLI - size 5 for typical Times Puzzle
    public Futoshiki() { this(5); }

    public Futoshiki(int maxValue) {
        super(maxValue);
        initialize(maxValue);
    }

    private void initialize(int maxValue) {

        try {
            for (int x = 1; x <= maxValue; x++) {
                for (int y = 1; y <= maxValue; y++) {
                    Point p = new Point(x,y);
                    Cell cell = new Cell(maxValue, p);
                    getCells().put(p, cell);
                }
            }

            for (int x = 1; x <= maxValue; x++) {
                Unit row = new Unit(maxValue, String.format("Row %d", x));
                rows.add(row);
                for (int y = 1; y <= maxValue; y++) {
                    Point p = new Point(x,y);
                    Cell cell = getCells().get(p);
                    row.addCell(cell);
                }
            }

            for (int y = 1; y <= maxValue; y++) {
                Unit column = new Unit(maxValue, String.format("Column %d", y));
                columns.add(column);
                for (int x = 1; x <= maxValue; x++) {
                    Point p = new Point(x,y);
                    Cell cell = getCells().get(p);
                    column.addCell(cell);
                }
            }
        } catch (AddCellException e) {
            System.err.println("Should never happen:" + e);
        }

    }

    private int getSize(String line) throws IllegalFileFormatException
    {
        int size;

        if (line != null) {
            String pattern = "size=(\\d+)";
            Pattern r = Pattern.compile(pattern);

            Matcher m = r.matcher(line);
            if (m.find()) {
                String sizeString = m.group(0);
                size = Integer.parseInt(sizeString);
            }
            else {
                throw new IllegalFileFormatException("Did not recognize size, expected size=<N> in first line. Found: '" + line + "'.");
            }

        }
        else {
            throw new IllegalFileFormatException("Missing size=<N> in first line. Aborting.");
        }

        return size;
    }

    private void importArray(int size, int[][] values) {

    }

    /**
     *
     * Imports a Futoshiki puzzle from a file.
     * The expected format is
     *
     * First line: size=N
     * Followed by data:
     * CSV in 2*N-1 rows, 2*N-1 columns
     * Empty Cells are signaled by a 0
     * Relationship is shown by ' ' (for no relationship), '<', '>', '^', 'v'
     * For example
     *
     *  size=5
     *  0,<,0,<,0,<,0,<,5
     *   , ,^, , , , , ,
     *  0,<,0,<,0,<,0,<,0
     *   , ,^, , , , , ,
     *  0,<,0,<,0,<,0,<,0
     *   , ,^, , , , , ,
     *  0,<,0,<,0,<,0,<,0
     *   , ,^, , , , , ,
     *  0,<,0,<,0,<,0,<,0
     *
     * @param path : Path
     * @throws IOException, IllegalFileFormatException, CellContentException
     */
    @Override
    public void importFile(Path path) throws IOException, IllegalFileFormatException, CellContentException {
        try( BufferedReader br = Files.newBufferedReader(path) ) {
            String line;
            line = br.readLine();

            int size = getSize(line);
            int rowSize = size * 2 - 1;

            int[][] values = new int[size][size];
            char[][] relations = new char[size - 1][size - 1];

            int row = 0;

            while ( (line = br.readLine()) != null) {
                String[] lineValues = line.split(",");
                if (lineValues.length != rowSize) {
                    throw new IllegalFileFormatException("Illegal entry in file " + path + " : " + line);
                }

                for (int col = 0; col < rowSize; col++) {
                    values[row][col] = Integer.parseInt(lineValues[col]);
                }

                row++;
            }

            reset();

            importArray(size, values);
        }
    }

    @Override
    public void exportFile(Path path) throws IOException {

    }

    @Override
    public void showMarkUp() {

    }

    @Override
    public void showHints(int level) {

    }

    @Override
    public String toCLIString() {
        return null;
    }

    @Override
    public int getLow() {
        return 0;
    }

    @Override
    public int getHigh() {
        return 0;
    }

    @Override
    public void createRandomPuzzle() {

    }
}
