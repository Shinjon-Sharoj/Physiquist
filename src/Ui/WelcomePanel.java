/*package Ui;

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


*/
package Ui;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.File;

public class WelcomePanel extends JPanel {

    private final MainWindow mainWindow;
    private BufferedImage backgroundImage;
    private float animationOffset = 0f;
    private Timer animationTimer;

    public WelcomePanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(null);

        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("bg.jpg"));
        } catch (Exception e) {
            System.err.println("bg.jpg not found! Using fallback gradient.");
            backgroundImage = null;
        }

        // Animated subtitle
        JLabel subtitle = new JLabel("Master Physics with Interactive Formulas", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 24));
        subtitle.setForeground(new Color(255, 255, 255, 220));
        subtitle.setBounds(200, 310, 700, 40);
        add(subtitle);

        // Enhanced "Let's Start" Button with glow effect
        JButton startButton = new JButton("Let's Start") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Glow effect
                for (int i = 8; i > 0; i--) {
                    g2.setColor(new Color(255, 100, 100, 20));
                    g2.fillRoundRect(i, i, getWidth() - 2*i, getHeight() - 2*i, 50, 50);
                }
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, getBackground(),
                    0, getHeight(), getBackground().darker()
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 36));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(new Color(220, 20, 60));
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setOpaque(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.setBounds(325, 480, 450, 100);

        // Pulse animation on hover
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            private Timer pulseTimer;
            
            public void mouseEntered(java.awt.event.MouseEvent e) {
                startButton.setBackground(new Color(255, 50, 90));
                pulseTimer = new Timer(50, evt -> {
                    startButton.repaint();
                });
                pulseTimer.start();
            }
            
            public void mouseExited(java.awt.event.MouseEvent e) {
                startButton.setBackground(new Color(220, 20, 60));
                if (pulseTimer != null) {
                    pulseTimer.stop();
                    pulseTimer = null;
                }
            }
        });

        startButton.addActionListener(e -> mainWindow.showCategoryPanel());
        add(startButton);

        // Feature badges
        String[] features = {"9 Categories", "Quick Calculations", "Easy to Use"};
        String[] featureIcons = {"#", "⚡", "✓"};
        int badgeY = 380;
        int badgeX = 250;
        int badgeSpacing = 200;
        
        for (int i = 0; i < features.length; i++) {
            JLabel badge = createFeatureBadge(features[i], featureIcons[i]);
            badge.setBounds(badgeX, badgeY, 180, 50);
            add(badge);
            badgeX += badgeSpacing;
        }

        // Start background animation
        animationTimer = new Timer(50, e -> {
            animationOffset += 0.5f;
            if (animationOffset > 100) animationOffset = 0;
            repaint();
        });
        animationTimer.start();
    }

    private JLabel createFeatureBadge(String text, String icon) {
        JLabel badge = new JLabel(icon + " " + text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Semi-transparent background
                g2.setColor(new Color(255, 255, 255, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                // Border
                g2.setColor(new Color(100, 150, 255, 200));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        badge.setForeground(new Color(30, 60, 120));
        badge.setOpaque(false);
        return badge;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // Background with overlay
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, w, h, this);
            // Dark overlay for better text visibility
            g2.setColor(new Color(0, 0, 50, 100));
            g2.fillRect(0, 0, w, h);
        } else {
            // Animated gradient background
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(20, 60, 140),
                w, h, new Color(100, 150, 255)
            );
            g2.setPaint(gradient);
            g2.fillRect(0, 0, w, h);
        }

        // Animated floating particles
        g2.setColor(new Color(255, 255, 255, 60));
        for (int i = 0; i < 20; i++) {
            float x = (i * 50 + animationOffset * 2) % w;
            float y = (i * 30 + animationOffset) % h;
            g2.fillOval((int)x, (int)y, 4, 4);
        }

        // PHYSIQUIST text with enhanced shadow and glow
        String text = "PHYSIQUIST";
        g2.setFont(new Font("Segoe UI Black", Font.BOLD, 120));
        FontMetrics fm = g2.getFontMetrics();
        int x = (w - fm.stringWidth(text)) / 2;
        int y = 220;

        // Multiple shadow layers for depth
        for (int i = 12; i > 0; i -= 2) {
            int alpha = 20 + (i * 5);
            g2.setColor(new Color(0, 0, 0, Math.min(alpha, 255)));
            g2.drawString(text, x + i, y + i);
        }

        // Glow effect
        g2.setColor(new Color(100, 180, 255, 100));
        g2.drawString(text, x - 2, y - 2);
        g2.drawString(text, x + 2, y - 2);
        g2.drawString(text, x - 2, y + 2);
        g2.drawString(text, x + 2, y + 2);

        // Main text with gradient
        GradientPaint textGradient = new GradientPaint(
            x, y - 80, new Color(255, 255, 255),
            x, y, new Color(200, 220, 255)
        );
        g2.setPaint(textGradient);
        g2.drawString(text, x, y);

        g2.dispose();
    }
}