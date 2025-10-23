package Ui;

import java.awt.*;
import javax.swing.*;

public class CategoryPanel extends JPanel {

    private static final String[] CATEGORIES = {
        "Mechanics", "Gravitation", "Fluid Mechanics", "Thermodynamics",
        "Waves and Oscillations", "Optics", "Electricity and Magnetism",
        "Vector", "Modern Physics"
    };

    public CategoryPanel(MainWindow mainWindow) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Select a Category", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        gridPanel.setBackground(Color.WHITE);
        for (String category : CATEGORIES) {
            JButton btn = new JButton(category);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            btn.setFocusPainted(false);
            btn.setBackground(new Color(220, 235, 255));
            btn.addActionListener(e -> mainWindow.showFormulaList(category));
            gridPanel.add(btn);
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);
    }
}
