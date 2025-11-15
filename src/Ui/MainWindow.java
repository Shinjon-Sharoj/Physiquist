package Ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class MainWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private WelcomePanel welcomePanel;
    private CategoryPanel categoryPanel;
    private FormulaListPanel currentFormulaListPanel;
    private InputOutputPanel currentInputOutputPanel;

    public MainWindow() {
        setTitle("PHYSIQUIST");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setIconImage(createAppIcon());

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Color.WHITE);

        initializePanels();

        add(mainPanel);
        cardLayout.show(mainPanel, "WelcomePanel");
        setVisible(true);
    }






    private void initializePanels() {
        welcomePanel = new WelcomePanel(this);
        mainPanel.add(welcomePanel, "WelcomePanel");

        categoryPanel = new CategoryPanel(this);
        mainPanel.add(categoryPanel, "CategoryPanel");
    }

    private Image createAppIcon() {
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        
        g2d.setColor(new Color(70, 130, 180));
        g2d.fillOval(8, 8, 16, 16);
        
        g2d.setColor(Color.WHITE);
        g2d.fillOval(12, 12, 8, 8);
        
        g2d.setColor(new Color(255, 215, 0));
        g2d.drawOval(4, 4, 24, 24);
        
        g2d.dispose();
        return icon;
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    public void showFormulaList(String category) {
        if (currentFormulaListPanel != null) {
            mainPanel.remove(currentFormulaListPanel);
        }
        
        currentFormulaListPanel = new FormulaListPanel(this, category);
        mainPanel.add(currentFormulaListPanel, "FormulaListPanel");
        cardLayout.show(mainPanel, "FormulaListPanel");
    }

    public void showInputOutputPanel(String formulaName) {
        if (currentInputOutputPanel != null) {
            mainPanel.remove(currentInputOutputPanel);
        }
        
        currentInputOutputPanel = new InputOutputPanel(this, formulaName);
        mainPanel.add(currentInputOutputPanel, "InputOutputPanel");
        cardLayout.show(mainPanel, "InputOutputPanel");
    }

    public void backToCategory() {
        if (currentFormulaListPanel != null) {
            mainPanel.remove(currentFormulaListPanel);
            currentFormulaListPanel = null;
        }
        if (currentInputOutputPanel != null) {
            mainPanel.remove(currentInputOutputPanel);
            currentInputOutputPanel = null;
        }
        
        cardLayout.show(mainPanel, "CategoryPanel");
    }

    public void backToFormulaList() {
        if (currentInputOutputPanel != null) {
            mainPanel.remove(currentInputOutputPanel);
            currentInputOutputPanel = null;
        }
        
        if (currentFormulaListPanel != null) {
            cardLayout.show(mainPanel, "FormulaListPanel");
        } else {
            backToCategory();
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public void cleanup() {
        if (currentFormulaListPanel != null) {
            mainPanel.remove(currentFormulaListPanel);
            currentFormulaListPanel = null;
        }
        if (currentInputOutputPanel != null) {
            mainPanel.remove(currentInputOutputPanel);
            currentInputOutputPanel = null;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            new MainWindow();
        });
    }
}