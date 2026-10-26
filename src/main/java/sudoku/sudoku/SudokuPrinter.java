package sudoku.sudoku;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import java.awt.print.*;

import sudoku.MarkUp;
import sudoku.Point;
import sudoku.swing.Options;

public final class SudokuPrinter implements Printable {

    private final Sudoku puzzle;
    private final Options options;

    public SudokuPrinter(Sudoku puzzle, Options options) {
        this.puzzle = puzzle;
        this.options = options;
    }

    @Override
    public int print(Graphics graphics,
                     PageFormat pageFormat,
                     int pageIndex) {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g = (Graphics2D) graphics.create();

        double pageX = pageFormat.getImageableX();
        double pageY = pageFormat.getImageableY();
        double pageWidth = pageFormat.getImageableWidth();
        double pageHeight = pageFormat.getImageableHeight();

        /*
         * Leave a little space between neighbouring puzzles.
         * These values are in printer points: 72 points = 1 inch.
         */
        double horizontalGap = 12.0;
        double verticalGap = 12.0;

        double slotWidth = (pageWidth - 2 * horizontalGap) / 2.0;
        double slotHeight = (pageHeight - 2 * verticalGap) / 2.0;

        /*
         * The board remains square. It is centred inside its
         * 3-by-3 slot.
         */
        double boardSize = Math.min(slotWidth, slotHeight) * 0.90;

        double left = pageX + (slotWidth - boardSize) / 2.0;
        double top = pageY + (slotHeight - boardSize) / 2.0;
        double cell = boardSize / 9.0;

        g.setColor(Color.BLACK);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g, left, top, boardSize, cell);
        drawNumbers(g, left, top, cell);

        g.dispose();
        return PAGE_EXISTS;
    }

    private void drawGrid(Graphics2D g,
                          double left,
                          double top,
                          double boardSize,
                          double cell) {
        for (int i = 0; i <= 9; i++) {
            float lineWidth = (i % 3 == 0) ? 2.0f : 0.6f;
            g.setStroke(new BasicStroke(lineWidth));

            double position = i * cell;

            g.draw(new Line2D.Double(
                    left + position,
                    top,
                    left + position,
                    top + boardSize));

            g.draw(new Line2D.Double(
                    left,
                    top + position,
                    left + boardSize,
                    top + position));
        }
    }

    private void drawNumbers(Graphics2D g,
                             double left,
                             double top,
                             double cell) {
        int fontSize = Math.max(6, (int) (cell * 0.55));

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                // need to set the font each time since the hints might change it
                g.setFont(new Font("SansSerif", Font.BOLD, fontSize));
                FontMetrics metrics = g.getFontMetrics();

                Point point = new Point(row + 1, col + 1);
                int value = puzzle.getValue(point);

                double cellLeft = left + col * cell;
                double cellTop = top + row * cell;

                if (value == 0) {
                    if (options.getHintLevel() > 0) {
                        MarkUp hints = puzzle.getHints(point, options.getHintLevel());
                        drawHints(g, hints, cellLeft, cellTop, cell);
                    }
                    continue;
                }

                String text = Integer.toString(value);

                double textX = cellLeft
                        + (cell - metrics.stringWidth(text)) / 2.0;

                double textY = cellTop
                        + (cell - metrics.getHeight()) / 2.0
                        + metrics.getAscent();

                g.drawString(text, (float) textX, (float) textY);
            }
        }
    }

    private void drawHints(Graphics2D g,
                                  MarkUp hints,
                                  double cellLeft,
                                  double cellTop,
                                  double cellWidth) {
        Font hintFont = new Font("SansSerif", Font.PLAIN, 5);
        g.setFont(hintFont);

        FontMetrics metrics = g.getFontMetrics();

        double leftMargin = cellWidth * 0.06;
        double usableWidth = cellWidth - 2 * leftMargin;
        double slotWidth = usableWidth / 9.0;

        double baseline = cellTop + metrics.getAscent() + 1.0;

        for (int value = 1; value <= 9; value++) {
            if (hints.get(value)) {
                String text = Integer.toString(value);
                double textWidth = metrics.stringWidth(text);

                double slotLeft = cellLeft + leftMargin
                        + (value - 1) * slotWidth;

                double textX = slotLeft
                        + (slotWidth - textWidth) / 2.0;

                g.drawString(text, (float) textX, (float) baseline);
            }
        }
    }

}
