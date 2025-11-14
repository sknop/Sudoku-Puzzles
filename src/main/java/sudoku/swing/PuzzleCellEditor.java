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
        JTable table;
        protected int row;
        protected int column;
        protected int limit;

        public ArrowAction(JTable table, int row, int column, int limit) {
            this.table = table;
            this.row = row;
            this.column = column;
            this.limit = limit;
        }

        protected void moveToCell(int newRow, int newColumn) {
            table.editCellAt(newRow, newColumn);
            table.changeSelection(newRow, newColumn, false, false);
        }
    }

    public static class UpAction extends ArrowAction
    {

        public UpAction(JTable table, int row, int column, int limit) {
            super(table, row, column, limit);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int newRow = row - 1;
            if (newRow < 0) newRow = limit;
            moveToCell(newRow, column);
        }
    }

    public static class DownAction extends ArrowAction
    {
        public DownAction(JTable table, int row, int column, int limit) {
            super(table, row, column, limit);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int newRow = row + 1;
            if (newRow > limit) newRow = 0;
            moveToCell(newRow, column);
        }
    }

    public static class LeftAction extends ArrowAction
    {
        public LeftAction(JTable table, int row, int column, int limit) {
            super(table, row, column, limit);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int newColumn = column - 1;
            if (newColumn < 0) newColumn = limit;
            moveToCell(row, newColumn);
        }
    }

    public static class RightAction extends ArrowAction
    {
        public RightAction(JTable table, int row, int column, int limit) {
            super(table, row, column, limit);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int newColumn = column + 1;
            if (newColumn > limit) newColumn = 0;
            moveToCell(row, newColumn);
        }
    }

    public ArrowAction getUpAction(JTable table, int row, int column) {
        return new UpAction(table, row, column, 8);
    }

    public ArrowAction getDownAction(JTable table, int row, int column) {
        return new DownAction(table, row, column, 8);
    }

    public ArrowAction getLeftAction(JTable table, int row, int column) {
        return new LeftAction(table, row, column, 8);
    }

    public ArrowAction getRightAction(JTable table, int row, int column) {
        return new RightAction(table, row, column, 8);
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
        am.put("Arrow.up", getUpAction(table, row, column));
        am.put("Arrow.down", getDownAction(table, row, column));
        am.put("Arrow.left", getLeftAction(table, row, column));
        am.put("Arrow.right", getRightAction(table, row, column));

        UndoKeys.addUndoKeys(textField, (UndoTableModel) table.getModel());

        return panel;
    }

}


