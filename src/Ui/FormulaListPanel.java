package Ui;

import java.awt.*;
import java.util.*;
import javax.swing.*;

public class FormulaListPanel extends JPanel {

    private final Map<String, String[]> formulas = new LinkedHashMap<>();

    public FormulaListPanel(MainWindow mainWindow, String category) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ========== ALL FORMULAS ==========

        formulas.put("Mechanics", new String[]{
            "Velocity", "Acceleration", "Time",
            "Force", "Mass", "Work", "Power", "Momentum", "Impulse", "Kinetic Energy",
            "Potential Energy", "Mechanical Energy", "Centripetal Force",
            "Centripetal Acceleration", "Torque", "Angular Momentum",
            "Angular Velocity", "Angular Acceleration", "Rotational Kinetic Energy",
            "First Equation of Motion", "Second Equation of Motion",
            "Third Equation of Motion", "Maximum Height", "Time of Flight",
            "Tension", "Friction", "Viscosity", "Collision"
        });

        formulas.put("Gravitation", new String[]{
            "Gravitational Force", "Acceleration due to Gravity",
            "Gravitational Potential Energy", "Orbital Velocity",
            "Kepler's Laws", "Distance-Time Relation",
            "Velocity-Time Relation", "Moment of Inertia"
        });

        formulas.put("Fluid Mechanics", new String[]{
            "Pressure", "Density", "Bernoulli's Principle", "Elasticity", "Pressure in gases",
            "Fluid Pressure", "Atmospheric Pressure", "Pascal's Law",
            "Pressure due to Depth", "Boyle's Law", "Charles's Law",
            "Gay-Lussac's Law", "Avogadro's Law", "Combined Gas Law", "Ideal Gas Law",
            "Dalton's Law"
        });

        formulas.put("Thermodynamics", new String[]{
            "Temperature Conversion", "First Law of Thermodynamics",
            "Efficiency of Heat Engine", "Carnot Efficiency", "Entropy"
        });

        formulas.put("Waves and Oscillations", new String[]{
            "Wave Speed", "Period", "Hooke's Law",
            "Energy in Simple Harmonic Oscillator", "Resonance Frequency",
            "Doppler Effect", "Sound Intensity", "Decibel Formula"
        });

        formulas.put("Optics", new String[]{
            "Lens Formula", "Mirror Formula", "Magnification",
            "Snell's Law", "Critical Angle", "Total Internal Reflection"
        });

        formulas.put("Electricity and Magnetism", new String[]{
            "Electric Potential", "Electric Field", "Coulomb's Law", "Ohm's Law",
            "Electric Power", "Series Resistance", "Parallel Resistance",
            "Magnetic Field", "Charge", "Inductance", "Conductivity", "Faraday's Law"
        });

        formulas.put("Modern Physics", new String[]{
            "Mass-Energy Equivalence", "Photon Energy", "de Broglie Wavelength",
            "Photoelectric Effect", "Half-Life Formula", "Radioactive Decay Law", 
            "Electromagnetic Wave"
        });

        formulas.put("Vector", new String[]{
            "Displacement Vector", "Velocity Vector", "Acceleration Vector"
        });

        // ========== UI ==========
        JLabel title = new JLabel(category + " Formulas", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        title.setForeground(new Color(0, 0, 128)); // Navy blue color
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
            // If category not found, show message
            JLabel noFormulasLabel = new JLabel("No formulas found for " + category, SwingConstants.CENTER);
            noFormulasLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noFormulasLabel.setForeground(Color.RED);
            listPanel.add(noFormulasLabel);
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling
        add(scrollPane, BorderLayout.CENTER);

        // Back button
        JButton backBtn = new JButton("← Back to Categories");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backBtn.setBackground(new Color(255, 230, 230));
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backBtn.addActionListener(e -> mainWindow.backToCategory());
        
        // Add hover effect to back button
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

    // Helper method to get all available categories
    public static String[] getAvailableCategories() {
        return new String[]{
            "Mechanics", "Gravitation", "Fluid Mechanics", "Thermodynamics",
            "Waves and Oscillations", "Optics", "Electricity and Magnetism",
            "Modern Physics", "Vector"
        };
    }

    // Helper method to check if a category exists
    public static boolean isValidCategory(String category) {
        String[] categories = getAvailableCategories();
        for (String cat : categories) {
            if (cat.equals(category)) {
                return true;
            }
        }
        return false;
    }
}