package Ui;

import java.awt.*;
import javax.swing.*;

public class MainWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainWindow() {
        setTitle("PhysiQuist");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // ===== WelcomePanel =====
        WelcomePanel welcomePanel = new WelcomePanel(this);
        mainPanel.add(welcomePanel, "WelcomePanel");

        // ===== CategoryPanel =====
        CategoryPanel categoryPanel = new CategoryPanel(this);
        mainPanel.add(categoryPanel, "CategoryPanel");

        // Add mainPanel to JFrame
        add(mainPanel);

        // প্রথমে WelcomePanel দেখাবে
        cardLayout.show(mainPanel, "WelcomePanel");

        setVisible(true);
    }

    // ===== প্যানেল পরিবর্তনের মেথড =====
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    // ===== Formula List দেখানোর মেথড =====
    public void showFormulaList(String categoryName) {
        FormulaListPanel listPanel = new FormulaListPanel(this, categoryName);
        mainPanel.add(listPanel, "FormulaListPanel");
        cardLayout.show(mainPanel, "FormulaListPanel");
    }

    public void backToCategory() {
        cardLayout.show(mainPanel, "CategoryPanel");
    }

    // ===== InputOutputPanel দেখানোর মেথড =====
    public void showInputOutputPanel(String formulaName) {
        InputOutputPanel ioPanel = new InputOutputPanel(this, formulaName);
        mainPanel.add(ioPanel, "InputOutputPanel");
        cardLayout.show(mainPanel, "InputOutputPanel");
    }
}
