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

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TupleTest
{
    @Test
    public void testCompare() throws Exception {
        Point from1 = new Point(1,1);
        Point to1 = new Point(1,2);

        Point from2 = new Point(1,1);
        Point to2 = new Point(1,2);

        Tuple t1 = new Tuple(from1, to1);
        Tuple t2 = new Tuple(from2, to2);

        assertTrue(t1.equals(t2));
        assertTrue(t1.hashCode() == t2.hashCode());
        assertFalse(t1 == t2);
    }

    @Test
    public void testMap() throws Exception {
        Map<Tuple, Integer> map = new HashMap<>();

        for (int row = 1; row <= 3; row++) {
            for (int col = 1; col < 3; col++) {
                Point from = new Point(row, col);
                Point to = new Point(row, col+1);

                Tuple t = new Tuple(from, to);

                map.put(t, row + col);
            }
        }

        Point from2 = new Point(2,2);
        Point to2 = new Point(2,3);

        Tuple t2 = new Tuple(from2, to2);

        Integer result = map.get(t2);
        assertTrue(result == 4);
    }
}
