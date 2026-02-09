package sudoku.futoshiki;

import javax.swing.*;
import java.awt.*;

public class CrossPopup extends JPopupMenu {
    private String selectedSymbol = "None";

    public CrossPopup() {
        // Step 1: Set a 3x3 Grid Layout
        setLayout(new GridLayout(3, 3, 2, 2)); // rows, cols, hgap, vgap

        // Step 2: Define the buttons and symbols
        // Symbols: Greater (>), Less (<), and Center Circle (○)
        add(new JLabel(""));               // (0,0) Empty
        add(createButton("⌃", "North"));   // (0,1) North
        add(new JLabel(""));               // (0,2) Empty

        add(createButton("<", "West"));    // (1,0) West
        add(createButton("○", "Center"));  // (1,1) Center Circle
        add(createButton(">", "East"));    // (1,2) East

        add(new JLabel(""));               // (2,0) Empty
        add(createButton("⌄", "South"));   // (2,1) South
        add(new JLabel(""));               // (2,2) Empty
    }

    private JButton createButton(String symbol, String id) {
        JButton btn = new JButton(symbol);
        btn.setPreferredSize(new Dimension(50, 50));
        btn.setFocusable(false);
        btn.addActionListener(e -> {
            this.selectedSymbol = symbol;
            this.setVisible(false); // Close popup on click
            System.out.println("User chose: " + id + " (" + symbol + ")");
        });
        return btn;
    }

    public String getSelectedSymbol() {
        return selectedSymbol;
    }
}