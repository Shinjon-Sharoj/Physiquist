package Ui;

import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class WelcomePanel extends JPanel {

    private MainWindow mainWindow;
    private BufferedImage backgroundImage;

    public WelcomePanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        setLayout(null); // Absolute positioning

        // ===== Load background image correctly =====
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/Ui/bg.jpg"));
        } catch (Exception e) {
            System.out.println("Background image load failed: " + e.getMessage());
            backgroundImage = null;
        }

        // ===== "Let's Start" Button =====
        JButton startButton = new JButton("Let's Start");
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 28));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(new Color(0, 102, 204));
        startButton.setFocusPainted(false);
        startButton.setBounds(380, 470, 240, 70);
        startButton.addActionListener(e -> mainWindow.showPanel("CategoryPanel"));
        add(startButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // ===== Draw background =====
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, width, height, this);
        } else {
            g2.setColor(new Color(135, 206, 250)); // fallback sky blue
            g2.fillRect(0, 0, width, height);
        }

        // ===== Draw PHYSIQUIST title =====
        String title = "PHYSIQUIST";
        Font titleFont = new Font("Segoe UI Black", Font.BOLD, 120);
        g2.setFont(titleFont);

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(title);
        int textHeight = fm.getAscent();

        int x = (width - textWidth) / 2;
        int y = height / 2;

        // shadow
        g2.setColor(new Color(0, 0, 0, 120));
        g2.drawString(title, x + 6, y + 25);

        // glow effect
        for (int i = 1; i <= 2; i++) {
            g2.setColor(new Color(255, 255, 255, 18));
            g2.drawString(title, x - i, y - i);
            g2.drawString(title, x + i, y + i);
        }

        // main text
        g2.setColor(new Color(0, 0, 128)); // Navy Blue
        g2.drawString(title, x, y+28);
    }
}


