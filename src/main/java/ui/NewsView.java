package ui;

import javax.swing.*;
import java.awt.*;

public class NewsView extends JFrame {
    public NewsView() {
        setTitle("News");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea stub = new JTextArea("""
                News Section
                ------------
                TODO: Implement your news feed here.
                """);
        stub.setEditable(false);
        stub.setMargin(new Insets(8, 8, 8, 8));

        add(new JScrollPane(stub), BorderLayout.CENTER);
    }
}
