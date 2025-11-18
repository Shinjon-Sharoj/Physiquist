package Ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CategoryPanel extends JPanel {

    private static final String[] CATEGORIES = {
        "Mechanics",
        "Gravitation",
        "Fluid Mechanics",
        "Thermodynamics",
        "Waves and Oscillations",
        "Optics",
        "Electricity and Magnetism",
        "Vector",
        "Modern Physics"
    };

    private static final String[] CATEGORY_SYMBOLS = {
        "F", "g", "ρ", "Q", "λ", "n", "E", "→", "ℏ"
    };

    private static final Color[] CATEGORY_COLORS = {
        new Color(220, 50, 50),    // Mechanics - Red
        new Color(100, 180, 255),  // Gravitation - Sky Blue
        new Color(50, 150, 255),   // Fluid - Blue
        new Color(255, 100, 50),   // Thermo - Orange
        new Color(150, 50, 255),   // Waves - Purple
        new Color(255, 180, 50),   // Optics - Yellow
        new Color(50, 200, 50),    // E&M - Green
        new Color(100, 100, 255),  // Vector - Indigo
        new Color(180, 50, 200)    // Modern - Magenta
    };

    private static final Color BG = new Color(248, 252, 255);

    public CategoryPanel(MainWindow mainWindow) {
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(30, 40, 40, 40));

        // === Enhanced Title with Icon ===
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        titlePanel.setBackground(BG);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));

        // Create graduation cap icon
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw simple graduation cap
                g2.setColor(new Color(30, 80, 180));
                int[] xPoints = {5, 25, 45, 25};
                int[] yPoints = {25, 15, 25, 30};
                g2.fillPolygon(xPoints, yPoints, 4);
                g2.fillRect(23, 30, 4, 12);
                
                g2.dispose();
            }
        };
        iconLabel.setPreferredSize(new Dimension(50, 48));

        JLabel title = new JLabel("Choose Physics Topic");
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title.setForeground(new Color(30, 80, 180));

        titlePanel.add(iconLabel);
        titlePanel.add(title);
        add(titlePanel, BorderLayout.NORTH);

        // === Grid Panel with Enhanced Cards ===
        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 25, 25));
        gridPanel.setBackground(BG);
        gridPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        for (int i = 0; i < CATEGORIES.length; i++) {
            JButton btn = createEnhancedButton(CATEGORIES[i], CATEGORY_SYMBOLS[i], 
                                               CATEGORY_COLORS[i], mainWindow);
            gridPanel.add(btn);
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BG);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JButton createEnhancedButton(String text, String symbol, Color color, MainWindow mainWindow) {
        // Use array to store hover progress (workaround for inner class field access)
        final float[] hoverProgress = {0f};
        
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Shadow effect
                for (int i = 8; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 10 + (int)(hoverProgress[0] * 10)));
                    g2.fillRoundRect(i, i, w - 2*i, h - 2*i, 30, 30);
                }

                // Gradient background
                Color baseColor = getBackground();
                Color topColor = brighten(baseColor, 1.2f);
                Color bottomColor = baseColor;
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, topColor,
                    0, h, bottomColor
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, w, h, 25, 25);

                // Hover glow overlay
                if (hoverProgress[0] > 0) {
                    g2.setColor(new Color(255, 255, 255, (int)(50 * hoverProgress[0])));
                    g2.fillRoundRect(0, 0, w, h, 25, 25);
                }

                // Border
                g2.setColor(color.darker());
                g2.setStroke(new BasicStroke(3 + hoverProgress[0] * 2));
                g2.drawRoundRect(1, 1, w - 3, h - 3, 25, 25);

                g2.dispose();

                // Paint text and icon
                super.paintComponent(g);
            }

            private Color brighten(Color c, float factor) {
                int r = Math.min(255, (int)(c.getRed() * factor));
                int g = Math.min(255, (int)(c.getGreen() * factor));
                int b = Math.min(255, (int)(c.getBlue() * factor));
                return new Color(r, g, b);
            }
        };

        // Button layout with icon and text
        btn.setLayout(new BorderLayout(0, 10));
        
        // Create custom icon with symbol
        JLabel iconLabel = new JLabel(symbol, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Draw circle background
                int size = 60;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                g2.setColor(new Color(255, 255, 255, 200));
                g2.fillOval(x, y, size, size);
                
                g2.setColor(new Color(255, 255, 255, 100));
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(x, y, size, size);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        iconLabel.setForeground(new Color(255, 255, 255));
        iconLabel.setPreferredSize(new Dimension(80, 80));
        
        JLabel textLabel = new JLabel("<html><center>" + text.replace(" ", "<br>") + "</center></html>", 
                                      SwingConstants.CENTER);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        textLabel.setForeground(Color.WHITE);
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 8));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(15, 10, 15, 10));
        contentPanel.add(iconLabel, BorderLayout.NORTH);
        contentPanel.add(textLabel, BorderLayout.CENTER);
        
        btn.add(contentPanel);

        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Smooth hover animation
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            private javax.swing.Timer animTimer;
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (animTimer != null) animTimer.stop();
                animTimer = new javax.swing.Timer(20, evt -> {
                    hoverProgress[0] = Math.min(1f, hoverProgress[0] + 0.1f);
                    btn.repaint();
                    if (hoverProgress[0] >= 1f) {
                        animTimer.stop();
                    }
                });
                animTimer.start();
                btn.setBackground(brighten(color, 1.15f));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (animTimer != null) animTimer.stop();
                animTimer = new javax.swing.Timer(20, evt -> {
                    hoverProgress[0] = Math.max(0f, hoverProgress[0] - 0.1f);
                    btn.repaint();
                    if (hoverProgress[0] <= 0f) {
                        animTimer.stop();
                    }
                });
                animTimer.start();
                btn.setBackground(color);
            }

            private Color brighten(Color c, float factor) {
                int r = Math.min(255, (int)(c.getRed() * factor));
                int g = Math.min(255, (int)(c.getGreen() * factor));
                int b = Math.min(255, (int)(c.getBlue() * factor));
                return new Color(r, g, b);
            }
        });

        btn.addActionListener(e -> mainWindow.showFormulaList(text));

        return btn;
    }
}