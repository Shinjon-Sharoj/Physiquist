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
            "Kepler’s Laws (Orbital Period, Radius relation)", "Distance-Time Relation",
            "Velocity-Time Relation", "Moment of Inertia"
        });

        formulas.put("Fluid Mechanics", new String[]{
            "Pressure", "Density", "Bernoulli’s Principle", "Elasticity", "Pressure in gases",
            "Fluid Pressure (Hydrostatic Pressure)", "Atmospheric Pressure", "Pascal’s Law",
            "Pressure due to Depth in Fluid", "Boyle’s Law", "Charles’s Law",
            "Gay-Lussac’s Law", "Avogadro’s Law", "Combined Gas Law", "Ideal Gas Law",
            "Dalton’s Law of Partial Pressure"
        });

        formulas.put("Thermodynamics", new String[]{
            "Temperature Conversion", "First Law of Thermodynamics",
            "Efficiency of Heat Engine", "Carnot Efficiency", "Entropy"
        });

        formulas.put("Waves and Oscillations", new String[]{
            "Wave Speed", "Period", "Hooke’s Law (Spring)",
            "Energy in Simple Harmonic Oscillator", "Resonance Frequency",
            "Doppler Effect", "Sound Intensity", "Decibel Formula"
        });

        formulas.put("Optics", new String[]{
            "Lens Formula", "Mirror Formula", "Magnification",
            "Snell’s Law", "Critical Angle", "Total Internal Reflection"
        });

        formulas.put("Electricity and Magnetism", new String[]{
            "Electric Potential", "Electric Field", "Coulomb’s Law", "Ohm’s Law",
            "Electric Power", "Series Resistance", "Parallel Resistance",
            "Magnetic Field", "Charge", "Inductance", "Conductivity", "Faraday’s Law"
        });

        formulas.put("Vector", new String[]{
            "Displacement Vector", "Velocity Vector", "Acceleration Vector",
            "Divergence", "Curl", "Gradient"
        });

        formulas.put("Modern Physics", new String[]{
            "Mass-Energy Equivalence", "Photon Energy", "de Broglie Wavelength",
            "Photoelectric Effect", "Nuclear Fission", "Nuclear Fusion",
            "Half-Life Formula", "Radioactive Decay Law", "Electromagnetic Wave"
        });

        // ========== UI ==========
        JLabel title = new JLabel(category + " Formulas", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
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
                btn.setFocusPainted(false);
                btn.addActionListener(e ->
                    JOptionPane.showMessageDialog(mainWindow, 
                        "You selected: " + f + "\n(Now Input/Output panel will open)",
                        "Formula Selected",
                        JOptionPane.INFORMATION_MESSAGE
                    )
                );
                btn.setMaximumSize(new Dimension(600, 40));
                btn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                listPanel.add(btn);
                listPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        add(scrollPane, BorderLayout.CENTER);

        JButton backBtn = new JButton("← Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backBtn.addActionListener(e -> mainWindow.backToCategory());
        add(backBtn, BorderLayout.SOUTH);
    }
}
