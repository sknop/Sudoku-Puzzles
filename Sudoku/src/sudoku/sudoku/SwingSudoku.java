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

import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import java.awt.BorderLayout;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import sudoku.exceptions.CellContentException;
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
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		table = new JTable(new SudokuTableModel());

	    table.setCellSelectionEnabled(true);
	    table.setRowSelectionAllowed(false);
	    table.setColumnSelectionAllowed(false);

	    ListSelectionModel cellSelectionModel = table.getSelectionModel();
	    cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		frame.getContentPane().add(table, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.EAST);
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.SOUTH);
	}

}
