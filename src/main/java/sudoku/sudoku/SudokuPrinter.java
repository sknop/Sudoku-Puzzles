package sudoku.sudoku;

import java.awt.*;
import java.awt.print.*;

public final class SudokuPrinter implements Printable {

    private final int[][] puzzle;

    public SudokuPrinter(int[][] puzzle) {
        if (puzzle.length != 9) {
            throw new IllegalArgumentException("Puzzle must have 9 rows");
        }
        for (int[] row : puzzle) {
            if (row.length != 9) {
                throw new IllegalArgumentException("Puzzle must have 9 columns");
            }
        }
        this.puzzle = puzzle;
    }

    @Override
    public int print(Graphics graphics,
                     PageFormat pageFormat,
                     int pageIndex) {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g = (Graphics2D) graphics.create();

        double x = pageFormat.getImageableX();
        double y = pageFormat.getImageableY();
        double width = pageFormat.getImageableWidth();
        double height = pageFormat.getImageableHeight();

        // Keep the board square and centre it on the printable page.
        double boardSize = Math.min(width, height) * 0.8;
        double left = x + (width - boardSize) / 2.0;
        double top = y + (height - boardSize) / 2.0;
        double cell = boardSize / 9.0;

        g.setColor(Color.BLACK);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Grid.
        for (int i = 0; i <= 9; i++) {
            float lineWidth = (i % 3 == 0) ? 2.5f : 0.8f;
            g.setStroke(new BasicStroke(lineWidth));

            double pos = i * cell;
            g.draw(new java.awt.geom.Line2D.Double(
                    left + pos, top,
                    left + pos, top + boardSize));
            g.draw(new java.awt.geom.Line2D.Double(
                    left, top + pos,
                    left + boardSize, top + pos));
        }

        // Numbers.
        g.setFont(new Font("SansSerif", Font.PLAIN,
                (int) (cell * 0.55)));

        FontMetrics metrics = g.getFontMetrics();

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = puzzle[row][col];

                if (value == 0) {
                    continue;
                }

                String text = Integer.toString(value);
                double cellLeft = left + col * cell;
                double cellTop = top + row * cell;

                double textX = cellLeft
                        + (cell - metrics.stringWidth(text)) / 2.0;
                double textY = cellTop
                        + (cell - metrics.getHeight()) / 2.0
                        + metrics.getAscent();

                g.drawString(text, (float) textX, (float) textY);
            }
        }

        g.dispose();
        return PAGE_EXISTS;
    }

    public void printWithDialog() throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Sudoku puzzle");
        job.setPrintable(this);

        if (job.printDialog()) {
            job.print();
        }
    }

    static void main() throws PrinterException {
        int[][] puzzle = {
                {5, 3, 0, 0, 7, 0, 0, 0, 0},
                {6, 0, 0, 1, 9, 5, 0, 0, 0},
                {0, 9, 8, 0, 0, 0, 0, 6, 0},
                {8, 0, 0, 0, 6, 0, 0, 0, 3},
                {4, 0, 0, 8, 0, 3, 0, 0, 1},
                {7, 0, 0, 0, 2, 0, 0, 0, 6},
                {0, 6, 0, 0, 0, 0, 2, 8, 0},
                {0, 0, 0, 4, 1, 9, 0, 0, 5},
                {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };

        new SudokuPrinter(puzzle).printWithDialog();
    }
}
