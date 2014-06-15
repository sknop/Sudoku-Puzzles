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

public class TestFileImport
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
	}

	@After
	public void tearDown() throws Exception {
		Files.deleteIfExists(path);
	}
}
