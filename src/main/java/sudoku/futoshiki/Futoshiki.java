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
import sudoku.Tuple;
import sudoku.exceptions.AddCellException;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalFileFormatException;
import sudoku.unit.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Futoshiki extends Puzzle
{

    private final List<Unit> rows = new ArrayList<>();
    private final List<Unit> columns = new ArrayList<>();
    private final Map<Tuple, Relation> relations = new HashMap<>();

    // Default constructor for CLI - size 5 for typical Times Puzzle
    public Futoshiki() { this(5); }

    public Futoshiki(int maxValue) {
        super(maxValue);
        if (maxValue < 2) {
            throw new RuntimeException("MaxValue must be larger than 1");
        }
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
                String sizeString = m.group(1);
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

    private void importArray(int size, int[][] values) throws CellContentException {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Point p = new Point(row + 1,col + 1);
                int value = values[row][col];
                if (value > 0)
                    getCells().get(p).setInitValue(value);
            }
        }
    }

    private void importHorizonalRelations(int size, char[][] hor_rel) throws IllegalFileFormatException {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size - 1; col++) {
                char val = hor_rel[row][col];
                if (val != '-') { // minus means not set
                    Point from = new Point(row + 1, col + 1); // Points are counted from 1, not 0
                    Point to = new Point(row + 1, col + 2);

                    Cell source = getCells().get(from);
                    Cell target = getCells().get(to);

                    Relation forward;
                    Relation back;

                    if (val == '>') {
                        forward = new GreaterThan(source, target, size);
                        back = new LessThan(target, source, size);
                    }
                    else if (val == '<') {
                        forward = new LessThan(source, target, size);
                        back = new GreaterThan(target, source, size);
                    }
                    else {
                        throw new IllegalFileFormatException("Illegal relation '" + val + "' in row " + row + " col " + col);
                    }

                    source.addConstraint(forward);
                    target.addConstraint(back);

                    relations.put(new Tuple(from, to), forward);
                    relations.put(new Tuple(to, from), back);
                }
            }
        }
    }

    private void importVerticalRelations(int size, char[][] ver_rel) throws IllegalFileFormatException {
        for (int row = 0; row < size - 1; row++) {
            for (int col = 0; col < size; col++) {
                char val = ver_rel[row][col];
                if (val != '-') { // minus means not set
                    Point from = new Point(row + 1, col + 1);
                    Point to = new Point(row + 2, col + 1);

                    Cell source = getCells().get(from);
                    Cell target = getCells().get(to);

                    Relation forward;
                    Relation back;

                    if (val == 'v') {
                        forward = new GreaterThan(source, target, size);
                        back = new LessThan(target, source, size);
                    }
                    else if (val == '^') {
                        forward = new LessThan(source, target, size);
                        back = new GreaterThan(target, source, size);
                    }
                    else {
                        throw new IllegalFileFormatException("Illegal relation '" + val + "' in row " + row + " col " + col);
                    }

                    source.addConstraint(forward);
                    target.addConstraint(back);

                    relations.put(new Tuple(from, to), forward);
                    relations.put(new Tuple(to, from), back);
                }
            }
        }
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
     * Relationship is shown by '-' (for no relationship), '<', '>', '^', 'v'
     * For example
     *
     *  size=5
     *  0,<,0,<,0,<,0,<,5   0
     *  -,-,^,-,-,-,-,-,-   1
     *  0,<,0,<,0,<,0,<,0   2
     *  -,-,^,-,-,-,-,-,v   3
     *  0,<,0,<,0,<,0,<,0   4
     *  -,-,^,-,-,-,-,-,-   5
     *  0,<,0,<,0,<,0,<,0   6
     *  -,-,^,-,-,-,-,-,-   7
     *  0,<,0,<,0,<,0,<,0   8
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
            char[][] hor_rel = new char[size][size - 1];
            char[][] ver_rel = new char[size - 1][size];

            int row = 0;

            while ( (line = br.readLine()) != null) {
                String[] lineValues = line.split(",");
                if (lineValues.length != rowSize) {
                    throw new IllegalFileFormatException("Illegal entry in file " + path + " : " + line + " row = " + row + " length = " + lineValues.length);
                }

                // need to toggle between cell line and relation line
                if (row % 2 == 0) {
                    for (int col = 0; col < rowSize; col++) {
                        if (col % 2 == 0 ) {
                            values[row / 2][col / 2] = Integer.parseInt(lineValues[col]);
                        }
                        else {
                            hor_rel[row / 2][(col - 1) / 2] = lineValues[col].charAt(0);
                        }
                    }
                }
                else {
                    for (int col = 0; col < rowSize; col++) {
                        if (col % 2 == 0) {
                            ver_rel[(row - 1) / 2][col / 2] = lineValues[col].charAt(0);
                        }
                    }
                }
                row++;
            }

            reset();

            importArray(size, values);
            importHorizonalRelations(size, hor_rel);
            importVerticalRelations(size, ver_rel);
        }
    }

    @Override
    public void exportFile(Path path) throws IOException {
        OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.WRITE};

        try (BufferedWriter writer = Files.newBufferedWriter(path, options )) {
            writer.append("size=");
            writer.append(Integer.toString(maxValue));
            writer.append("\n");

            for (int row = 1; row < maxValue; row++) {
                writeOneLine(writer, row);

                writeOneRelationLine(writer, row);
            }
            // and the final row
            writeOneLine(writer, maxValue);
        }
    }

    private void writeOneRelationLine(BufferedWriter writer, int row) throws IOException {
        writer.append(getRelation(row, 1, Direction.Vertical, "-"));
        writer.append(",-");
        for (int col = 2; col < maxValue; col++) {
            writer.append(",");
            writer.append(getRelation(row, col, Direction.Vertical, "-"));
            writer.append(",-");
        }
        writer.append(",");
        writer.append(getRelation(row, maxValue, Direction.Vertical, "-"));
        writer.append("\n");
    }

    private void writeOneLine(BufferedWriter writer, int row) throws IOException {
        writer.append(Integer.toString(getValue(row, 1)));
        writer.append(",");
        writer.append(getRelation(row, 1, Direction.Horizontal, "-"));

        for (int col = 2; col < maxValue; col++) {
            writer.append(",");
            writer.append(Integer.toString(getValue(row,col)));
            writer.append(",");
            writer.append(getRelation(row, col, Direction.Horizontal, "-"));
        }
        // and the last value in the row
        writer.append(",");
        writer.append(Integer.toString(getValue(row,maxValue)));
        writer.append("\n");
    }

    @Override
    public void showMarkUp() {

    }

    @Override
    public void showHints(int level) {

    }

    @Override
    public String toCLIString() {
        StringBuilder b = new StringBuilder();
        // row of numbers indicating each cell position
        b.append("     ");
        for (int i = 1; i <= maxValue; i++) {
            b.append(i);
            b.append("   ");
        }
        b.append("\n");

        // top border
        drawBorder(b);

        // now the cell content interspersed with the relations
        for (int row = 1; row < maxValue; row++) {
            drawContentRow(b, row);
            drawRelationRow(b, row);
        }
        // last row of cell content separately
        drawContentRow(b, maxValue);
        drawRelationRow(b, maxValue);

        // bottom border
        drawBorder(b);

        return b.toString();
    }

    private void drawBorder(StringBuilder b) {
        b.append("   +");
        for (int i = 0; i < maxValue * 3 + maxValue - 1; i++) {
            b.append("-");
        }
        b.append("+");
        b.append("\n");
    }

    private void drawContentRow(StringBuilder b, int row) {
        b.append(" ");
        b.append(row);
        b.append(" |");
        for (int col = 1; col < maxValue; col++) {
            b.append(" ");
            b.append(getValueAsString(row, col));
            b.append(" ");
            b.append(getRelation(row, col, Direction.Horizontal, " "));
        }
        b.append(" ");
        b.append(getValueAsString(row, maxValue));
        b.append(" |\n");
    }

    private void drawRelationRow(StringBuilder b, int row) {
        b.append("   |");
        for (int col = 1; col < maxValue; col++) {
            b.append(" ");
            b.append(getRelation(row, col, Direction.Vertical, " "));
            b.append("  ");
        }
        b.append(" ");
        b.append(getRelation(row, maxValue, Direction.Vertical, " "));
        b.append(" |\n");
    }

    private enum Direction {
        Horizontal,
        Vertical
    }

    private String getRelation(int row, int col, Direction direction, String emptyString) {
        Point from = new Point(row, col);

        Point to;
        if (direction == Direction.Horizontal) {
            to = new Point(row, col + 1);
        }
        else {
            to = new Point(row + 1, col);
        }

        Tuple tuple = new Tuple(from, to);
        Relation relation = relations.get(tuple);

        String result = emptyString;
        if (relation != null) {
            if (relation.getClass() == GreaterThan.class) {
                if (direction == Direction.Horizontal) {
                    result = ">";
                } else {
                    result = "v";
                }
            } else if (relation.getClass() == LessThan.class) {
                if (direction == Direction.Horizontal) {
                    result = "<";
                } else {
                    result = "^";
                }
            }
        }
        return result;
    }

    @Override
    public int getLow() {
        return 1;
    }

    @Override
    public int getHigh() {
        return maxValue;
    }

    @Override
    public void createRandomPuzzle() {

    }
}