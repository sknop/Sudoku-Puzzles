package sudoku.kakuro;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClueTest {

    @Test
    void compareToOnTotal() {
        Clue clue1 = new Clue(4, 2);
        Clue clue2 = new Clue(3,2);

        assertTrue(clue1.compareTo(clue2) > 0);
    }

    @Test
    void compareOnNumberOfCells() {
        Clue clue1 = new Clue(6, 3);
        Clue clue2 = new Clue(6,2);

        assertTrue(clue1.compareTo(clue2) > 0);
    }
}