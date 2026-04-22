package sudoku.futoshiki;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class CrossPopup extends JPopupMenu {

    public CrossPopup(Consumer<String> callback) {
        setLayout(new GridLayout(3, 3, 2, 2));

        add(new JLabel(""));
        add(createButton("⌃", callback));
        add(new JLabel(""));

        add(createButton("<", callback));
        add(createButton("○", callback));
        add(createButton(">", callback));

        add(new JLabel(""));
        add(createButton("⌄", callback));
        add(new JLabel(""));
    }

    private JButton createButton(String symbol, Consumer<String> callback) {
        JButton btn = new JButton(symbol);
        btn.setPreferredSize(new Dimension(50, 50));
        btn.setFocusable(false);
        btn.addActionListener(e -> {
            callback.accept(symbol);
            this.setVisible(false);
        });
        return btn;
    }
}
