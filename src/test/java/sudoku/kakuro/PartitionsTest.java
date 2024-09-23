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

        List<Object> collector = new ArrayList<>();
        partitions.unpack(result, collector);

        for (Object o : collector) {
            System.out.println(o);
        }
    }

    @Test
    void allPartitions() {
        ArrayList<ArrayList<Object>> result = partitions.allPartitions();

        for (Object o : result) {
            System.out.println(o);
        }
    }
}