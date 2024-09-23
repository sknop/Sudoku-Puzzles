package sudoku.kakuro;

public record Clue(Integer total, Integer numberOfCells) implements Comparable<Clue> {
    @Override
    public int compareTo(Clue clue) {
        return (total - clue.total) != 0 ? (total - clue.total) : numberOfCells - clue.numberOfCells;
    }
}
