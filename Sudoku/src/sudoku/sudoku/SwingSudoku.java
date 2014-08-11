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

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import java.awt.BorderLayout;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import javax.swing.JPanel;
import javax.swing.border.MatteBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import sudoku.Point;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;
import sudoku.exceptions.IllegalFileFormatException;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class SwingSudoku extends Sudoku
{

	private JFrame frame;
	private JTable table;

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
		public Object getValueAt(int rowIndex, int columnIndex) {
			int value = getValue(rowIndex + 1, columnIndex + 1);
			if (value == 0) {
				return null;
			}
			else {
				return Integer.toString(value);
			}
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
	        Point p = new Point(rowIndex + 1, columnIndex + 1);
			boolean isReadOnly = isReadOnly(p);
			return !isReadOnly;
		}
		
		@Override
		public void setValueAt(Object value, int row, int col) {
	        Point p = new Point(row + 1, col + 1);
	        int intValue = Integer.parseInt((String) value);
	        try {
				setValue(p, intValue);
			} catch (IllegalCellPositionException e) {
				System.err.println("Should never happen " + e);
			} catch (CellContentException e) {
				e.printStackTrace();
			}
	        
	        fireTableCellUpdated(row, col);
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
		frame.setBounds(100, 100, 400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		table = new JTable(new SudokuTableModel()) {
			@Override
    		public Component prepareRenderer(
        			TableCellRenderer renderer, int row, int column)
        		{
        			Component c = super.prepareRenderer(renderer, row, column);
        			JComponent jc = (JComponent)c;

    				jc.setBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK) );


        			//  Use bold font on selected row

        			return c;
        		}
		};

		
	    table.setCellSelectionEnabled(true);
	    table.setRowSelectionAllowed(false);
	    table.setColumnSelectionAllowed(false);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );

		TableColumnModel cm = table.getColumnModel();
	    table.setRowHeight(40);
	    for (int c = 0; c < cm.getColumnCount(); c++) {
	    	TableColumn tc = cm.getColumn(c);
	    	tc.setPreferredWidth(20);
	    	tc.setCellRenderer(centerRenderer);
	    }
	    
	    ListSelectionModel cellSelectionModel = table.getSelectionModel();
	    cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		frame.getContentPane().add(table, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.EAST);
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.SOUTH);
	}

}
