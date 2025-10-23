package Ui;

import java.awt.*;
import javax.swing.*;

public class MainWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainWindow() {
        setTitle("Physics Formula Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        CategoryPanel categoryPanel = new CategoryPanel(this);
        mainPanel.add(categoryPanel, "CategoryPanel");

        add(mainPanel);
        setVisible(true);
    }

    public void showFormulaList(String categoryName) {
        FormulaListPanel listPanel = new FormulaListPanel(this, categoryName);
        mainPanel.add(listPanel, "FormulaListPanel");
        cardLayout.show(mainPanel, "FormulaListPanel");
    }

    public void backToCategory() {
        cardLayout.show(mainPanel, "CategoryPanel");
    }
}
