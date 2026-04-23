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

import sudoku.*;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;
import sudoku.swing.PuzzleTableModel;
import sudoku.swing.StatusListener;
import sudoku.unit.Direction;

public class FutoshikiTableModel extends PuzzleTableModel {

    public FutoshikiTableModel(Futoshiki puzzle) {
        super(puzzle, 2 * puzzle.getMaxValue() - 1, 2 * puzzle.getMaxValue() - 1);
    }

    private Futoshiki futoshiki() {
        return (Futoshiki) puzzle;
    }

    // Dynamic: grid size changes when puzzle is reset to a new size
    @Override
    public int getRowCount() {
        return 2 * puzzle.getMaxValue() - 1;
    }

    @Override
    public int getColumnCount() {
        return 2 * puzzle.getMaxValue() - 1;
    }

    // Cell type predicates (0-based table coordinates)
    public static boolean isNumberCell(int row, int col) {
        return row % 2 == 0 && col % 2 == 0;
    }

    public static boolean isHorizontalRelation(int row, int col) {
        return row % 2 == 0 && col % 2 == 1;
    }

    public static boolean isVerticalRelation(int row, int col) {
        return row % 2 == 1 && col % 2 == 0;
    }

    // Convert 0-based table coordinates to 1-based puzzle coordinates
    private Point toPuzzlePoint(int tableRow, int tableCol) {
        return new Point(tableRow / 2 + 1, tableCol / 2 + 1);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (!isNumberCell(rowIndex, columnIndex)) return false;
        if (puzzle.isSolved()) return false;
        Point p = toPuzzlePoint(rowIndex, columnIndex);
        return !puzzle.isReadOnly(p);
    }

    @Override
    public CellWrapper getValueAt(int rowIndex, int columnIndex) {
        if (!isNumberCell(rowIndex, columnIndex)) return null;
        Point p = toPuzzlePoint(rowIndex, columnIndex);
        Cell cell = puzzle.getCells().get(p);
        if (cell.getValue() == 0) {
            int illegal = illegalEntries.getOrDefault(p, 0);
            return new CellWrapper(cell, illegal);
        }
        return new CellWrapper(cell, 0);
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (!isNumberCell(row, col)) return;

        Point p = toPuzzlePoint(row, col);
        String stringValue = (String) value;
        int intValue = 0;
        if (!stringValue.isEmpty()) {
            intValue = NumberConverter.getCharAsValue(stringValue.charAt(0));
        } else {
            stringValue = "0";
        }

        CellWrapper wrapper = getValueAt(row, col);
        String previousValue = Integer.toString(wrapper.getVisibleValue());
        if (previousValue.equals(stringValue)) return;

        if (!isUndoAction) {
            undoManager.addEdit(new PuzzleUndo(stringValue, previousValue, row, col));
        }
        isUndoAction = false;

        try {
            illegalEntries.remove(p);
            puzzle.setValue(p, intValue);
        } catch (IllegalCellPositionException e) {
            System.err.println("Should never happen " + e);
        } catch (CellContentException e) {
            illegalEntries.put(p, intValue);
            try {
                puzzle.setValue(p, 0);
            } catch (IllegalCellPositionException | CellContentException e1) {
                System.err.println("Should never happen " + e);
            }
        }

        fireTableCellUpdated(row, col);

        // Refresh all cells that share a constraint with this cell
        Cell cell = wrapper.getCell();
        for (var constraint : cell.getConstraints()) {
            for (Cell c : constraint.getCells()) {
                Point point = c.getLocation();
                int tableRow = 2 * (point.x() - 1);
                int tableCol = 2 * (point.y() - 1);
                fireTableCellUpdated(tableRow, tableCol);
            }
        }

        listeners.forEach(StatusListener::statusChanged);
    }

    @Override
    public boolean cellExists(int row, int col) {
        return isNumberCell(row, col);
    }

    @Override
    public boolean isChanged(int row, int col, Object editorValue) {
        if (!isNumberCell(row, col)) return false;
        String stringValue = (String) editorValue;
        if (stringValue.isEmpty()) stringValue = "0";
        CellWrapper wrapper = getValueAt(row, col);
        if (wrapper == null) return false;
        return !Integer.toString(wrapper.getVisibleValue()).equals(stringValue);
    }

    // Returns the display symbol for a relation cell
    public int getMaxValue() {
        return puzzle.getMaxValue();
    }

    public String getRelationSymbol(int tableRow, int tableCol) {
        int puzzleRow = tableRow / 2 + 1;
        int puzzleCol = tableCol / 2 + 1;
        String raw;
        if (isHorizontalRelation(tableRow, tableCol)) {
            raw = futoshiki().getRelation(puzzleRow, puzzleCol, Direction.Horizontal, "");
        } else if (isVerticalRelation(tableRow, tableCol)) {
            raw = futoshiki().getRelation(puzzleRow, puzzleCol, Direction.Vertical, "");
        } else {
            return "";
        }
        return raw; // "v", "^", ">", "<" — all ASCII, uniform size
    }

    // Updates a relation from a CrossPopup symbol selection
    public void setRelationFromSymbol(int tableRow, int tableCol, String symbol) {
        int puzzleRow = tableRow / 2 + 1;
        int puzzleCol = tableCol / 2 + 1;
        Futoshiki f = futoshiki();

        if (isHorizontalRelation(tableRow, tableCol)) {
            char ch = switch (symbol) {
                case ">" -> '>';
                case "<" -> '<';
                default -> '-'; // "○" or anything else clears the relation
            };
            f.setHorizontalRelation(puzzleRow, puzzleCol, ch);
        } else if (isVerticalRelation(tableRow, tableCol)) {
            char ch = switch (symbol) {
                case "⌄" -> 'v';
                case "⌃" -> '^';
                default -> '-';
            };
            f.setVerticalRelation(puzzleRow, puzzleCol, ch);
        }

        // Fire updates for the relation cell and its adjacent number cells
        fireTableCellUpdated(tableRow, tableCol);
        if (isHorizontalRelation(tableRow, tableCol)) {
            if (tableCol > 0) fireTableCellUpdated(tableRow, tableCol - 1);
            if (tableCol < getColumnCount() - 1) fireTableCellUpdated(tableRow, tableCol + 1);
        } else if (isVerticalRelation(tableRow, tableCol)) {
            if (tableRow > 0) fireTableCellUpdated(tableRow - 1, tableCol);
            if (tableRow < getRowCount() - 1) fireTableCellUpdated(tableRow + 1, tableCol);
        }
        listeners.forEach(StatusListener::statusChanged);
    }
}
