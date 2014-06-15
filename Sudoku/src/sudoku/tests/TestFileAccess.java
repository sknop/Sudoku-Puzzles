package sudoku.tests;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalFileFormatException;
import sudoku.sudoku.Sudoku;

public class TestFileAccess
{
	Sudoku sudoku;
	Path path;
	
	@Before
	public void setUp() throws Exception {
		sudoku = new Sudoku();
		path = FileSystems.getDefault().getPath("sudoku.csv");
	}

	@Test
	public void testImportEmptyFile() 
			throws IOException, IllegalFileFormatException, CellContentException {
		OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.WRITE};
		BufferedWriter writer = Files.newBufferedWriter(path, options );
		
		for (int row = 0; row < 9; row++) {
			writer.append("0");
			for (int col = 1; col < 9; col++) {
				writer.append(",");
				writer.append("0");
			}
			writer.append("\n");
		}
		
		writer.close();
		
		sudoku.importFile(path);
		
		assertTrue("First value is not 0", sudoku.getValue(1, 1) == 0);
	}

	@Test
	public void testImportFullFile() 
			throws IOException, IllegalFileFormatException, CellContentException {
		int[][] values = {
				{ 1,2,3,4,5,6,7,8,9 },
				{ 4,5,6,7,8,9,1,2,3 },
				{ 7,8,9,1,2,3,4,5,6 },
				{ 2,3,4,5,6,7,8,9,1 },
				{ 5,6,7,8,9,1,2,3,4 },
				{ 8,9,1,2,3,4,5,6,7 },
				{ 3,4,5,6,7,8,9,1,2 },
				{ 6,7,8,9,1,2,3,4,5 },
				{ 9,1,2,3,4,5,6,7,8 }
		};
		
		OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.WRITE};
		BufferedWriter writer = Files.newBufferedWriter(path, options );
		
		for (int row = 0; row < 9; row++) {
			writer.append(Integer.toString(values[row][0]));
			for (int col = 1; col < 9; col++) {
				writer.append(",");
				writer.append(Integer.toString(values[row][col]));
			}
			writer.append("\n");
		}
		
		writer.close();
		
		sudoku.importFile(path);

		assertTrue("First value is not 1", sudoku.getValue(1, 1) == 1);
	}

	@Test
	public void testExportFile() 
			throws IOException, IllegalFileFormatException, CellContentException {
		int[][] values = {
				{ 1,2,3,4,5,6,7,8,9 },
				{ 4,5,6,7,8,9,1,2,3 },
				{ 7,8,9,1,2,3,4,5,6 },
				{ 2,3,4,5,6,7,8,9,1 },
				{ 5,6,7,8,9,1,2,3,4 },
				{ 8,9,1,2,3,4,5,6,7 },
				{ 3,4,5,6,7,8,9,1,2 },
				{ 6,7,8,9,1,2,3,4,5 },
				{ 9,1,2,3,4,5,6,7,8 }
		};
		
		sudoku.importArray(values);
		
		sudoku.exportFile(path);
		
		Sudoku newSudoku = new Sudoku();
		newSudoku.importFile(path);

		for (int row = 1; row <=9; row++) {
			for (int col = 1; col <= 9; col++) {
				assertTrue("Values do not match", sudoku.getValue(row, col) == newSudoku.getValue(row,col));
			}
		}
	}
	
	@After
	public void tearDown() throws Exception {
		Files.deleteIfExists(path);
	}
}
