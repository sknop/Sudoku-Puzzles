package sudoku.sudoku;

import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTable;

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
			return 9 + 1; // extra column for column header
		}

		@Override
		public int getColumnCount() {
			return 9 + 1; // extra column for the row headers
		}

		@Override
		public String getColumnName(int columnIndex) {
			if (columnIndex == 0) {
				return null;
			}
			else {
				return Integer.toString(columnIndex);
			}
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				if (rowIndex == 0) {
					return null; // upper left corner should be empty
				}
				else {
					return Integer.toString(rowIndex);
				}
			}
			else if (rowIndex == 0) {
				return Integer.toString(columnIndex);
			}
			else {
				int value = getValue(rowIndex, columnIndex);
				if (value == 0) {
					return null;
				}
				else {
					return Integer.toString(value);
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
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		table = new JTable(new SudokuTableModel()) {
	            @Override
	            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
	                if (col == 0) {
	                    return this.getTableHeader().getDefaultRenderer()
	                        .getTableCellRendererComponent(this,
	                        this.getValueAt(row, col), false, false, row, col);
	                } else {
	                    return super.prepareRenderer(renderer, row, col);
	                }
	            };
		};
        final JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer(table));

		frame.getContentPane().add(table, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.EAST);
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.SOUTH);
	}

}

class HeaderRenderer implements TableCellRenderer {
TableCellRenderer renderer;
    public HeaderRenderer(JTable jTable1) {
        renderer = jTable1.getTableHeader().getDefaultRenderer();
    }
    @Override
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int col) {
        return renderer.getTableCellRendererComponent(
            table, value, isSelected, hasFocus, row, col);
    }
}