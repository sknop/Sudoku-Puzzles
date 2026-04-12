package sudoku.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import sudoku.*;

public class PuzzleCellEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer
{
    final Color backgroundColor = Color.WHITE;
    final Font bigFont = new Font("Lucida Grande", Font.BOLD, 28);
    final Font smallFont = new Font("Lucida Grande", Font.PLAIN, 8);

    JPanel panel;
    JTextField textField;
    JLabel label;
    Options options;

    public PuzzleCellEditor(Options options) {
        this.options = options;
    }

    @Override
    public Object getCellEditorValue() {
        return textField.getText();
    }

    private JPanel updateData(CellWrapper wrapper, boolean hasFocus) {
        panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));

        textField = new JTextField(1);
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(textField, BorderLayout.CENTER);

        textField.setColumns(1);
        textField.setFont(bigFont);

        textField.setBackground(backgroundColor);
        textField.setDocument(createFieldLimit());

        label = new JLabel();
        label.setFont(smallFont);
        label.setBackground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);

        if (wrapper != null) {
            Cell cell = wrapper.getCell();

            if (cell.getValue() > 0) {
                textField.setText(NumberConverter.getValueAsString(cell.getValue()));

                if (cell.isReadOnly()) {
                    textField.setForeground(Color.BLUE);
                } else {
                    textField.setForeground(Color.BLACK);
                }
            } else {
                if (wrapper.getIllegalValue() > 0) {
                    textField.setForeground(Color.RED);
                    textField.setText(NumberConverter.getValueAsString(wrapper.getIllegalValue()));
                }
            }

            if (hasFocus) {
                panel.setBackground(Color.green.darker());
            } else {
                panel.setBackground(backgroundColor);
            }

            if (options.getHintLevel() > 0) {
                label.setText(formatMarkup(cell.getHints(options.getHintLevel() - 1)));
            }
        }
        return panel;
    }

    protected FieldLimit createFieldLimit() {
        return new FieldLimit(9);
    }

    protected String formatMarkup(MarkUp set) {
        StringBuilder b = new StringBuilder();
        if (set.cardinality() > 0) {
            b.append("<html>");
            for (int i = 1; i <= 9; i++) {
                b.append(set.get(i) ? i : " ");
            }
            b.append("</html>");
        }
        return b.toString();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        CellWrapper wrapper = (CellWrapper) value;

        return updateData(wrapper, hasFocus);
    }

    public static abstract class ArrowAction extends AbstractAction
    {
        protected JTable table;
        protected PuzzleTableModel tableModel;

        public ArrowAction(JTable table, PuzzleTableModel tableModel) {
            this.table = table;
            this.tableModel = tableModel;
        }

        protected void moveToCell(int newRow, int newColumn) {
            table.editCellAt(newRow, newColumn);
            table.changeSelection(newRow, newColumn, false, false);
        }
    }

    public static class UpAction extends ArrowAction
    {
        public UpAction(JTable table, PuzzleTableModel tableModel) {
            super(table, tableModel);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            int col = table.getSelectedColumn();
            if (row < 0 || col < 0) return;
            int newRow = row;
            for (int i = 0; i < table.getRowCount(); i++) {
                newRow = newRow == 0 ? table.getRowCount() - 1 : newRow - 1;
                if (tableModel.cellExists(newRow, col)) break;
            }
            moveToCell(newRow, col);
        }
    }

    public static class DownAction extends ArrowAction
    {
        public DownAction(JTable table, PuzzleTableModel tableModel) {
            super(table, tableModel);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            int col = table.getSelectedColumn();
            if (row < 0 || col < 0) return;
            int newRow = row;
            for (int i = 0; i < table.getRowCount(); i++) {
                newRow = newRow == table.getRowCount() - 1 ? 0 : newRow + 1;
                if (tableModel.cellExists(newRow, col)) break;
            }
            moveToCell(newRow, col);
        }
    }

    public static class LeftAction extends ArrowAction
    {
        public LeftAction(JTable table, PuzzleTableModel tableModel) {
            super(table, tableModel);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            int col = table.getSelectedColumn();
            if (row < 0 || col < 0) return;
            int newCol = col;
            for (int i = 0; i < table.getColumnCount(); i++) {
                newCol = newCol == 0 ? table.getColumnCount() - 1 : newCol - 1;
                if (tableModel.cellExists(row, newCol)) break;
            }
            moveToCell(row, newCol);
        }
    }

    public static class RightAction extends ArrowAction
    {
        public RightAction(JTable table, PuzzleTableModel tableModel) {
            super(table, tableModel);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            int col = table.getSelectedColumn();
            if (row < 0 || col < 0) return;
            int newCol = col;
            for (int i = 0; i < table.getColumnCount(); i++) {
                newCol = newCol == table.getColumnCount() - 1 ? 0 : newCol + 1;
                if (tableModel.cellExists(row, newCol)) break;
            }
            moveToCell(row, newCol);
        }
    }

    public ArrowAction getUpAction(JTable table, PuzzleTableModel tableModel) {
        return new UpAction(table, tableModel);
    }

    public ArrowAction getDownAction(JTable table, PuzzleTableModel tableModel) {
        return new DownAction(table, tableModel);
    }

    public ArrowAction getLeftAction(JTable table, PuzzleTableModel tableModel) {
        return new LeftAction(table, tableModel);
    }

    public ArrowAction getRightAction(JTable table, PuzzleTableModel tableModel) {
        return new RightAction(table, tableModel);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        CellWrapper wrapper = (CellWrapper) value;

        JPanel panel = updateData(wrapper, true);

        InputMap im = textField.getInputMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "Arrow.up");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "Arrow.down");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "Arrow.left");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "Arrow.right");

        ActionMap am = textField.getActionMap();
        PuzzleTableModel model = (PuzzleTableModel) table.getModel();
        am.put("Arrow.up", getUpAction(table, model));
        am.put("Arrow.down", getDownAction(table, model));
        am.put("Arrow.left", getLeftAction(table, model));
        am.put("Arrow.right", getRightAction(table, model));

        return panel;
    }

}


