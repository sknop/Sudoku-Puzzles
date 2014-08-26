package sudoku.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.BitSet;

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
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import sudoku.Cell;
import sudoku.CellWrapper;

@SuppressWarnings("serial")
public class CellEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer
{
	final Color backgroundColor = Color.WHITE;
	final Font bigFont = new Font("Lucida Grande", Font.BOLD, 28);
	final Font smallFont = new Font("Lucida Grande", Font.PLAIN, 8);
	
	JPanel panel;
	JTextField textField;
	JLabel label;
	
	public CellEditor() {
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
		
    	textField.setBackground( backgroundColor );
    	textField.setDocument(new FieldLimit(9));
    	
		label = new JLabel();
		label.setFont(smallFont);
		label.setBackground(Color.WHITE);
		label.setHorizontalAlignment(0);
		panel.add(label, BorderLayout.NORTH);

		if (wrapper == null) {
			System.out.println("Not supposed to happen, wrapper == null?");
		}
		else {
			Cell cell = wrapper.getCell();
	
	    	if (cell.getValue() > 0) {
	    		textField.setText(Integer.toString(cell.getValue()));
	
	    		if (cell.isReadOnly()) {
	        		textField.setForeground( Color.BLUE );
	        	}
	        	else {
	        		textField.setForeground( Color.BLACK );
	        	}
			}
			else {
				if ( wrapper.getIllegalValue() > 0 ) {
			        textField.setForeground( Color.RED );
			        textField.setText(Integer.toString(wrapper.getIllegalValue()));
			    }
			}
			
			if (hasFocus) {
				panel.setBackground(Color.green.darker());
			}
			else {
				panel.setBackground(backgroundColor);
			}
			
			label.setText( formatMarkup( cell.getMarkUp() ));
		}
		return panel;
	}

	private String formatMarkup(BitSet set) {
		StringBuilder b = new StringBuilder();
		for (int i = 1; i <= 9; i++) {
			b.append( set.get(i) ? i : " ");
		}
		return b.toString();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		CellWrapper wrapper = (CellWrapper) value;
		
		JPanel panel = updateData(wrapper, hasFocus);
		
		return panel;
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
    	
    	abstract class ArrowAction extends AbstractAction {
    		void moveToCell(int row, int column) {
				table.editCellAt(row, column);
			    table.changeSelection(row, column, false, false);
    		}
    	}
    	class UpAction extends ArrowAction {
			@Override
			public void actionPerformed(ActionEvent e) {
				int newRow = row - 1;
				if (newRow < 0) newRow = 8;
				int newColumn = column;
				moveToCell(newRow, newColumn);
			}
    	};
    	class DownAction extends ArrowAction {
			@Override
			public void actionPerformed(ActionEvent e) {
				int newRow = row + 1;
				if (newRow > 8) newRow = 0;
				int newColumn = column;
				moveToCell(newRow, newColumn);
			}
    	};
    	class LeftAction extends ArrowAction {
			@Override
			public void actionPerformed(ActionEvent e) {
				int newRow = row;
				int newColumn = column - 1;
				if (newColumn < 0) newColumn = 8;
				moveToCell(newRow, newColumn);
			}
    	};
    	class RightAction extends ArrowAction {
			@Override
			public void actionPerformed(ActionEvent e) {
				int newRow = row;
				int newColumn = column + 1;
				if (newColumn > 8) newColumn = 0;
				moveToCell(newRow, newColumn);
			}
    	};

    	ActionMap am = textField.getActionMap();
    	am.put("Arrow.up", new UpAction());
    	am.put("Arrow.down", new DownAction());
    	am.put("Arrow.left", new LeftAction());
    	am.put("Arrow.right", new RightAction());
    	
		return panel;
	}
	
}

@SuppressWarnings("serial")
class FieldLimit extends PlainDocument {
    private int limit;

    FieldLimit(int limit) {
       super();
       this.limit = limit;
    }

    static boolean isNumeric(String str)
    {
        for (char c : str.toCharArray())
        {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
    
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
       if (str == null)
         return;

       if (isNumeric(str)) {
	       int value = Integer.parseInt(str);
	       if ((value <= limit) && getLength() == 0) {
	         super.insertString(offset, str.toUpperCase(), attr);
	       }
       }
    }
}