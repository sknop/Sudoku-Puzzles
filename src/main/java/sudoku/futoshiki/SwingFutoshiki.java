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
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class SwingFutoshiki extends SwingPuzzle {

    private static final int RELATION_SIZE = 20;
    // Placeholder symbols shown in empty relation cells so users know where to click
    private static final String H_PLACEHOLDER = "—";
    private static final String V_PLACEHOLDER = "⋮";

    private JComboBox<Integer> sizeCombo;

    public SwingFutoshiki(String[] args)
            throws ArgumentParserException,
                   IOException,
                   IllegalFileFormatException,
                   CellContentException {
        super("Futoshiki", args);
    }

    @Override
    protected int getCellSize() {
        return 40;
    }

    @Override
    protected Puzzle createPuzzle() {
        return new Futoshiki(5);
    }

    @Override
    protected PuzzleTableModel createTableModel() {
        return new FutoshikiTableModel((Futoshiki) puzzle);
    }

    @Override
    protected PuzzleCellEditor createCellEditor(Options options) {
        return new FutoshikiCellEditor(options);
    }

    @Override
    protected JTable createTable(TableModel model) {
        return new JTable(model) {
            final PuzzleCellEditor numberEditor = createCellEditor(options);
            final PuzzleCellEditor numberRenderer = createCellEditor(options);

            final TableCellRenderer relationCellRenderer = (tbl, value, isSelected, hasFocus, row, col) -> {
                FutoshikiTableModel ftm = (FutoshikiTableModel) tbl.getModel();
                String symbol = ftm.getRelationSymbol(row, col);
                boolean empty = symbol.isEmpty();
                String display = empty
                        ? (FutoshikiTableModel.isHorizontalRelation(row, col) ? H_PLACEHOLDER : V_PLACEHOLDER)
                        : symbol;
                JLabel label = new JLabel(display, SwingConstants.CENTER);
                label.setFont(new Font("Lucida Grande", Font.PLAIN, empty ? 12 : 14));
                label.setForeground(empty ? Color.GRAY : Color.BLACK);
                label.setOpaque(true);
                label.setBackground(UIManager.getColor("Panel.background"));
                return label;
            };

            final TableCellRenderer emptyCellRenderer = (tbl, value, isSelected, hasFocus, row, col) -> {
                JPanel p = new JPanel();
                p.setBackground(UIManager.getColor("Panel.background"));
                return p;
            };

            {
                setShowGrid(false);
                setIntercellSpacing(new Dimension(0, 0));
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (c instanceof JComponent jc) {
                    if (FutoshikiTableModel.isNumberCell(row, column)) {
                        jc.setBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK));
                    } else {
                        jc.setBorder(BorderFactory.createEmptyBorder());
                    }
                }
                return c;
            }

            @Override
            public void changeSelection(int row, int column, boolean toggle, boolean extend) {
                super.changeSelection(row, column, toggle, extend);
                FutoshikiTableModel ftm = (FutoshikiTableModel) getModel();
                if (FutoshikiTableModel.isNumberCell(row, column) && ftm.isCellEditable(row, column)) {
                    this.editCellAt(row, column);
                    this.transferFocus();
                }
            }

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                return numberEditor;
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (FutoshikiTableModel.isNumberCell(row, column)) return numberRenderer;
                if (FutoshikiTableModel.isHorizontalRelation(row, column) ||
                        FutoshikiTableModel.isVerticalRelation(row, column)) return relationCellRenderer;
                return emptyCellRenderer;
            }
        };
    }

    @Override
    protected void initialize() {
        super.initialize();

        // Wrap the table in a centering panel so it doesn't hug the left edge
        Container contentPane = frame.getContentPane();
        contentPane.remove(table);
        JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        tablePanel.add(table);
        contentPane.add(tablePanel, BorderLayout.CENTER);

        applyFutoshikiLayout();
        contentPane.add(createSizePanel(), BorderLayout.NORTH);
        addRelationMouseListener();
        frame.pack();
    }

    @Override
    protected void afterBulkUpdate() {
        frame.pack();
    }

    @Override
    protected void afterLoadFile() {
        tableModel.fireTableStructureChanged();
        applyFutoshikiLayout();
        if (sizeCombo != null) {
            sizeCombo.setSelectedItem(puzzle.getMaxValue());
        }
        frame.pack();
    }

    private JPanel createSizePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        panel.add(new JLabel("Size:"));
        sizeCombo = new JComboBox<>(new Integer[]{5, 6, 7, 8, 9});
        sizeCombo.setSelectedItem(puzzle.getMaxValue());
        sizeCombo.addActionListener(_ -> {
            int newSize = (Integer) sizeCombo.getSelectedItem();
            if (newSize != puzzle.getMaxValue()) {
                changeSize(newSize);
            }
        });
        panel.add(sizeCombo);
        return panel;
    }

    private void changeSize(int newSize) {
        boolean wasWide = isWideLayout();
        table.editCellAt(-1, -1);
        table.getSelectionModel().clearSelection();
        tableModel.clearIllegal();
        ((Futoshiki) puzzle).reset(newSize);
        tableModel.fireTableStructureChanged();
        applyFutoshikiLayout();
        if (isWideLayout() != wasWide) {
            rebuildBottomPanel();
        }
        statusChanged();
        frame.pack();
    }

    private void applyFutoshikiLayout() {
        int gridSize = tableModel.getRowCount();

        // Reset global row height first so the JTable's internal SizeSequence
        // is rebuilt cleanly before we apply per-row overrides.
        table.setRowHeight(cellSize);
        for (int r = 0; r < gridSize; r++) {
            table.setRowHeight(r, r % 2 == 0 ? cellSize : RELATION_SIZE);
        }

        TableColumnModel cm = table.getColumnModel();
        for (int c = 0; c < cm.getColumnCount(); c++) {
            int width = c % 2 == 0 ? cellSize : RELATION_SIZE;
            TableColumn tc = cm.getColumn(c);
            tc.setPreferredWidth(width);
            tc.setMinWidth(width);
            tc.setMaxWidth(width);
        }
    }

    private void addRelationMouseListener() {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col < 0) return;
                if (FutoshikiTableModel.isHorizontalRelation(row, col) ||
                        FutoshikiTableModel.isVerticalRelation(row, col)) {
                    FutoshikiTableModel ftm = (FutoshikiTableModel) tableModel;
                    CrossPopup popup = new CrossPopup(symbol ->
                            ftm.setRelationFromSymbol(row, col, symbol));
                    popup.show(table, e.getX(), e.getY());
                }
            }
        });
    }

    public static void main(final String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                SwingFutoshiki window = new SwingFutoshiki(args);
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
