package sudoku.kakuro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class Partitions {
    private int maxValue;

    private final TreeMap<Clue, ArrayList<ArrayList<Integer>>> partitions = new TreeMap<>();

    public Partitions(int maxValue) {
        this.maxValue = maxValue;

        // var allPartitions = allElements();

    }

    void allElements() {
        ArrayList<ArrayList<Object>> rawPartitions = allPartitions();
        for (ArrayList<Object> elements : rawPartitions) {
            for (var entry : elements) {

            }
        }
    }

    ArrayList<ArrayList<Object>> allPartitions() {
        ArrayList<ArrayList<Object>> result = new ArrayList<>();
        for (int elements=2; elements < maxValue+1; elements++) {
            List<Object> inner = partitions(1, elements, new ArrayList<>());
            ArrayList<Object> collector = new ArrayList<>();
            unpack(inner, collector);
            result.add(collector);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    void unpack(List<Object> parts, List<Object> collector) {
        if (!parts.isEmpty()) {
            if (parts.getFirst() instanceof List<?>) {
                for (Object inner : parts)
                    unpack((List<Object>) inner, collector);
            } else
                collector.add(parts);
        }
    }

    List<Object> partitions(int start, int elements, List<Integer> parts) {
        List<Object> results = new ArrayList<>();
        if (elements > 0) {
            for (int i = start; i < maxValue + 2 - elements; i++) {
                List<Integer> myParts = new ArrayList<>(parts);
                myParts.add(i);
                List<Object> subResult = partitions(i + 1, elements - 1, myParts);
                results.add(subResult);

            }
        }
        else {
            results = Collections.singletonList(parts);
        }
        return results;
    }
}
