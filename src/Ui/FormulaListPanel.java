/*package Ui;

import java.awt.*;
import java.util.*;
import javax.swing.*;

public class FormulaListPanel extends JPanel {

    private final Map<String, String[]> formulas = new LinkedHashMap<>();

    public FormulaListPanel(MainWindow mainWindow, String category) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);


        formulas.put("Mechanics", new String[]{
            "Velocity", "Acceleration", "Force", "Work", "Power", 
            "Kinetic Energy", "Potential Energy", "Momentum" , "Time",
            "Mass","Impulse","Centripetal Force","Centripetal Acceleration",
            "Torque","Angular Momentum","Angular Velocity","Angular Acceleration",
            "Rotational Kinetic Energy","First Equation of Motion","Second Equation of Motion",
            "Third Equation of Motion", "Maximum Height","Time of Flight","Tension","Friction",
            "Viscosity","Collision"
        });

        formulas.put("Gravitation", new String[]{
            "Gravitational Force", "Acceleration due to Gravity",
            "Gravitational Potential Energy", 
            "Orbital Velocity", "Kepler's Laws", "Distance-Time Relation", 
            "Velocity-Time Relation", "Moment of Inertia"
        });

        formulas.put("Fluid Mechanics", new String[]{
            "Pressure", "Density","Bernoulli's Principle","Elasticity", "Pressure in Gases", 
            "Fluid Pressure (Hydrostatic Pressure)", "Atmospheric Pressure", "Pascal's Law", 
            "Pressure due to Depth in Fluid", "Boyle's Law", "Charles's Law", "Gay-Lussac's Law", 
            "Avogadro's Law", "Combined Gas Law", "Ideal Gas Law", "Dalton's Law of Partial Pressure"
        });

        formulas.put("Thermodynamics", new String[]{
            "Temperature Conversion","First Law of Thermodynamics", "Heat Engine Efficiency", 
            "Carnot Efficiency", "Entropy"
        });

        formulas.put("Waves and Oscillations", new String[]{
            "Wave Speed","Period", "Hooke's Law (Spring)", "Energy in Simple Harmonic Oscillator", 
            "Resonance Frequency", "Doppler Effect", "Sound Intensity", "Decibel Formula"
        });

        formulas.put("Optics", new String[]{
            "Lens Formula","Mirror Formula","Magnification", "Snell's Law", 
            "Critical Angle", "Total Internal Reflection"
        });

        formulas.put("Electricity and Magnetism", new String[]{
            "Ohm's Law", "Electric Power", "Coulomb's Law","Electric Potential","Electric Field",
            "Series Resistance", "Parallel Resistance", "Magnetic Field (Due to a Straight Wire)", 
            "Charge", "Inductance", "Conductivity", "Faraday's Law of Electromagnetic Induction"
        });

        formulas.put("Modern Physics", new String[]{
            "Mass-Energy Equivalence", "Photon Energy","de Broglie Wavelength", 
            "Photoelectric Effect (Einstein's Equation)", "Nuclear Fission", "Nuclear Fusion", 
            "Half-Life Formula", "Radioactive Decay Law", "Electromagnetic Wave"
        });

        formulas.put("Vector", new String[]{
            "Displacement Vector"
        });

        // ========== UI ==========
        JLabel title = new JLabel(category + " Formulas", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        title.setForeground(new Color(0, 0, 128));
        add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        String[] selected = formulas.get(category);
        if (selected != null) {
            for (String f : selected) {
                JButton btn = new JButton(f);
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                btn.setBackground(new Color(230, 240, 255));
                btn.setForeground(Color.BLACK);
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 200, 255), 1),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
                
                // Add hover effect
                btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        btn.setBackground(new Color(200, 220, 255));
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        btn.setBackground(new Color(230, 240, 255));
                    }
                });
                
                btn.addActionListener(e -> mainWindow.showInputOutputPanel(f));
                btn.setMaximumSize(new Dimension(600, 45));
                listPanel.add(btn);
                listPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        } else {
            JLabel noFormulasLabel = new JLabel("No formulas found for " + category, SwingConstants.CENTER);
            noFormulasLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noFormulasLabel.setForeground(Color.RED);
            listPanel.add(noFormulasLabel);
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Back button
        JButton backBtn = new JButton("← Back to Categories");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backBtn.setBackground(new Color(255, 230, 230));
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backBtn.addActionListener(e -> mainWindow.backToCategory());
        
        backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backBtn.setBackground(new Color(255, 200, 200));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backBtn.setBackground(new Color(255, 230, 230));
            }
        });
        
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public static String[] getAvailableCategories() {
        return new String[]{
            "Mechanics", "Gravitation", "Fluid Mechanics", "Thermodynamics",
            "Waves and Oscillations", "Optics", "Electricity and Magnetism",
            "Modern Physics", "Vector"
        };
    }

    public static boolean isValidCategory(String category) {
        String[] categories = getAvailableCategories();
        for (String cat : categories) {
            if (cat.equals(category)) {
                return true;
            }
        }
        return false;
    }
}*/


package Ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class FormulaListPanel extends JPanel {

    private static final Map<String, String[]> FORMULAS = new LinkedHashMap<>();

    static {
        FORMULAS.put("Mechanics", new String[]{
            "Velocity", "Acceleration", "Force", "Work", "Power", "Kinetic Energy",
            "Potential Energy", "Momentum", "Impulse", "Centripetal Force",
            "Torque", "Angular Momentum", "First Equation of Motion",
            "Second Equation of Motion", "Third Equation of Motion",
            "Maximum Height", "Time of Flight", "Friction", "Tension"
        });

        FORMULAS.put("Gravitation", new String[]{
            "Gravitational Force", "Acceleration due to Gravity",
            "Gravitational Potential Energy", "Orbital Velocity",
            "Escape Velocity", "Kepler's Third Law"
        });

        FORMULAS.put("Fluid Mechanics", new String[]{
            "Pressure", "Density","Bernoulli's Principle","Elasticity", "Pressure in Gases", 
            "Fluid Pressure (Hydrostatic Pressure)", "Atmospheric Pressure", "Pascal's Law", 
            "Pressure due to Depth in Fluid", "Boyle's Law", "Charles's Law", "Gay-Lussac's Law", 
            "Avogadro's Law", "Combined Gas Law", "Ideal Gas Law", "Dalton's Law of Partial Pressure"
        });

        FORMULAS.put("Thermodynamics", new String[]{
            "First Law of Thermodynamics", "Heat Transfer",
            "Carnot Efficiency", "Entropy Change", "Work Done by Gas"
        });

        FORMULAS.put("Waves and Oscillations", new String[]{
            "Wave Speed", "Simple Harmonic Motion", "Spring Constant",
            "Pendulum Period", "Doppler Effect", "Resonance"
        });

        FORMULAS.put("Optics", new String[]{
            "Lens Formula", "Mirror Formula", "Snell's Law",
            "Magnification", "Power of Lens", "Critical Angle"
        });

        FORMULAS.put("Electricity and Magnetism", new String[]{
            "Ohm's Law", "Coulomb's Law", "Electric Field", "Electric Potential",
            "Series & Parallel Resistance", "Capacitance", "Magnetic Force"
        });

        FORMULAS.put("Modern Physics", new String[]{
            "E = mc²", "Photoelectric Effect", "de Broglie Wavelength",
            "Half-Life", "Nuclear Binding Energy", "Relativistic Momentum"
        });

        FORMULAS.put("Vector", new String[]{
            "Vector Addition", "Dot Product", "Cross Product",
            "Unit Vector", "Scalar Triple Product"
        });
    }

    private static final Color BG = new Color(248, 252, 255);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color ACCENT = new Color(30, 100, 200);

    public FormulaListPanel(MainWindow mainWindow, String category) {
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(25, 35, 35, 35));

        // === Enhanced Title with Gradient Background ===
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(230, 240, 255),
                    getWidth(), getHeight(), new Color(200, 220, 255)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                g2.dispose();
            }
        };
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 20));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel(getCategoryIcon(category));
        
        JLabel title = new JLabel(category + " Formulas");
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(ACCENT);

        titlePanel.add(iconLabel);
        titlePanel.add(title);
        titlePanel.setBorder(new EmptyBorder(0, 0, 25, 0));
        add(titlePanel, BorderLayout.NORTH);

        // === Formula Cards Grid ===
        JPanel grid = new JPanel(new GridLayout(0, 2, 30, 25));
        grid.setBackground(BG);
        grid.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] list = FORMULAS.getOrDefault(category, new String[0]);
        for (int i = 0; i < list.length; i++) {
            grid.add(createEnhancedFormulaCard(list[i], i, mainWindow));
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setBackground(BG);
        add(scroll, BorderLayout.CENTER);

        // === Enhanced Back Button ===
        JButton backBtn = new JButton("← Back to Categories") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, getBackground(),
                    0, getHeight(), getBackground().darker()
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backBtn.setBackground(new Color(255, 102, 102));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setBorder(BorderFactory.createEmptyBorder(14, 30, 14, 30));

        backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                backBtn.setBackground(new Color(255, 70, 70));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                backBtn.setBackground(new Color(255, 102, 102));
            }
        });

        backBtn.addActionListener(e -> mainWindow.backToCategoryPanel());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBackground(BG);
        bottom.setBorder(new EmptyBorder(20, 0, 10, 0));
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel createEnhancedFormulaCard(String formula, int index, MainWindow mainWindow) {
        // Use array to store hover scale (workaround for inner class field access)
        final float[] hoverScale = {1.0f};
        
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                
                // Shadow layers
                for (int i = 5; i > 0; i--) {
                    int alpha = 15 + (int)((hoverScale[0] - 1.0f) * 100);
                    g2.setColor(new Color(0, 0, 0, Math.max(0, Math.min(alpha, 50))));
                    g2.fillRoundRect(i, i, w - 2*i, h - 2*i, 20, 20);
                }
                
                // Card background with subtle gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_BG,
                    0, h, new Color(245, 250, 255)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, w, h, 18, 18);
                
                // Hover glow effect
                if (hoverScale[0] > 1.0f) {
                    g2.setColor(new Color(100, 150, 255, 30));
                    g2.setStroke(new BasicStroke(4));
                    g2.drawRoundRect(2, 2, w - 5, h - 5, 18, 18);
                }
                
                // Border
                Color borderColor = hoverScale[0] > 1.0f ? ACCENT : new Color(200, 220, 255);
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(hoverScale[0] > 1.0f ? 3 : 2));
                g2.drawRoundRect(1, 1, w - 3, h - 3, 18, 18);
                
                g2.dispose();
            }
        };
        
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(22, 25, 22, 25));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Formula number badge
        JLabel numberBadge = new JLabel(String.valueOf(index + 1));
        numberBadge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        numberBadge.setForeground(new Color(150, 150, 150));
        numberBadge.setPreferredSize(new Dimension(30, 20));
        
        JLabel label = new JLabel("<html><div style='text-align:center;'>" + formula + "</div></html>", 
                                  SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(new Color(30, 30, 50));

        JPanel contentPanel = new JPanel(new BorderLayout(5, 0));
        contentPanel.setOpaque(false);
        contentPanel.add(numberBadge, BorderLayout.WEST);
        contentPanel.add(label, BorderLayout.CENTER);
        
        card.add(contentPanel, BorderLayout.CENTER);

        // Smooth scale animation on hover
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            private javax.swing.Timer scaleTimer;
            
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (scaleTimer != null) scaleTimer.stop();
                scaleTimer = new javax.swing.Timer(20, evt -> {
                    hoverScale[0] = Math.min(1.05f, hoverScale[0] + 0.01f);
                    card.repaint();
                    if (hoverScale[0] >= 1.05f) {
                        scaleTimer.stop();
                    }
                });
                scaleTimer.start();
            }
            
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (scaleTimer != null) scaleTimer.stop();
                scaleTimer = new javax.swing.Timer(20, evt -> {
                    hoverScale[0] = Math.max(1.0f, hoverScale[0] - 0.01f);
                    card.repaint();
                    if (hoverScale[0] <= 1.0f) {
                        scaleTimer.stop();
                    }
                });
                scaleTimer.start();
            }
            
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mainWindow.showInputOutputPanel(formula);
            }
        });

        return card;
    }

    private Icon getCategoryIcon(String category) {
        String symbol = switch (category) {
            case "Mechanics" -> "F";
            case "Gravitation" -> "g";
            case "Fluid Mechanics" -> "ρ";
            case "Thermodynamics" -> "Q";
            case "Waves and Oscillations" -> "λ";
            case "Optics" -> "n";
            case "Electricity and Magnetism" -> "E";
            case "Vector" -> "→";
            case "Modern Physics" -> "ℏ";
            default -> "?";
        };
        
        Color bg = switch (category) {
            case "Mechanics" -> new Color(220, 50, 50);
            case "Gravitation" -> new Color(100, 180, 255);
            case "Fluid Mechanics" -> new Color(50, 150, 255);
            case "Thermodynamics" -> new Color(255, 100, 50);
            case "Waves and Oscillations" -> new Color(150, 50, 255);
            case "Optics" -> new Color(255, 180, 50);
            case "Electricity and Magnetism" -> new Color(50, 200, 50);
            case "Vector" -> new Color(100, 100, 255);
            case "Modern Physics" -> new Color(180, 50, 200);
            default -> Color.GRAY;
        };
        
        return createIcon(symbol, bg);
    }

    private ImageIcon createIcon(String text, Color bg) {
        BufferedImage img = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw circle background
        g.setColor(bg);
        g.fillOval(2, 2, 46, 46);
        
        // Draw border
        g.setColor(bg.darker());
        g.setStroke(new BasicStroke(2));
        g.drawOval(2, 2, 46, 46);
        
        // Draw text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        int x = (50 - fm.stringWidth(text)) / 2;
        int y = (50 - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(text, x, y);
        
        g.dispose();
        return new ImageIcon(img);
    }
}



