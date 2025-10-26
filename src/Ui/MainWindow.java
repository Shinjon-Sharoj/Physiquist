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
        setSize(1000, 700); // লেআউটের জন্য বড় সাইজ
        setLocationRelativeTo(null); // স্ক্রিনের মাঝে显示
        setIconImage(createAppIcon()); // অ্যাপ আইকন

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Color.WHITE);

        // প্যানেলগুলো ইনিশিয়ালাইজ করুন
        initializePanels();

        add(mainPanel);
        cardLayout.show(mainPanel, "WelcomePanel");
        setVisible(true);
    }

    private void initializePanels() {
        // ওয়েলকাম প্যানেল
        welcomePanel = new WelcomePanel(this);
        mainPanel.add(welcomePanel, "WelcomePanel");

        // ক্যাটাগরি প্যানেল
        categoryPanel = new CategoryPanel(this);
        mainPanel.add(categoryPanel, "CategoryPanel");
    }

    private Image createAppIcon() {
        // একটি সিম্পল ফিজিক্স-থিমড আইকন তৈরি করুন
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        
        // একটি সিম্পল এটম আইকন আঁকুন
        g2d.setColor(new Color(70, 130, 180)); // স্টীল ব্লু
        g2d.fillOval(8, 8, 16, 16);
        
        g2d.setColor(Color.WHITE);
        g2d.fillOval(12, 12, 8, 8);
        
        g2d.setColor(new Color(255, 215, 0)); // গোল্ড
        g2d.drawOval(4, 4, 24, 24);
        
        g2d.dispose();
        return icon;
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    public void showFormulaList(String category) {
        // পূর্বের ফর্মুলা লিস্ট প্যানেল থাকলে রিমুভ করুন
        if (currentFormulaListPanel != null) {
            mainPanel.remove(currentFormulaListPanel);
        }
        
        currentFormulaListPanel = new FormulaListPanel(this, category);
        mainPanel.add(currentFormulaListPanel, "FormulaListPanel");
        cardLayout.show(mainPanel, "FormulaListPanel");
    }

    public void showInputOutputPanel(String formulaName) {
        // পূর্বের ইনপুট আউটপুট প্যানেল থাকলে রিমুভ করুন
        if (currentInputOutputPanel != null) {
            mainPanel.remove(currentInputOutputPanel);
        }
        
        currentInputOutputPanel = new InputOutputPanel(this, formulaName);
        mainPanel.add(currentInputOutputPanel, "InputOutputPanel");
        cardLayout.show(mainPanel, "InputOutputPanel");
    }

    public void backToCategory() {
        // টেম্পোরারি প্যানেল ক্লিন আপ করুন
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
        // কারেন্ট ইনপুট আউটপুট প্যানেল রিমুভ করুন কিন্তু ফর্মুলা লিস্ট রাখুন
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

    // কম্পোনেন্ট অ্যাডিশনের জন্য মেইন প্যানেল পাওয়ার মেথড
    public JPanel getMainPanel() {
        return mainPanel;
    }

    // ম্যানুয়াল কন্ট্রোলের জন্য কার্ড লেআউট পাওয়ার মেথড
    public CardLayout getCardLayout() {
        return cardLayout;
    }

    // প্রপার মেমোরি ম্যানেজমেন্টের জন্য ক্লিন আপ মেথড
    public void cleanup() {
        if (welcomePanel != null) {
            // ওয়েলকাম প্যানেলের জন্য প্রয়োজনীয় ক্লিন আপ
        }
        if (categoryPanel != null) {
            // ক্যাটাগরি প্যানেলের জন্য প্রয়োজনীয় ক্লিন আপ
        }
        if (currentFormulaListPanel != null) {
            mainPanel.remove(currentFormulaListPanel);
            currentFormulaListPanel = null;
        }
        if (currentInputOutputPanel != null) {
            mainPanel.remove(currentInputOutputPanel);
            currentInputOutputPanel = null;
        }
    }

    // অ্যাপ্লিকেশন লঞ্চ করার মেইন মেথড
    public static void main(String[] args) {
        // ভালো এপিয়ারেন্সের জন্য সিস্টেম লুক অ্যান্ড ফিল সেট করুন
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // বেটার গ্রাফিক্সের জন্য এন্টি-অ্যালায়েসিং এনেবল করুন
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // ইভেন্ট ডিসপ্যাচ থ্রেডে GUI তৈরি এবং দেখান
        SwingUtilities.invokeLater(() -> {
            new MainWindow();
        });
    }
}