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
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.border.MatteBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import sudoku.Cell;
import sudoku.CellWrapper;
import sudoku.Point;
import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalCellPositionException;
import sudoku.exceptions.IllegalFileFormatException;
import sudoku.swing.CellEditor;
import sudoku.swing.Options;
import sudoku.unit.Unit;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;


public class SwingSudoku extends Sudoku
{

	private JFrame frame;
	private JTable table;
	private JLabel solved;
	
	private Options options = new Options();
	
	private AbstractTableModel tableModel;

	private Map<Point, Integer> illegalEntries = new HashMap<>();
	private JFileChooser fileChooser = new JFileChooser();
	private File lastDirectory = new File(".");
	
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
			if (isSolved()) 
				return false;
			
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
	        		// Sudoku is 1 based, JTable is 0 based, so need to remove 1
	        		fireTableCellUpdated(point.getX() - 1, point.getY() - 1);
	        	}
	        }
	        
	        if (isSolved()) {
	        	solved.setText("Solved!");
	        }
	    }
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
			
			@Override
			public void changeSelection(final int row, final int column, boolean toggle, boolean extend)
            {
                super.changeSelection(row, column, toggle, extend);
                SudokuTableModel model = (SudokuTableModel) getModel();
                if (model.isCellEditable(row, column)) {
                	this.editCellAt(row, column);
                	this.transferFocus();
                }
            }
		};
		
	    table.setCellSelectionEnabled(true);
	    table.setRowSelectionAllowed(false);
	    table.setColumnSelectionAllowed(false);

	    table.setDefaultEditor(CellWrapper.class, new CellEditor(options));
	    table.setDefaultRenderer(CellWrapper.class, new CellEditor(options));
	    table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
	    		
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
		
		JPanel bottomPanel = new JPanel();
		frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(2, 3));
		bottomPanel.add(buttons);
		
		createButtons(buttons);

		JPanel reports = new JPanel();
		reports.setLayout(new GridLayout(2,2,1,1));
		bottomPanel.add(reports);

		JLabel optionsLabel = new JLabel("Hints :");
		reports.add(optionsLabel);
		
		JComboBox<String> hintOptions = new JComboBox<>();
		hintOptions.addItem("None");
		hintOptions.addItem("Markup");
		hintOptions.addItem("Hints 1");
		reports.add(hintOptions);
		
		hintOptions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				options.setHintLevel(hintOptions.getSelectedIndex());
				tableModel.fireTableDataChanged();
			}
		});
		
		JLabel solvedLabel = new JLabel("Status :");
		reports.add(solvedLabel);
				
		solved = new JLabel("Unsolved");
		reports.add(solved);

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
				// cheeky hack - remove selection so that the cell is not blocked 
				table.editCellAt(-1, -1);
				table.getSelectionModel().clearSelection();
				
				solveBruteForce();
				tableModel.fireTableDataChanged();
				solved.setText("Cheated");
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
		
		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fileChooser.setCurrentDirectory(lastDirectory);
				int returnValue = fileChooser.showOpenDialog(null);
				
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					Path path = FileSystems.getDefault().getPath(file.getPath());
					
					try {
						importFile(path);
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (IllegalFileFormatException e1) {
						e1.printStackTrace();
					} catch (CellContentException e1) {
						e1.printStackTrace();
					}
				}
			}
			
		});
		buttons.add(loadButton);
		
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
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
			}
			
		});
		buttons.add(saveButton);
		
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
}

