package sudoku.kakuro;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PartitionsTest {
    Partitions partitions = new Partitions(9);

    @Test
    void singlePartitions() {
        List<Object> result = partitions.partitions(1,3, new ArrayList<>());

        for (Object o : result) {
            System.out.println(o);
        }
    }

    @Test
    void unpackSinglePartition() {
        List<Object> result = partitions.partitions(1,3, new ArrayList<>());

        List<List<Integer>> collector = new ArrayList<>();
        partitions.unpack(result, collector);

        for (Object o : collector) {
            System.out.println(o);
        }
    }

    @Test
    void allPartitions() {
        List<List<List<Integer>>> result = partitions.allPartitions();

        for (Object o : result) {
            System.out.println(o);
        }
    }

    @Test
    void allElements() {
        var p = partitions.getPartitions();
        for (Clue key : p.keySet()) {
            var entry = p.get(key);

            System.out.println(key + " : " + entry);
        }

        assertEquals(1, p.get(new Clue(3,2)).size());
        assertEquals(2, p.get(new Clue(5,2)).size());
        assertEquals(1, p.get(new Clue(17,2)).size());
        assertEquals(1, p.get(new Clue(45,9)).size());
    }
}