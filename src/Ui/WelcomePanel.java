package Ui;

import java.awt.*;
import java.awt.image.*;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;

public class WelcomePanel extends JPanel {

    private MainWindow mainWindow;
    private BufferedImage backgroundImage;

    public WelcomePanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        setLayout(null); // Absolute positioning

        // ===== Load background image safely =====
        try {
            URL imageUrl = new URL(
                "https://static.vecteezy.com/system/resources/thumbnails/029/722/402/small/physics-science-theory-law-and-mathematical-formul-image-vector.jpg"
            );
            backgroundImage = ImageIO.read(imageUrl);
        } catch (Exception e) {
            System.out.println("Background image load failed. Using fallback color.");
            backgroundImage = null; // fallback
        }

        // ===== "Let's Start" Button =====
        JButton startButton = new JButton("Let's Start");
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 28));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(Color.BLUE);
        startButton.setFocusPainted(false);
        startButton.setBounds(350, 450, 200, 70);
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
            // fallback color
            g2.setColor(new Color(135, 206, 250)); // light sky blue
            g2.fillRect(0, 0, width, height);
        }

        // ===== Draw PHYSIQUIST title =====
        String title = "PHYSIQUIST";
        Font titleFont = new Font("Segoe UI Black", Font.BOLD, 120);
        g2.setFont(titleFont);

        // Center position
        FontMetrics fm = g2.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int titleHeight = fm.getAscent();
        int x = (width - titleWidth) / 2;
        int y = height / 2;

        // Shadow
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(title, x + 5, y + 5);

        // Glow effect
        for(int i = 1; i <= 6; i++) {
            g2.setColor(new Color(255, 255, 255, 20));
            g2.drawString(title, x - i, y - i);
            g2.drawString(title, x + i, y + i);
        }

        // Main text in Navy Blue
        g2.setColor(new Color(0, 0, 128)); // Navy Blue
        g2.drawString(title, x, y);
    }
}
