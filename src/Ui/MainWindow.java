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



/*package Ui;

import java.awt.*;
import javax.swing.*;

public class MainWindow extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    private final WelcomePanel welcomePanel;
    private final CategoryPanel categoryPanel;
    private FormulaListPanel currentFormulaListPanel;
    private InputOutputPanel currentInputOutputPanel;

    public MainWindow() {
        setTitle("PHYSIQUIST - Physics Made Easy");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        mainPanel.setBackground(new Color(248, 252, 255));

        welcomePanel = new WelcomePanel(this);
        categoryPanel = new CategoryPanel(this);

        mainPanel.add(welcomePanel, "Welcome");
        mainPanel.add(categoryPanel, "Category");

        add(mainPanel);
        cardLayout.show(mainPanel, "Welcome");
        setVisible(true);
    }

    public void showCategoryPanel() { cleanup(); cardLayout.show(mainPanel, "Category"); }
    public void showFormulaList(String category) {
        cleanup();
        currentFormulaListPanel = new FormulaListPanel(this, category);
        mainPanel.add(currentFormulaListPanel, "FormulaList");
        cardLayout.show(mainPanel, "FormulaList");
    }
    public void showInputOutputPanel(String formula) {
        cleanup();
        currentInputOutputPanel = new InputOutputPanel(this, formula);
        mainPanel.add(currentInputOutputPanel, "InputOutput");
        cardLayout.show(mainPanel, "InputOutput");
    }
    public void backToFormulaList() {
        if (currentInputOutputPanel != null) {
            mainPanel.remove(currentInputOutputPanel);
            currentInputOutputPanel = null;
        }
        if (currentFormulaListPanel != null) cardLayout.show(mainPanel, "FormulaList");
        else showCategoryPanel();
    }
    public void backToCategoryPanel() { cleanup(); cardLayout.show(mainPanel, "Category"); }

    private void cleanup() {
        if (currentFormulaListPanel != null) { mainPanel.remove(currentFormulaListPanel); currentFormulaListPanel = null; }
        if (currentInputOutputPanel != null) { mainPanel.remove(currentInputOutputPanel); currentInputOutputPanel = null; }
    }
}*/