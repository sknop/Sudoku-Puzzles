/*******************************************************************************
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
 * Contributors:
 *     2015 - Sven Erik Knop - initial API and implementation
 *******************************************************************************/
package sudoku.samurai;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import javax.swing.*;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import sudoku.CellWrapper;
import sudoku.Point;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalFileFormatException;
import sudoku.swing.Options;
import sudoku.samurai.SamuraiCellEditor;
import sudoku.swing.UndoKeys;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;


public class SwingSamurai extends Samurai
{
    public final int SIZE = 40;

    JFrame frame;
    JTable table;
    JLabel solved;

    Options options = new Options();

    SamuraiTableModel tableModel;

    Map<Point, Integer> illegalEntries = new HashMap<>();
    JFileChooser fileChooser = new JFileChooser();
    File lastDirectory = new File(".");

    /**
     * Create the application.
     * @throws ArgumentParserException
     * @throws CellContentException
     * @throws IllegalFileFormatException
     * @throws IOException
     */
    public SwingSamurai(String[] args)
            throws ArgumentParserException,
            IOException,
            IllegalFileFormatException,
            CellContentException {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("Samurai",true);
        parser.addArgument("-i", "--input");

        Namespace options = parser.parseArgs(args);

        String fileName = options.get("input");
        if (fileName != null) {
            Path path = FileSystems.getDefault().getPath(fileName);
            this.importFile(path);
        }

        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    @SuppressWarnings("serial")
    private void initialize() {
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setBounds(100, 100, 450, 450);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        tableModel = new SamuraiTableModel(this, 21,21);
        table = new JTable(tableModel) {
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

                return c;
            }

            @Override
            public void changeSelection(final int row, final int column, boolean toggle, boolean extend)
            {
                super.changeSelection(row, column, toggle, extend);
                SamuraiTableModel model = (SamuraiTableModel) getModel();
                if (model.isCellEditable(row, column)) {
                    this.editCellAt(row, column);
                    this.transferFocus();
                }
            }

            class SquarePanel extends JPanel {
                public SquarePanel() {
                    setBorder(new LineBorder(Color.GRAY));
                    setBackground(Color.DARK_GRAY);
                }

                @Override
                public Dimension getMinimumSize() {
                    return getPreferredSize();
                }

                @Override
                public Dimension getMaximumSize() {
                    return getPreferredSize();
                }

                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(SIZE,SIZE);
                }
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if ( SamuraiTableModel.isVisible(row, column) ) {
                    return super.getCellRenderer(row, column);
                }
                else {
                    return (t,  v,  i,  h,  r,  c) ->  new SquarePanel();
                }
            }
        };

        table.setCellSelectionEnabled(true);
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);

        table.setDefaultEditor(CellWrapper.class, new SamuraiCellEditor(options));
        table.setDefaultRenderer(CellWrapper.class, new SamuraiCellEditor(options));
        table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

        final int height = SIZE;
        final int width = SIZE;

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
        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(2, 3));
        bottomPanel.add(buttons);

        createButtons(buttons);

        JPanel reports = new JPanel();
        reports.setLayout(new GridLayout(2,2,0,0));
        Dimension reportSize = new Dimension(180,60);
        reports.setMaximumSize(reportSize);
        reports.setMinimumSize(reportSize);
        reports.setPreferredSize(reportSize);
        bottomPanel.add(reports);

        JLabel optionsLabel = new JLabel("Hints :");
        reports.add(optionsLabel);

        final JComboBox<String> hintOptions = new JComboBox<>();
        hintOptions.addItem("None");
        hintOptions.addItem("Markup");
        hintOptions.addItem("Hints 1");
        hintOptions.addItem("Hints 2");
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

        tableModel.setStatus();

        UndoKeys.addUndoKeys(frame.getRootPane(), tableModel);

        frame.pack();
    }

    private void createButtons(JPanel buttons) {
        JButton createButton = new JButton("Create");
        createButton.addActionListener( e -> {
            illegalEntries.clear();
            createRandomPuzzle();
            tableModel.fireTableDataChanged();
        });
        buttons.add(createButton);

        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener( e -> {
            // cheeky hack - remove selection so that the cell is not blocked
            table.editCellAt(-1, -1);
            table.getSelectionModel().clearSelection();

            solveBruteForce();
            tableModel.fireTableDataChanged();
            solved.setText("Cheated");
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
                    importFile(path);
                } catch (IOException |IllegalFileFormatException |CellContentException e1) {
                    e1.printStackTrace();
                }
            }
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
                    exportFile(path);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        buttons.add(saveButton);

    }

    /**
     * Launch the application.
     */
    public static void main(final String[] args) {
        EventQueue.invokeLater( () -> {
            try {
                SwingSamurai window = new SwingSamurai(args);
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

