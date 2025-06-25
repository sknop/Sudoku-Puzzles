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

package sudoku.swing;


import net.sourceforge.argparse4j.ArgumentParserBuilder;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import sudoku.Cell;
import sudoku.Puzzle;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalFileFormatException;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.function.Consumer;

public abstract class SwingPuzzle implements StatusListener
{
    protected final int cellSize = getCellSize();
    protected Puzzle puzzle = createPuzzle();

    protected JFrame frame;
    protected JTable table;
    protected JLabel solved;
    protected JLabel filledCells;
    protected JLabel totalPotentialValues;

    protected Options options = new Options();

    PuzzleTableModel tableModel;

    JFileChooser fileChooser = new JFileChooser();
    File lastDirectory = new File(".");

    public SwingPuzzle(String name, String[] args) throws ArgumentParserException,
            IOException,
            IllegalFileFormatException,
            CellContentException
    {
        tableModel = createTableModel();
        tableModel.addListener(this);

        ArgumentParserBuilder builder = ArgumentParsers.newFor(name).addHelp(true);

        ArgumentParser parser = builder.build();
        parser.addArgument("-i", "--input").
                help("Input file, if not set, create empty puzzle");

        Namespace options = parser.parseArgs(args);

        String fileName = options.get("input");
        if (fileName != null) {
            Path path = FileSystems.getDefault().getPath(fileName);
            puzzle.importFile(path);
        }

        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    protected void initialize() {
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setBounds(100, 100, 450, 450);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        table = createTable(tableModel);

        table.setCellSelectionEnabled(true);
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);

        // table.setDefaultEditor(CellWrapper.class, createCellEditor(options));
        // table.setDefaultRenderer(CellWrapper.class, createCellEditor(options));
        table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

        final int height = cellSize;
        final int width = cellSize;

        TableColumnModel cm = table.getColumnModel();
        table.setRowHeight(height);
        for (int c = 0; c < cm.getColumnCount(); c++) {
            TableColumn tc = cm.getColumn(c);
            tc.setPreferredWidth(width);
            tc.setMinWidth(width);
            tc.setMaxWidth(width);
        }

        ListSelectionModel cellSelectionModel = table.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        frame.getContentPane().add(table, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(2, 3));
        bottomPanel.add(buttons, BorderLayout.WEST);

        createButtons(buttons);

        JPanel reports = new JPanel();
        reports.setLayout(new GridLayout(4,2,0,0));
        Dimension reportSize = new Dimension(180,90);
        reports.setMaximumSize(reportSize);
        reports.setMinimumSize(reportSize);
        reports.setPreferredSize(reportSize);
        bottomPanel.add(reports, BorderLayout.EAST);

        JLabel optionsLabel = new JLabel("Hints :");
        reports.add(optionsLabel);

        final JComboBox<String> hintOptions = new JComboBox<>();
        hintOptions.addItem("None");
        hintOptions.addItem("Markup");
        hintOptions.addItem("Hints 1");
        hintOptions.addItem("Hints 2");
        hintOptions.addItem("Hints 3");
        reports.add(hintOptions);

        hintOptions.addActionListener( e -> {
            options.setHintLevel(hintOptions.getSelectedIndex());
            tableModel.fireTableDataChanged();
            if (table.isEditing())
                table.getCellEditor().stopCellEditing();
        });

        JLabel solvedLabel = new JLabel("Status :");
        reports.add(solvedLabel);

        solved = new JLabel();
        reports.add(solved);

        JLabel filledCellsLabel = new JLabel("Filled cells :");
        reports.add(filledCellsLabel);

        filledCells = new JLabel();
        reports.add(filledCells);

        JLabel totalPotentialValuesLabel = new JLabel("Potentials :");
        reports.add(totalPotentialValuesLabel);

        totalPotentialValues = new JLabel();
        reports.add(totalPotentialValues);

        statusChanged();

        UndoKeys.addUndoKeys(frame.getRootPane(), tableModel);

        frame.pack();
    }

    private void createButtons(JPanel buttons) {
        JButton createButton = new JButton("Create");
        createButton.addActionListener( e -> {
            tableModel.clearIllegal();
            puzzle.createRandomPuzzle();
            tableModel.fireTableDataChanged();
            statusChanged();
        });
        buttons.add(createButton);

        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener( e -> {
            // cheeky hack - remove selection so that the cell is not blocked
            table.editCellAt(-1, -1);
            table.getSelectionModel().clearSelection();

            puzzle.solveBruteForce();
            tableModel.fireTableDataChanged();
            solved.setText("Cheated");
            statusChanged();
        });
        buttons.add(solveButton);

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener( e -> {
            frame.dispose();
            System.exit(0);
        });
        buttons.add(quitButton);

        JButton loadButton = new JButton("Load");
        loadButton.addActionListener( e -> {
            fileChooser.setCurrentDirectory(lastDirectory);
            int returnValue = fileChooser.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                Path path = FileSystems.getDefault().getPath(file.getPath());

                try {
                    puzzle.reset();
                    puzzle.importFile(path);
                } catch (IOException |IllegalFileFormatException |CellContentException e1) {
                    e1.printStackTrace();
                }
            }
            statusChanged();
        });
        buttons.add(loadButton);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener( e -> {
            fileChooser.setCurrentDirectory(lastDirectory);
            int returnValue = fileChooser.showSaveDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                Path path = FileSystems.getDefault().getPath(file.getPath());

                try {
                    puzzle.exportFile(path);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        buttons.add(saveButton);

        JToggleButton readWriteButton = new JToggleButton("Write");
        readWriteButton.addActionListener( e -> {
            Consumer<Cell> command;

            // cheeky hack - remove selection so that the cell is not blocked
            table.editCellAt(-1, -1);
            table.getSelectionModel().clearSelection();

            if (readWriteButton.getText().equals("Write")) {
                readWriteButton.setText("R/O");
                command = Cell::makeReadOnly;
            }
            else {
                readWriteButton.setText("Write");
                command = Cell::makeWritable;
            }

            for (Cell c : puzzle.getCells().values()) {
                command.accept(c);
            }

            tableModel.fireTableDataChanged();
        });
        buttons.add(readWriteButton);

    }

    protected abstract PuzzleTableModel createTableModel();

    protected abstract JTable createTable(TableModel model);

    protected abstract int getCellSize();

    protected abstract Puzzle createPuzzle();

    protected abstract PuzzleCellEditor createCellEditor(Options options);

    @Override
    public void statusChanged() {
        if (tableModel.anyIllegalValues()) {
            solved.setText("Illegal");
        }
        else {
            if (puzzle.isSolved()) {
                solved.setText("Solved!");
            } else {
                int solutions = puzzle.isUnique();
                if (solutions == 1) {
                    solved.setText("Unsolved");
                } else if (solutions == 0) {
                    solved.setText("No solutions");
                } else {
                    solved.setText("Not unique");
                }

            }
        }
        filledCells.setText(Integer.toString(puzzle.getTotalFilledCells()));
        totalPotentialValues.setText(Integer.toString(puzzle.getTotalPossibleValues()));
    }
}
