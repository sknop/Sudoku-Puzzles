package sudoku;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class MarkUpTest {
    static int width = 9;

    @Test
    public void testMarkUp() {
        MarkUp markup = new MarkUp(width);

        assertFalse(markup.get(1));

        markup.set(1);
        assertTrue(markup.get(1));

        markup.clear();
        assertFalse(markup.get(1));

        assertFalse(markup.get(5));
        markup.set(5);
        assertTrue(markup.get(5));
    }

    @Test
    public void testAllSet() {
        MarkUp markup = MarkUp.allSet(width);

        for (int i = 1; i <= width; ++i) {
            assertTrue(markup.get(i));
        }
    }


    @Test
    public void testOr() {
        MarkUp markup = new MarkUp(width);
        markup.set(1);
        markup.set(3);

        MarkUp other = new MarkUp(width);
        other.set(1);
        other.set(2);

        var result = markup.or(other);
        assertTrue(result.get(1));
        assertTrue(result.get(2));
        assertTrue(result.get(3));
        assertFalse(result.get(4));
    }

    @Test
    public void testAnd() {
        MarkUp markup = new MarkUp(width);
        markup.set(1);
        markup.set(3);

        MarkUp other = new MarkUp(width);
        other.set(1);
        other.set(2);

        var result = markup.and(other);
        assertTrue(result.get(1));
        assertFalse(result.get(2));
        assertFalse(result.get(3));
        assertFalse(result.get(4));
    }

    @Test
    public void testXor() {
        MarkUp markup = new MarkUp(width);
        markup.set(1);
        markup.set(3);

        MarkUp other = new MarkUp(width);
        other.set(1);
        other.set(2);

        var result = markup.xor(other);
        assertFalse(result.get(1));
        assertTrue(result.get(2));
        assertTrue(result.get(3));
        assertFalse(result.get(4));
    }

    @Test
    public void testComplement() {
        MarkUp markup = new MarkUp(width);

        markup.set(2);
        markup.set(3);
        markup.set(5);
        markup.set(9);

        MarkUp complement = markup.complement();
        assertFalse(complement.get(2));
        assertFalse(complement.get(3));
        assertFalse(complement.get(5));
        assertFalse(complement.get(9));

        assertTrue(complement.get(1));
        assertTrue(complement.get(4));
        assertTrue(complement.get(6));
        assertTrue(complement.get(7));
        assertTrue(complement.get(8));
    }

    @Test
    public void testToString() {
        MarkUp markup = new MarkUp(width);
        markup.set(1);
        markup.set(2);
        String s = markup.toString();

        assertEquals("000000011", s);
    }

    @Test
    public void testCardinality() {
        MarkUp markup = new MarkUp(width);
        markup.set(1);
        markup.set(2);
        markup.set(5);

        assertEquals(3, markup.cardinality());
    }

    @Test
    public void testUnset() {
        MarkUp markup = new MarkUp(width);

        markup.set(1);
        markup.set(9);
        markup.unset(1);

        assertEquals(1, markup.cardinality());
        assertFalse(markup.get(1));
        assertTrue(markup.get(9));
    }

    @Test
    public void testIterator() {
        MarkUp markup = new MarkUp(width);
        markup.set(1);
        markup.set(3);
        markup.set(9);

        Iterator<Integer> iterator = markup.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(1, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(3, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(9, iterator.next());
    }

    @Test
    public void testThrowsException() {
        MarkUp markup = new MarkUp(width);
        try {
            markup.set(10);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Should have thrown exception");
    }

    @Test
    public void testEqualsAndHash() {
        MarkUp markup = new MarkUp(width);
        markup.set(1);
        markup.set(3);

        MarkUp other = new MarkUp(width);
        other.set(1);
        other.set(3);

        assertEquals(markup, other);
        assertEquals(markup.hashCode(), other.hashCode());

        other.unset(3);
        assertNotEquals(markup, other);
        assertNotEquals(markup.hashCode(), other.hashCode());
    }

    @Test
    public void testCombineMarkUp() {
        MarkUp one = new MarkUp(width);
        one.set(1);
        one.set(3);

        MarkUp two = new MarkUp(width);
        two.set(1);
        two.set(4);
        two.set(7);

        MarkUp merged = new MarkUp(width);
        merged = merged.or(one);
        merged = merged.or(two);

        assertTrue(merged.get(1));
        assertTrue(merged.get(3));
        assertTrue(merged.get(4));

        var result = merged.complement();

        assertTrue(result.get(2));
        assertTrue(result.get(5));
        assertTrue(result.get(6));
        assertTrue(result.get(8));
        assertTrue(result.get(9));

        assertFalse(result.get(1));
        assertFalse(result.get(3));
        assertFalse(result.get(4));
        assertFalse(result.get(7));
    }

    @Test
    public void testClone() {
        MarkUp markup = new MarkUp(width);
        markup.set(1);
        markup.set(3);

        MarkUp clone = markup.clone();
        assertEquals(markup, clone);
    }
}
