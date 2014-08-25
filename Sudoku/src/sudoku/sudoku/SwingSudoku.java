/*******************************************************************************
 * Copyright (c) 2014 Sven Erik Knop.
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
 *     2014 - Sven Erik Knop - initial API and implementation
 *******************************************************************************/
package sudoku.sudoku;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.border.MatteBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import sudoku.Cell;
import sudoku.Point;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;
import sudoku.exceptions.IllegalFileFormatException;
import sudoku.unit.Unit;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

class CellWrapper {
	Cell cell;
	int illegalValue = 0;
	
	public CellWrapper(Cell cell, int illegalValue) {
		this.cell = cell;
		this.illegalValue = illegalValue;
	}
}

public class SwingSudoku extends Sudoku
{

	private JFrame frame;
	private JTable table;
	
	private AbstractTableModel tableModel;

	private Map<Point, Integer> illegalEntries = new HashMap<>();
	
	
	@SuppressWarnings("serial")
	class SudokuTableModel extends AbstractTableModel
	{
		@Override
		public int getRowCount() {
			return 9;
		}

		@Override
		public int getColumnCount() {
			return 9;
		}
		
		@Override
		public CellWrapper getValueAt(int rowIndex, int columnIndex) {
			Point p = new Point(rowIndex + 1, columnIndex + 1);
			Cell cell = cells.get(p);
			
			if (cell.getValue() == 0) {
				int illegal = 0;
				
				if (illegalEntries.containsKey(p)) {
					illegal = illegalEntries.get(p);
				}
				return new CellWrapper(cell, illegal);
			}
			else {
				return new CellWrapper(cell, 0);
			}
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
	        Point p = new Point(rowIndex + 1, columnIndex + 1);
			boolean isReadOnly = isReadOnly(p);
			return !isReadOnly;
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return CellWrapper.class;
		}
		
		@Override
		public void setValueAt(Object value, int row, int col) {
	        Point p = new Point(row + 1, col + 1);
	        String stringValue = (String) value;
	        int intValue = 0;
	        
	        if (!stringValue.isEmpty()) {
	        	intValue = Integer.parseInt(stringValue);
	        }

	        try {
				if (illegalEntries.containsKey(p)) {
					illegalEntries.remove(p);
				}
				setValue(p, intValue);
			} catch (IllegalCellPositionException e) {
				System.err.println("Should never happen " + e);
			} catch (CellContentException e) {
				illegalEntries.put(p, intValue);
				try {
					setValue(p, 0);
				} catch (IllegalCellPositionException | CellContentException e1) {
					System.err.println("Should never happen " + e);
				}
			}
	        
	        fireTableCellUpdated(row, col);
	        
	        Cell cell = cells.get(p);
	        for (Unit u : cell.getUnits()) {
	        	for (Cell c : u.getCells()) {
	        		Point point = c.getLocation();
	        		fireTableCellUpdated(point.getX() - 1, point.getY() - 1);
	        	}
	        }
	    }
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingSudoku window = new SwingSudoku(args);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

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
				   CellContentException {
		ArgumentParser parser = ArgumentParsers.newArgumentParser("CLI Sudoku",true);
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
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		tableModel = new SudokuTableModel();
		
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


        			//  Use bold font on selected row

        			return c;
        		}
		};

		
	    table.setCellSelectionEnabled(true);
	    table.setRowSelectionAllowed(false);
	    table.setColumnSelectionAllowed(false);

	    table.setDefaultEditor(CellWrapper.class, new CellEditor());
	    table.setDefaultRenderer(CellWrapper.class, new CellEditor());
	    table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
	    
//		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
//			Color backgroundColor = getBackground();
//			
//			@Override
//			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//				CellWrapper cellWrapper = (CellWrapper) value;
//				Cell cell = cellWrapper.cell;
//				
//				JPanel panel = new JPanel();
//				panel.setLayout(new BorderLayout(0, 0));
//				
//				JTextField textField = new JTextField(1);
//				textField.setHorizontalAlignment(SwingConstants.CENTER);
//				panel.add(textField, BorderLayout.CENTER);
//
//				textField.setColumns(1);				
//				textField.setFont(font);
//
//	        	textField.setBackground( Color.WHITE );
//	        	
//	        	if (cell.getValue() > 0) {
//		        		textField.setText(Integer.toString(cell.getValue()));
//	
//		        		if (cell.isReadOnly()) {
//			        		textField.setForeground( Color.BLUE );
//			        	}
//			        	else {
//			        		textField.setForeground( Color.BLACK );
//			        	}
//		        }
//		        else {
//	        		if ( cellWrapper.illegalValue > 0 ) {
//			            textField.setForeground( Color.RED );
//			            textField.setText(Integer.toString(cellWrapper.illegalValue));
//			        }
//		        }
//
//	    		JLabel label = new JLabel(formatMarkup(cell.getMarkUp()));
//	    		label.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
//	    		label.setBackground(Color.WHITE);
//	    		label.setHorizontalAlignment(0);
//	    		panel.add(label, BorderLayout.NORTH);
//
//				if (hasFocus) {
//					panel.setBackground(Color.green.darker());
//				}
//				else {
//					panel.setBackground(backgroundColor);
//				}
//
//				return panel;
//		    }
//			
//			private String formatMarkup(BitSet set) {
//				StringBuilder b = new StringBuilder();
//				for (int i = 1; i <= 9; i++) {
//					b.append( set.get(i) ? i : " ");
//				}
//				return b.toString();
//			}
//		};
//
//		table.setDefaultRenderer(CellWrapper.class, centerRenderer);
		
		final int height = 50;
		
		TableColumnModel cm = table.getColumnModel();
	    table.setRowHeight(height);
	    for (int c = 0; c < cm.getColumnCount(); c++) {
	    	TableColumn tc = cm.getColumn(c);
	    	tc.setPreferredWidth(height);
	    	tc.setMinWidth(height);
	    	tc.setMaxWidth(height);
	    }
	    
	    ListSelectionModel cellSelectionModel = table.getSelectionModel();
	    cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    
		frame.getContentPane().add(table, BorderLayout.CENTER);
		
		JPanel buttons = new JPanel();
		frame.getContentPane().add(buttons, BorderLayout.SOUTH);
		
		createButtons(buttons);
				
		frame.pack();
	}

	private void createButtons(JPanel buttons) {
		JButton createButton = new JButton("Create");
		createButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				illegalEntries.clear();
				createRandomPuzzle();
				tableModel.fireTableDataChanged();
			}
		});
		buttons.add(createButton);

		JButton solveButton = new JButton("Solve");
		solveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				solveBruteForce();
				tableModel.fireTableDataChanged();
			}
		});
		buttons.add(solveButton);
		
		JButton quitButton = new JButton("Quit");
		quitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				System.exit(0);
			}
		});
		buttons.add(quitButton);
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

@SuppressWarnings("serial")
class CellEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer
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
    	
		label = new JLabel();
		label.setFont(smallFont);
		label.setBackground(Color.WHITE);
		label.setHorizontalAlignment(0);
		panel.add(label, BorderLayout.NORTH);

		Cell cell = wrapper.cell;

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
			if ( wrapper.illegalValue > 0 ) {
		        textField.setForeground( Color.RED );
		        textField.setText(Integer.toString(wrapper.illegalValue));
		    }
		}
		
		if (hasFocus) {
			panel.setBackground(Color.green.darker());
		}
		else {
			panel.setBackground(backgroundColor);
		}
		
		label.setText( formatMarkup( cell.getMarkUp() ));

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
		// TODO Auto-generated method stub
		CellWrapper wrapper = (CellWrapper) value;
		
		JPanel panel = updateData(wrapper, true);
		
		return panel;
	}
	
}
@SuppressWarnings("serial")
class MyEditor extends DefaultCellEditor
{
	private Font font;
	public MyEditor(Font font) {
		super(new JTextField());
		this.font = font;
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		JTextField editor = (JTextField) super
				.getTableCellEditorComponent(table, value, isSelected, row,
						column);

		if (value != null)
			editor.setText(value.toString());
		editor.setHorizontalAlignment(SwingConstants.CENTER);
		editor.setFont(font);
		editor.setDocument(new FieldLimit(9));
		return editor;
	}
}