/*******************************************************************************
 * Copyright (c) 2014 Sven Erik Knop.
 * Licensed under the EUPL V.1.1
 * <p>
 * This Software is provided to You under the terms of the European 
 * Union Public License (the "EUPL") version 1.1 as published by the 
 * European Union. Any use of this Software, other than as authorized 
 * under this License is strictly prohibited (to the extent such use 
 * is covered by a right of the copyright holder of this Software).
 * <p>
 * This Software is provided under the License on an "AS IS" basis and 
 * without warranties of any kind concerning the Software, including 
 * without limitation merchantability, fitness for a particular purpose, 
 * absence of defects or errors, accuracy, and non-infringement of 
 * intellectual property rights other than copyright. This disclaimer 
 * of warranty is an essential part of the License and a condition for 
 * the grant of any rights to this Software.
 * <p>
 * For more details, see http://joinup.ec.europa.eu/software/page/eupl.
 * <p>
 * Contributors:
 *     2014 - Sven Erik Knop - initial API and implementation
 *******************************************************************************/
package sudoku.sudoku;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import sudoku.Puzzle;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalFileFormatException;
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


public class SwingSudoku extends SwingPuzzle
{
	/**
	 * Create the application.
	 * @throws ArgumentParserException 
	 * @throws CellContentException 
	 * @throws IllegalFileFormatException 
	 * @throws IOException 
	 */
	public SwingSudoku(String[] args) 
			throws ArgumentParserException, 
				   IOException, 
				   IllegalFileFormatException, 
				   CellContentException
    {
        super("Sudoku", args);
    }

    @Override
    protected int getCellSize() {
        return 50;
    }

    @Override
    protected Puzzle createPuzzle() {
        return new Sudoku();
    }

    @Override
    protected PuzzleCellEditor createCellEditor(Options options) {
        return new PuzzleCellEditor(options);
    }

    @Override
    protected PuzzleTableModel createTableModel() {
        return new PuzzleTableModel(puzzle, 9,9);
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
                    int bottom = ((row -2) % 3 == 0) ? 1 : 0;
                    int right = ((column - 2) % 3 == 0) ? 1 : 0;

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
                SwingSudoku window = new SwingSudoku(args);
                window.frame.setVisible(true);
                window.frame.setResizable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
		});
	}

}

