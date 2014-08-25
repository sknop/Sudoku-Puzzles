package sudoku.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.BitSet;

import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
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