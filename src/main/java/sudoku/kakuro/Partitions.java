package sudoku.kakuro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Partitions {
    private final int maxValue;

    final TreeMap<Clue, List<List<Integer>>> partitions = new TreeMap<>();

    public Partitions(int maxValue) {
        this.maxValue = maxValue;

        allElements();
    }

    void allElements() {
        List<List<List<Integer>>> rawPartitions = allPartitions();
        for (List<List<Integer>> elements : rawPartitions) {
            for (var entry : elements) {
                int total = entry.stream().mapToInt(Integer::intValue).sum();
                Clue key = new Clue(total, elements.size());
                if (partitions.containsKey(key)) {
                    partitions.get(key).add(entry);
                }
                else {
                    ArrayList<List<Integer>> value = new ArrayList<>();
                    value.add(entry);
                    partitions.put(key, value);
                }
            }
        }
    }

    List<List<List<Integer>>> allPartitions() {
        List<List<List<Integer>>> result = new ArrayList<>();
        for (int elements=2; elements < maxValue+1; elements++) {
            List<Object> inner = partitions(1, elements, new ArrayList<>());
            List<List<Integer>> collector = new ArrayList<>();
            unpack(inner, collector);
            result.add(collector);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    void unpack(List<Object> parts, List<List<Integer>> collector) {
        if (!parts.isEmpty()) {
            if (parts.getFirst() instanceof List<?>) {
                for (Object inner : parts)
                    unpack((List<Object>) inner, collector);
            } else
                collector.add(parts.stream().map(e -> (Integer) e).collect(Collectors.toList()));
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
