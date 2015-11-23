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

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void importFile(Path path) throws IOException, IllegalFileFormatException, CellContentException {

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
