/*******************************************************************************
 * Copyright (c) 2014 Sven Erik Knop.
 * Licensed under the EUPL V.1.1
 * <p>
 * This Software is provided to You under the terms of the European 
 * Union Public License (the "EUPL") version 1.1 as published by the 
 * European Union. Any use of this Software, other than as authorized 
 * under this License is strictly prohibited (to the extent such use 
 * is covered by a right of the copyright holder of this Software).
 * <p>
 * This Software is provided under the License on an "AS IS" basis and 
 * without warranties of any kind concerning the Software, including 
 * without limitation merchantability, fitness for a particular purpose, 
 * absence of defects or errors, accuracy, and non-infringement of 
 * intellectual property rights other than copyright. This disclaimer 
 * of warranty is an essential part of the License and a condition for 
 * the grant of any rights to this Software.
 * <p>
 * For more details, see http://joinup.ec.europa.eu/software/page/eupl.
 * <p>
 * Contributors:
 *     2014 - Sven Erik Knop - initial API and implementation
 *******************************************************************************/
package sudoku.samurai;

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
import sudoku.exceptions.IllegalCellPositionException;
import sudoku.exceptions.IllegalFileFormatException;

public class SamuraiTest
{
	Samurai samurai;
	Path path;

	@BeforeEach
	public void setUp() {
		samurai = new Samurai();
		path = FileSystems.getDefault().getPath("test.samurai");
	}

	@Test
	public void testSetValue() {
		assertEquals(0, samurai.getValue(1, 1),"Cell (1,1) not empty");
		try {
			samurai.setValue(1, 1, 1);
		} catch (CellContentException | IllegalCellPositionException e) {
			fail("Should not happen " + e);
		}
		
		assertEquals(1, samurai.getValue(1, 1), "Cell (1,1) is not 1");
		
		assertFalse(samurai.isSolved(),"Samurai claims to be finished");
	}

	@Test
	public void testImportEmptyFile() 
			throws IOException, IllegalFileFormatException, CellContentException {
		OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.WRITE};

		try(BufferedWriter writer = Files.newBufferedWriter(path, options )) {
		
			for (int row = 0; row < 21; row++) {
				writer.append("0");
				for (int col = 1; col < 21; col++) {
					writer.append(",");
					writer.append("0");
				}
				writer.append("\n");
			}
		}
		
		samurai.importFile(path);
		
		assertEquals(0, samurai.getValue(1, 1),"First value is not 0");
	}

	@Test
	public void testImportFullFile() 
			throws IOException, IllegalFileFormatException, CellContentException {
		int[][] values = { 
				{ 5,8,0,0,0,0,0,1,7,0,0,0,3,1,0,0,0,0,0,9,2 },
				{ 9,0,2,0,0,0,8,0,5,0,0,0,6,0,2,0,0,0,4,0,5 },
				{ 0,4,0,5,0,8,0,9,0,0,0,0,0,5,0,1,0,2,0,7,0 },
				{ 0,0,9,1,0,2,5,0,0,0,0,0,0,0,1,8,0,4,7,0,0 },
				{ 0,0,0,0,7,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0 },
				{ 0,0,5,9,0,3,2,0,0,0,0,0,0,0,6,7,0,5,2,0,0 },
				{ 0,9,0,6,0,1,0,5,0,0,0,0,0,9,0,4,0,6,0,5,0 },
				{ 6,0,4,0,0,0,1,0,3,0,0,0,2,0,5,0,0,0,9,0,8 },
				{ 7,1,0,0,0,0,0,2,6,0,8,0,1,4,0,0,0,0,0,2,3 },
				{ 0,0,0,0,0,0,0,0,0,9,0,8,0,0,0,0,0,0,0,0,0 },
				{ 0,0,0,0,0,0,0,0,5,0,0,0,9,0,0,0,0,0,0,0,0 },
				{ 0,0,0,0,0,0,0,0,0,4,0,2,0,0,0,0,0,0,0,0,0 },
				{ 3,4,0,0,0,0,0,6,9,0,7,0,5,1,0,0,0,0,0,3,8 },
				{ 8,0,2,0,0,0,5,0,1,0,0,0,7,0,2,0,0,0,9,0,4 },
				{ 0,9,0,8,0,5,0,3,0,0,0,0,0,8,0,3,0,5,0,1,0 },
				{ 0,0,3,7,0,8,9,0,0,0,0,0,0,0,6,1,0,2,4,0,0 },
				{ 0,0,0,0,5,0,0,0,0,0,0,0,0,0,0,0,6,0,0,0,0 },
				{ 0,0,7,6,0,3,4,0,0,0,0,0,0,0,8,4,0,9,3,0,0 },
				{ 0,2,0,3,0,1,0,8,0,0,0,0,0,2,0,7,0,4,0,9,0 },
				{ 7,0,4,0,0,0,1,0,6,0,0,0,9,0,5,0,0,0,7,0,3 },
				{ 1,8,0,0,0,0,0,5,2,0,0,0,1,6,0,0,0,0,0,4,2 }
			};
		
		OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.WRITE};
		try (BufferedWriter writer = Files.newBufferedWriter(path, options ) ) {
		
			for (int row = 0; row < 21; row++) {
				writer.append(Integer.toString(values[row][0]));
				for (int col = 1; col < 21; col++) {
					writer.append(",");
					writer.append(Integer.toString(values[row][col]));
				}
				writer.append("\n");
			}
		}
		
		samurai.importFile(path);

		assertEquals(5, samurai.getValue(1, 1), "First value is not 5");
		assertEquals(2, samurai.getValue(21, 21),"Last value is not 2");
	}

	@Test
	public void testFullSamurai() {
		int[][] values = { 
			{ 5,8,0,0,0,0,0,1,7,0,0,0,3,1,0,0,0,0,0,9,2 },
			{ 9,0,2,0,0,0,8,0,5,0,0,0,6,0,2,0,0,0,4,0,5 },
			{ 0,4,0,5,0,8,0,9,0,0,0,0,0,5,0,1,0,2,0,7,0 },
			{ 0,0,9,1,0,2,5,0,0,0,0,0,0,0,1,8,0,4,7,0,0 },
			{ 0,0,0,0,7,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0 },
			{ 0,0,5,9,0,3,2,0,0,0,0,0,0,0,6,7,0,5,2,0,0 },
			{ 0,9,0,6,0,1,0,5,0,0,0,0,0,9,0,4,0,6,0,5,0 },
			{ 6,0,4,0,0,0,1,0,3,0,0,0,2,0,5,0,0,0,9,0,8 },
			{ 7,1,0,0,0,0,0,2,6,0,8,0,1,4,0,0,0,0,0,2,3 },
			{ 0,0,0,0,0,0,0,0,0,9,0,8,0,0,0,0,0,0,0,0,0 },
			{ 0,0,0,0,0,0,0,0,5,0,0,0,9,0,0,0,0,0,0,0,0 },
			{ 0,0,0,0,0,0,0,0,0,4,0,2,0,0,0,0,0,0,0,0,0 },
			{ 3,4,0,0,0,0,0,6,9,0,7,0,5,1,0,0,0,0,0,3,8 },
			{ 8,0,2,0,0,0,5,0,1,0,0,0,7,0,2,0,0,0,9,0,4 },
			{ 0,9,0,8,0,5,0,3,0,0,0,0,0,8,0,3,0,5,0,1,0 },
			{ 0,0,3,7,0,8,9,0,0,0,0,0,0,0,6,1,0,2,4,0,0 },
			{ 0,0,0,0,5,0,0,0,0,0,0,0,0,0,0,0,6,0,0,0,0 },
			{ 0,0,7,6,0,3,4,0,0,0,0,0,0,0,8,4,0,9,3,0,0 },
			{ 0,2,0,3,0,1,0,8,0,0,0,0,0,2,0,7,0,4,0,9,0 },
			{ 7,0,4,0,0,0,1,0,6,0,0,0,9,0,5,0,0,0,7,0,3 },
			{ 1,8,0,0,0,0,0,5,2,0,0,0,1,6,0,0,0,0,0,4,2 }
		};
		
		try {
			samurai.importArray(values);
		} catch (CellContentException e) {
			fail("Should not happen " + e);
		}
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		Files.deleteIfExists(path);
	}
}
