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

package sudoku.supersudoku;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.internal.HelpScreenException;
import sudoku.Puzzle;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalFileFormatException;
import sudoku.sudoku.Sudoku;
import sudoku.swing.Options;
import sudoku.swing.PuzzleCellEditor;
import sudoku.swing.PuzzleTableModel;
import sudoku.swing.SwingPuzzle;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class SwingSuper extends SwingPuzzle
{
    /**
     * Create the application.
     * @throws ArgumentParserException
     * @throws CellContentException
     * @throws IllegalFileFormatException
     * @throws IOException
     */
    public SwingSuper(String[] args)
            throws ArgumentParserException,
            IOException,
            IllegalFileFormatException,
            CellContentException
    {
        super("Super", args);
    }

    @Override
    protected int getCellSize() {
        return 50;
    }

    @Override
    protected Puzzle createPuzzle() {
        return new SuperSudoku();
    }

    @Override
    protected PuzzleCellEditor createCellEditor(Options options) {
        return new SuperCellEditor(options);
    }

    @Override
    protected PuzzleTableModel createTableModel() {
        return new PuzzleTableModel(puzzle, 16,16);
    }

    @Override
    protected JTable createTable(TableModel model) {

        PuzzleCellEditor editor = createCellEditor(options);
        PuzzleCellEditor renderer = createCellEditor(options);

        return new JTable(model) {
            @Override
            public Component prepareRenderer(
                    TableCellRenderer renderer, int row, int column)
            {
                Component c = super.prepareRenderer(renderer, row, column);
                JComponent jc = (JComponent)c;

                int top = 1;
                int left = 1;
                int bottom = ((row - 3) % 4 == 0) ? 1 : 0;
                int right = ((column - 3) % 4 == 0) ? 1 : 0;

                jc.setBorder(new MatteBorder(top, left, bottom, right, Color.BLACK) );


                //  Use bold font on selected row

                return c;
            }

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                return editor;
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                return renderer;
            }

            @Override
            public void changeSelection(final int row, final int column, boolean toggle, boolean extend) {
                super.changeSelection(row, column, toggle, extend);
                PuzzleTableModel model = (PuzzleTableModel) getModel();
                if (model.isCellEditable(row, column)) {
                    this.editCellAt(row, column);
                    this.transferFocus();
                }
            }
        };
    }

    /**
     * Launch the application.
     */
    public static void main(final String[] args) {
        EventQueue.invokeLater( () -> {
            try {
                SwingSuper window = new SwingSuper(args);
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
