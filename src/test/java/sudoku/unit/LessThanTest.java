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

package sudoku.unit;

import org.junit.jupiter.api.Test;
import sudoku.Cell;
import sudoku.MarkUp;
import sudoku.exceptions.CellContentException;

import static org.junit.jupiter.api.Assertions.*;

public class LessThanTest
{
    @Test
    public void testSimple() {
        Cell source = new Cell(2, 1, 1);
        Cell target = new Cell(2, 1, 2);
        Relation relation = new LessThan(source, target, 2);

        MarkUp possibles = relation.getNumbers();

        assertEquals(1, totalSet(possibles));
        assertTrue(possibles.get(2));
    }

    @Test
    public void testFive() {
        Cell source = new Cell(5,3,3);
        Cell target = new Cell(5,4,3);
        Relation relation = new LessThan(source, target, 5);

        MarkUp possibles = relation.getNumbers();

        assertEquals(1, totalSet(possibles));
        assertTrue(possibles.get(5));
    }

    @Test
    public void testThreeOfFive() throws Exception {
        Cell source = new Cell(5,3,3);
        Cell target = new Cell(5,4,3);
        Relation relation = new LessThan(source, target, 5);

        target.setValue(3);

        MarkUp possibles = relation.getNumbers();

        assertEquals(3, totalSet(possibles));
        assertTrue(possibles.get(3));
        assertTrue(possibles.get(4));
        assertTrue(possibles.get(5));
    }

    @Test
    public void testConstraintSuccess() throws Exception
    {
        Cell source = new Cell(5,3,3);
        Cell target = new Cell(5,4,3);
        Relation relation = new LessThan(source, target, 5);
        source.addConstraint(relation);

        target.setValue(3);
        source.setValue(2);
    }

    @Test
    public void testConstraintFailure() throws Exception
    {
        Cell source = new Cell(5,3,3);
        Cell target = new Cell(5,4,3);
        Relation relation = new LessThan(source, target, 5);
        source.addConstraint(relation);

        target.setValue(3);

        try {
            source.setValue(4);
            fail("Did not trigger constraint exception");
        }
        catch(CellContentException e) {
            // pass, expected behaviour
        }
    }

    private int totalSet(MarkUp set) {
        int result = 0;
        for (Integer i : set) {
            result++;
        }

        return result;
    }
}
