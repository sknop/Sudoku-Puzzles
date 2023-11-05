/*
 * Copyright (c) 2015 Sven Erik Knop.
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
 *  Contributors:
 *      2015 - Sven Erik Knop - initial API and implementation
 *
 */

package sudoku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class NumberConverterTest
{
    @Test
    public void testGetString() {
        String result = NumberConverter.getValueAsString(1);
        assertEquals("1", result, "Value 1 does not give int 1");

        result = NumberConverter.getValueAsString(16);
        assertEquals("G", result, "Value 1 does not give 'G'");

        result = NumberConverter.getValueAsString(0);
        assertEquals(" ", result, "Value 1 does not give ' '");

    }

    @Test
    public void testGetInteger() {
        int result = NumberConverter.getCharAsValue('1');
        assertEquals(1, result, "Value '1' does not give 1");

        result = NumberConverter.getCharAsValue('G');
        assertEquals(16, result, "Value 'G' does not give 16");

        result = NumberConverter.getCharAsValue('0');
        assertEquals(0, result, "Value '0' does not give 0");

        result = NumberConverter.getCharAsValue(' ');
        assertEquals(0, result, "Value ' ' does not give 0");
    }

}
