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
package sudoku.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import sudoku.exceptions.CellContentException;
import sudoku.exceptions.IllegalFileFormatException;
import sudoku.sudoku.Sudoku;

public class FileAccessTest
{
	Sudoku sudoku;
	Path path;
	
	@BeforeEach
	public void setUp() throws Exception {
		sudoku = new Sudoku();
		path = FileSystems.getDefault().getPath("sudoku.csv");
	}

	@Test
	public void testImportEmptyFile() 
			throws IOException, IllegalFileFormatException, CellContentException {
		OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.WRITE};
		
		try (BufferedWriter writer = Files.newBufferedWriter(path, options ) ) {		
			for (int row = 0; row < 9; row++) {
				writer.append("0");
				for (int col = 1; col < 9; col++) {
					writer.append(",");
					writer.append("0");
				}
				writer.append("\n");
			}
		}		
		
		sudoku.importFile(path);
		
		assertEquals(sudoku.getValue(1, 1), 0, "First value is not 0");
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
		
		try (BufferedWriter writer = Files.newBufferedWriter(path, options )) {		
			for (int row = 0; row < 9; row++) {
				writer.append(Integer.toString(values[row][0]));
				for (int col = 1; col < 9; col++) {
					writer.append(",");
					writer.append(Integer.toString(values[row][col]));
				}
				writer.append("\n");
			}
		}		
		
		sudoku.importFile(path);

		assertEquals(sudoku.getValue(1, 1), 1,"First value is not 1");
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
				assertTrue(sudoku.getValue(row, col) == newSudoku.getValue(row,col),"Values do not match");
			}
		}
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		Files.deleteIfExists(path);
	}
}
