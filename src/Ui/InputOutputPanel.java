package Ui;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InputOutputPanel extends JPanel {

    private MainWindow mainWindow;
    private String formulaName;
    private JLabel formulaLabel;
    private JPanel inputPanel;
    private JPanel outputPanel;
    private JButton calculateButton;
    private JButton refreshButton;
    private JButton backButton;

    // Input fields storage
    private Map<String, JTextField> inputFields;
    private Map<String, JLabel> inputLabels;
    private Map<String, JComboBox<String>> unitDropdowns;
    private Map<String, Boolean> variableLockStatus;
    private String targetVariable;

    // Physical constants
    private final double G = 6.67430e-11; // Gravitational constant
    private final double k = 9e9; // Coulomb's constant
    private final double c = 3e8; // Speed of light
    private final double h = 6.62607015e-34; // Planck constant
    private final double g = 9.8;
    private final double epsilon = 8.854e-12;
    private final double K = 1.381e-23; // Boltzmann constant
    private final double RHO = 1.1e3; 

    public InputOutputPanel(MainWindow mainWindow, String formulaName) {
        this.mainWindow = mainWindow;
        this.formulaName = formulaName;
        this.inputFields = new HashMap<>();
        this.inputLabels = new HashMap<>();
        this.unitDropdowns = new HashMap<>();
        this.variableLockStatus = new HashMap<>();
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        initializeUI();
    }

    private void initializeUI() {
        // Title
        JLabel title = new JLabel(formulaName + " Calculator", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        title.setForeground(new Color(0, 0, 128));
        add(title, BorderLayout.NORTH);

        // Formula display
        formulaLabel = new JLabel(getFormulaDisplay(formulaName), SwingConstants.CENTER);
        formulaLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formulaLabel.setForeground(new Color(0, 0, 128));
        formulaLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // Add formula label
        contentPanel.add(formulaLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create input panel
        createInputPanel();
        contentPanel.add(inputPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Output panel (initially hidden)
        outputPanel = new JPanel();
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
        outputPanel.setBackground(Color.WHITE);
        outputPanel.setVisible(false);
        contentPanel.add(outputPanel);

        add(contentPanel, BorderLayout.CENTER);

        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(Color.WHITE);
        
        backButton = new JButton("← Back to Formula List");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backButton.addActionListener(e -> mainWindow.backToFormulaList());
        
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        refreshButton.setBackground(new Color(255, 200, 200));
        refreshButton.addActionListener(e -> refreshCalculator());
        
        bottomPanel.add(backButton);
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void createInputPanel() {
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Enter Values (Leave one variable empty to calculate it)"));

        String[] variables = getVariablesForFormula(formulaName);
        if (variables != null) {
            for (String variable : variables) {
                JPanel varPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                varPanel.setBackground(Color.WHITE);
                
                JLabel varLabel = new JLabel(variable + ":");
                varLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                varLabel.setPreferredSize(new Dimension(80, 30));
                inputLabels.put(variable, varLabel);
                
                JTextField textField = new JTextField();
                textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                textField.setPreferredSize(new Dimension(120, 30));
                textField.setToolTipText("Leave empty to calculate " + variable);
                inputFields.put(variable, textField);
                
                // Replace fixed unit label with dropdown
                JComboBox<String> unitComboBox = new JComboBox<>(getAvailableUnitsForVariable(variable));
                unitComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                unitComboBox.setPreferredSize(new Dimension(80, 30));
                unitDropdowns.put(variable, unitComboBox);
                
                varPanel.add(varLabel);
                varPanel.add(textField);
                varPanel.add(unitComboBox);
                
                inputPanel.add(varPanel);
                inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        // Calculate button
        calculateButton = new JButton("Calculate ");
        calculateButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        calculateButton.setBackground(new Color(100, 200, 100));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        calculateButton.setPreferredSize(new Dimension(250, 40));
        calculateButton.addActionListener(new CalculateButtonListener());

        inputPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        inputPanel.add(calculateButton);
    }

    private class CalculateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            calculateFormula();
        }
    }

    private void calculateFormula() {
        try {
            // Find which variable is empty (target variable)
            int emptyCount = 0;
            String emptyVariable = null;
            Map<String, Double> values = new HashMap<>();
            
            for (String variable : inputFields.keySet()) {
                JTextField field = inputFields.get(variable);
                String text = field.getText().trim();
                
                if (text.isEmpty()) {
                    emptyCount++;
                    emptyVariable = variable;
                } else {
                    double value = Double.parseDouble(text);
                    String selectedUnit = (String) unitDropdowns.get(variable).getSelectedItem();
                    
                    // Convert to base SI units
                    value = convertToBaseUnit(value, selectedUnit, variable);
                    
                    // Validate values
                    if (variable.toLowerCase().contains("time") && value == 0) {
                        JOptionPane.showMessageDialog(this, 
                            "Time cannot be zero.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (value < 0 && !isAngleVariable(variable)) {
                        JOptionPane.showMessageDialog(this, 
                            variable + " cannot be negative.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    values.put(variable, value);
                }
            }
            
            if (emptyCount != 1) {
                JOptionPane.showMessageDialog(this, 
                    "Please leave exactly ONE variable empty to calculate.", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            this.targetVariable = emptyVariable;
            
            // Perform calculation
            double result = performCalculation(formulaName, emptyVariable, values);
            
            // Display result with all unit conversions
            showOutputWithAllUnits(result, emptyVariable, values);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numbers for all fields.", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Calculation error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private double performCalculation(String formula, String targetVariable, Map<String, Double> values) {
        switch (formula) {
            // ========== MECHANICS ==========
            case "Velocity":
                if (targetVariable.equals("v")) return values.get("d") / values.get("t");
                if (targetVariable.equals("d")) return values.get("v") * values.get("t");
                return values.get("d") / values.get("v"); // t
                
            case "Acceleration":
                if (targetVariable.equals("a")) return (values.get("v") - values.get("u")) / values.get("t");
                if (targetVariable.equals("v")) return values.get("u") + values.get("a") * values.get("t");
                if (targetVariable.equals("u")) return values.get("v") - values.get("a") * values.get("t");
                return (values.get("v") - values.get("u")) / values.get("a"); // t
            
            case "Time":
                 if (targetVariable.equals("t")) return (values.get("v") - values.get("u")) / values.get("a");
                 if (targetVariable.equals("v")) return values.get("u") + values.get("a") * values.get("t");
                 if (targetVariable.equals("u")) return values.get("v") - values.get("a") * values.get("t");
                 return (values.get("v") - values.get("u")) / values.get("t"); // a

            case "Force":
                if (targetVariable.equals("F")) return values.get("m") * values.get("a");
                if (targetVariable.equals("m")) return values.get("F") / values.get("a");
                return values.get("F") / values.get("m"); // a
                
            case "Work":
                if (targetVariable.equals("W")) return values.get("F") * values.get("d") * Math.cos(Math.toRadians(values.getOrDefault("θ", 0.0)));
                if (targetVariable.equals("F")) return values.get("W") / (values.get("d") * Math.cos(Math.toRadians(values.getOrDefault("θ", 0.0))));
                return values.get("W") / (values.get("F") * Math.cos(Math.toRadians(values.getOrDefault("θ", 0.0)))); // d
                
            case "Power":
                if (targetVariable.equals("P")) return values.get("W") / values.get("t");
                if (targetVariable.equals("W")) return values.get("P") * values.get("t");
                return values.get("W") / values.get("P"); // t
                
            case "Kinetic Energy":
                if (targetVariable.equals("KE")) return 0.5 * values.get("m") * Math.pow(values.get("v"), 2);
                if (targetVariable.equals("m")) return (2 * values.get("KE")) / Math.pow(values.get("v"), 2);
                return Math.sqrt((2 * values.get("KE")) / values.get("m")); // v
                
            case "Potential Energy":
                if (targetVariable.equals("PE")) return values.get("m") * values.get("g") * values.get("h");
                if (targetVariable.equals("m")) return values.get("PE") / (values.get("g") * values.get("h"));
                if (targetVariable.equals("h")) return values.get("PE") / (values.get("m") * values.get("g"));
                return values.get("PE") / (values.get("m") * values.get("h")); // g
                
            case "Momentum":
                if (targetVariable.equals("p")) return values.get("m") * values.get("v");
                if (targetVariable.equals("m")) return values.get("p") / values.get("v");
                return values.get("p") / values.get("m"); // v
            
            case "Mass":
                if (targetVariable.equals("m")) return values.get("F") / values.get("a");
                if (targetVariable.equals("F")) return values.get("m") * values.get("a");
                return values.get("F") / values.get("m"); // a

            case "Impulse":
                if (targetVariable.equals("J")) return values.get("F") * values.get("t");
                if (targetVariable.equals("F")) return values.get("J") / values.get("t");
                return values.get("J") / values.get("F"); // t

            case "Centripetal Force":
                if (targetVariable.equals("Fc")) return (values.get("m") * Math.pow(values.get("v"), 2)) / values.get("r");
                if (targetVariable.equals("v")) return Math.sqrt((values.get("Fc") * values.get("r")) / values.get("m"));
                if (targetVariable.equals("r")) return (values.get("m") * Math.pow(values.get("v"), 2)) / values.get("Fc");
                return (values.get("Fc") * values.get("r")) / Math.pow(values.get("v"), 2); // m

           case "Centripetal Acceleration":
                if (targetVariable.equals("ac")) return Math.pow(values.get("v"), 2) / values.get("r");
                if (targetVariable.equals("v")) return Math.sqrt(values.get("ac") * values.get("r"));
                return Math.pow(values.get("v"), 2) / values.get("ac"); // r

            case "Torque":
                if (targetVariable.equals("τ")) return values.get("r") * values.get("F") * Math.sin(values.get("θ"));
                if (targetVariable.equals("F")) return values.get("τ") / (values.get("r") * Math.sin(values.get("θ")));
                if (targetVariable.equals("r")) return values.get("τ") / (values.get("F") * Math.sin(values.get("θ")));
                return Math.asin(values.get("τ") / (values.get("r") * values.get("F"))); // θ

            case "Angular Momentum":
                if (targetVariable.equals("L")) return values.get("I") * values.get("ω");
                if (targetVariable.equals("I")) return values.get("L") / values.get("ω");
                return values.get("L") / values.get("I"); // ω

            case "Angular Velocity":
                if (targetVariable.equals("ω")) return values.get("θ") / values.get("t");
                if (targetVariable.equals("θ")) return values.get("ω") * values.get("t");
                return values.get("θ") / values.get("ω"); // t

            case "Angular Acceleration":
                if (targetVariable.equals("α")) return (values.get("ω") - values.get("ω₀")) / values.get("t");
                if (targetVariable.equals("ω")) return values.get("ω₀") + values.get("α") * values.get("t");
                if (targetVariable.equals("ω₀")) return values.get("ω") - values.get("α") * values.get("t");
                return (values.get("ω") - values.get("ω₀")) / values.get("α"); // t

            case "Rotational Kinetic Energy":
                if (targetVariable.equals("KE")) return 0.5 * values.get("I") * Math.pow(values.get("ω"), 2);
                if (targetVariable.equals("I")) return (2 * values.get("KE")) / Math.pow(values.get("ω"), 2);
                return Math.sqrt((2 * values.get("KE")) / values.get("I")); // ω

            case "First Equation of Motion":
                if (targetVariable.equals("v")) return values.get("u") + values.get("a") * values.get("t");
                if (targetVariable.equals("u")) return values.get("v") - values.get("a") * values.get("t");
                if (targetVariable.equals("a")) return (values.get("v") - values.get("u")) / values.get("t");
                return (values.get("v") - values.get("u")) / values.get("a"); // t

            case "Second Equation of Motion":
                if (targetVariable.equals("s")) return values.get("u") * values.get("t") + 0.5 * values.get("a") * Math.pow(values.get("t"), 2);
                if (targetVariable.equals("u")) return (values.get("s") - 0.5 * values.get("a") * Math.pow(values.get("t"), 2)) / values.get("t");
                if (targetVariable.equals("a")) return (2 * (values.get("s") - values.get("u") * values.get("t"))) / Math.pow(values.get("t"), 2);
                return Math.sqrt((2 * (values.get("s") - values.get("u") * values.get("t"))) / values.get("a")); // t (approx)

            case "Third Equation of Motion":
                if (targetVariable.equals("v")) return Math.sqrt(Math.pow(values.get("u"), 2) + 2 * values.get("a") * values.get("s"));
                if (targetVariable.equals("u")) return Math.sqrt(Math.pow(values.get("v"), 2) - 2 * values.get("a") * values.get("s"));
                if (targetVariable.equals("a")) return (Math.pow(values.get("v"), 2) - Math.pow(values.get("u"), 2)) / (2 * values.get("s"));
                return (Math.pow(values.get("v"), 2) - Math.pow(values.get("u"), 2)) / (2 * values.get("a")); // s

            case "Maximum Height":
                if (targetVariable.equals("H")) return Math.pow(values.get("u") * Math.sin(values.get("θ")), 2) / (2 * values.get("g"));
                if (targetVariable.equals("u")) return Math.sqrt((2 * values.get("H") * values.get("g"))) / Math.sin(values.get("θ"));
                if (targetVariable.equals("θ")) return Math.asin(Math.sqrt((2 * values.get("H") * values.get("g")) / Math.pow(values.get("u"), 2)));
                return (Math.pow(values.get("u") * Math.sin(values.get("θ")), 2)) / (2 * values.get("H")); // g

            case "Time of Flight":
                if (targetVariable.equals("T")) return (2 * values.get("u") * Math.sin(values.get("θ"))) / values.get("g");
                if (targetVariable.equals("u")) return (values.get("T") * values.get("g")) / (2 * Math.sin(values.get("θ")));
                if (targetVariable.equals("θ")) return Math.asin((values.get("T") * values.get("g")) / (2 * values.get("u")));
                return (2 * values.get("u") * Math.sin(values.get("θ"))) / values.get("T"); // g

            case "Tension":
                if (targetVariable.equals("T")) {
                    if (values.containsKey("direction") && values.get("direction") == 1)
                        return values.get("m") * (values.get("g") + values.get("a")); // downward
                    else
                        return values.get("m") * (values.get("g") - values.get("a")); // upward
                }
                if (targetVariable.equals("m")) {
                    double a = values.getOrDefault("a", 0.0);
                    return values.get("T") / (values.get("g") + a);
                }
                return values.get("T") / values.get("m"); // g or a

            case "Friction":
                if (targetVariable.equals("f")) return values.get("μ") * values.get("N");
                if (targetVariable.equals("μ")) return values.get("f") / values.get("N");
                return values.get("f") / values.get("μ"); // N

            case "Viscosity":
                if (targetVariable.equals("F")) return values.get("η") * values.get("A") * (values.get("dv") / values.get("dy"));
                if (targetVariable.equals("η")) return values.get("F") / (values.get("A") * (values.get("dv") / values.get("dy")));
                if (targetVariable.equals("A")) return values.get("F") / (values.get("η") * (values.get("dv") / values.get("dy")));
                return values.get("η") * values.get("A") * (values.get("dy") / values.get("dv")); // dv or dy

            case "Collision":
                if (targetVariable.equals("v1"))
                    return (values.get("m1") * values.get("u1") + values.get("m2") * (values.get("u2") - values.get("v2"))) / values.get("m1");
                if (targetVariable.equals("v2"))
                    return (values.get("m1") * (values.get("u1") - values.get("v1")) + values.get("m2") * values.get("u2")) / values.get("m2");
                return (values.get("m1") * values.get("u1") + values.get("m2") * values.get("u2")) - (values.get("m1") * values.get("v1") + values.get("m2") * values.get("v2")); // check conservation

            // ========== GRAVITATION ==========
            case "Gravitational Force":
                if (targetVariable.equals("F")) return (G * values.get("m1") * values.get("m2")) / Math.pow(values.get("r"), 2);
                if (targetVariable.equals("m1")) return (values.get("F") * Math.pow(values.get("r"), 2)) / (G * values.get("m2"));
                if (targetVariable.equals("m2")) return (values.get("F") * Math.pow(values.get("r"), 2)) / (G * values.get("m1"));
                return Math.sqrt((G * values.get("m1") * values.get("m2")) / values.get("F")); // r
                
            case "Acceleration due to Gravity":
                if (targetVariable.equals("g")) return (G * values.get("M")) / Math.pow(values.get("r"), 2);
                if (targetVariable.equals("M")) return (values.get("g") * Math.pow(values.get("r"), 2)) / G;
                return Math.sqrt((G * values.get("M")) / values.get("g")); // r

            // ========== FLUID MECHANICS ==========
            case "Pressure":
                if (targetVariable.equals("P")) return values.get("F") / values.get("A");
                if (targetVariable.equals("F")) return values.get("P") * values.get("A");
                return values.get("F") / values.get("P"); // A
                
            case "Density":
                if (targetVariable.equals("ρ")) return values.get("m") / values.get("V");
                if (targetVariable.equals("m")) return values.get("ρ") * values.get("V");
                return values.get("m") / values.get("ρ"); // V

            // ========== THERMODYNAMICS ==========
            case "Temperature Conversion":
                if (targetVariable.equals("K")) {
                    if (values.containsKey("°C")) return values.get("°C") + 273.15;
                    return (values.get("°F") - 32) * 5.0/9.0 + 273.15;
                } else if (targetVariable.equals("°C")) {
                    if (values.containsKey("K")) return values.get("K") - 273.15;
                    return (values.get("°F") - 32) * 5.0/9.0;
                } else { // °F
                    if (values.containsKey("K")) return (values.get("K") - 273.15) * 9.0/5.0 + 32;
                    return values.get("°C") * 9.0/5.0 + 32;
                }

            // ========== WAVES AND OSCILLATIONS ==========
            case "Wave Speed":
                if (targetVariable.equals("v")) return values.get("f") * values.get("λ");
                if (targetVariable.equals("f")) return values.get("v") / values.get("λ");
                return values.get("v") / values.get("f"); // λ

            // ========== OPTICS ==========
            case "Lens Formula":
                if (targetVariable.equals("f")) return 1.0 / ((1.0 / values.get("v")) - (1.0 / values.get("u")));
                if (targetVariable.equals("v")) return 1.0 / ((1.0 / values.get("f")) + (1.0 / values.get("u")));
                return 1.0 / ((1.0 / values.get("f")) - (1.0 / values.get("v"))); // u

            // ========== ELECTRICITY AND MAGNETISM ==========
            case "Ohm's Law":
                if (targetVariable.equals("V")) return values.get("I") * values.get("R");
                if (targetVariable.equals("I")) return values.get("V") / values.get("R");
                return values.get("V") / values.get("I"); // R
                
            case "Electric Power":
                if (targetVariable.equals("P")) return values.get("V") * values.get("I");
                if (targetVariable.equals("V")) return values.get("P") / values.get("I");
                return values.get("P") / values.get("V"); // I
                
            case "Coulomb's Law":
                if (targetVariable.equals("F")) return k * values.get("q1") * values.get("q2") / Math.pow(values.get("r"), 2);
                if (targetVariable.equals("q1")) return values.get("F") * Math.pow(values.get("r"), 2) / (k * values.get("q2"));
                return Math.sqrt(k * values.get("q1") * values.get("q2") / values.get("F")); // r

            // ========== MODERN PHYSICS ==========
            case "Mass-Energy Equivalence":
                if (targetVariable.equals("E")) return values.get("m") * Math.pow(c, 2);
                return values.get("E") / Math.pow(c, 2); // m
                
            case "Photon Energy":
                if (targetVariable.equals("E")) return h * values.get("f");
                if (targetVariable.equals("f")) return values.get("E") / h;
                return values.get("E") / values.get("f"); // h

            // ========== VECTOR ==========
            case "Displacement Vector":
                double dx = values.get("x2") - values.get("x1");
                double dy = values.get("y2") - values.get("y1");
                return Math.sqrt(dx*dx + dy*dy);

            default:
                throw new IllegalArgumentException("Formula not implemented: " + formula);
        }
    }

    private void showOutputWithAllUnits(double baseResult, String calculatedVariable, Map<String, Double> inputValues) {
        outputPanel.removeAll();
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
        outputPanel.setVisible(true);
        
        // Get the selected unit for the calculated variable
        String selectedUnit = (String) unitDropdowns.get(calculatedVariable).getSelectedItem();
        double convertedResult = convertFromBaseUnit(baseResult, selectedUnit, calculatedVariable);
        
        // Main result title
        JLabel resultTitle = new JLabel("Result:");
        resultTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        resultTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultTitle.setForeground(new Color(0, 100, 0));
        
        // Main result in selected unit
        String resultText = String.format("%s = %.6f %s", calculatedVariable, convertedResult, selectedUnit);
        JLabel resultLabel = new JLabel(resultText);
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultLabel.setForeground(new Color(0, 0, 128));
        
        // All unit conversions section
        JLabel conversionsTitle = new JLabel("All Unit Conversions:");
        conversionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        conversionsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        conversionsTitle.setForeground(new Color(70, 70, 70));
        
        outputPanel.add(resultTitle);
        outputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        outputPanel.add(resultLabel);
        outputPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        outputPanel.add(conversionsTitle);
        outputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Add all unit conversions based on variable type
        addAllUnitConversions(baseResult, calculatedVariable);
        
        // Input values used
        JLabel inputTitle = new JLabel("Input Values Used:");
        inputTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        inputTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputTitle.setForeground(Color.DARK_GRAY);
        
        JLabel formulaWithValues = new JLabel(getFormulaWithValues(calculatedVariable, inputValues, baseResult));
        formulaWithValues.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formulaWithValues.setAlignmentX(Component.CENTER_ALIGNMENT);
        formulaWithValues.setForeground(Color.DARK_GRAY);
        
        outputPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        outputPanel.add(inputTitle);
        outputPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        outputPanel.add(formulaWithValues);
        
        outputPanel.revalidate();
        outputPanel.repaint();
    }

    private void addAllUnitConversions(double baseResult, String calculatedVariable) {
        String[] allUnits = getAllPossibleUnitsForVariable(calculatedVariable);
        
        JPanel conversionsPanel = new JPanel();
        conversionsPanel.setLayout(new BoxLayout(conversionsPanel, BoxLayout.Y_AXIS));
        conversionsPanel.setBackground(Color.WHITE);
        conversionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        for (String unit : allUnits) {
            double convertedValue = convertFromBaseUnit(baseResult, unit, calculatedVariable);
            String conversionText = String.format("  %s = %.6f %s", calculatedVariable, convertedValue, unit);
            
            JLabel conversionLabel = new JLabel(conversionText);
            conversionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            conversionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            conversionLabel.setForeground(new Color(50, 50, 50));
            
            conversionsPanel.add(conversionLabel);
            conversionsPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        }
        
        JScrollPane scrollPane = new JScrollPane(conversionsPanel);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        scrollPane.setMaximumSize(new Dimension(400, 150));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        outputPanel.add(scrollPane);
    }

    private String[] getAllPossibleUnitsForVariable(String variable) {
        if (isLengthVariable(variable)) {
            return new String[]{"m", "cm", "mm", "km", "ft", "in", "mi", "yd", "nm"};
        } else if (isMassVariable(variable)) {
            return new String[]{"kg", "g", "mg", "lb", "oz", "ton"};
        } else if (isTimeVariable(variable)) {
            return new String[]{"s", "ms", "μs", "min", "h", "day", "week"};
        } else if (isForceVariable(variable)) {
            return new String[]{"N", "dyne", "lbf", "kgf"};
        } else if (isEnergyVariable(variable)) {
            return new String[]{"J", "kJ", "cal", "kcal", "eV", "erg", "BTU"};
        } else if (isVelocityVariable(variable)) {
            return new String[]{"m/s", "km/h", "mph", "ft/s", "knot"};
        } else if (isPowerVariable(variable)) {
            return new String[]{"W", "kW", "MW", "hp"};
        } else if (isPressureVariable(variable)) {
            return new String[]{"Pa", "kPa", "MPa", "bar", "atm", "psi"};
        } else if (isAngleVariable(variable)) {
            return new String[]{"rad", "°", "grad"};
        } else if (isTemperatureVariable(variable)) {
            return new String[]{"K", "°C", "°F"};
        } else if (isCurrentVariable(variable)) {
            return new String[]{"A", "mA", "μA"};
        } else if (isVoltageVariable(variable)) {
            return new String[]{"V", "mV", "kV"};
        } else if (isResistanceVariable(variable)) {
            return new String[]{"Ω", "kΩ", "MΩ"};
        } else if (isChargeVariable(variable)) {
            return new String[]{"C", "mC", "μC"};
        }
        // Default - return only base unit
        return new String[]{getUnitForVariable(variable)};
    }

    // Helper methods for variable type detection
    private boolean isLengthVariable(String variable) {
        return variable.equals("d") || variable.equals("r") || variable.equals("h") || 
               variable.equals("s") || variable.equals("λ") || variable.equals("dy") ||
               variable.contains("x") || variable.contains("y");
    }

    private boolean isMassVariable(String variable) {
        return variable.equals("m") || variable.equals("m1") || variable.equals("m2") || 
               variable.equals("M");
    }

    private boolean isTimeVariable(String variable) {
        return variable.equals("t") || variable.equals("T");
    }

    private boolean isForceVariable(String variable) {
        return variable.equals("F") || variable.equals("Fc") || variable.equals("N") || 
               variable.equals("f") || variable.equals("Tension");
    }

    private boolean isEnergyVariable(String variable) {
        return variable.equals("W") || variable.equals("KE") || variable.equals("PE") || 
               variable.equals("E");
    }

    private boolean isVelocityVariable(String variable) {
        return variable.equals("v") || variable.equals("u") || variable.equals("dv");
    }

    private boolean isPowerVariable(String variable) {
        return variable.equals("P");
    }

    private boolean isPressureVariable(String variable) {
        return variable.equals("P");
    }

    private boolean isAngleVariable(String variable) {
        return variable.equals("θ");
    }

    private boolean isTemperatureVariable(String variable) {
        return variable.equals("K") || variable.equals("°C") || variable.equals("°F");
    }

    private boolean isCurrentVariable(String variable) {
        return variable.equals("I");
    }

    private boolean isVoltageVariable(String variable) {
        return variable.equals("V");
    }

    private boolean isResistanceVariable(String variable) {
        return variable.equals("R");
    }

    private boolean isChargeVariable(String variable) {
        return variable.equals("q1") || variable.equals("q2") || variable.equals("q");
    }

    // Enhanced conversion methods
    private double convertToBaseUnit(double value, String unit, String variable) {
        if (isLengthVariable(variable)) {
            switch (unit) {
                case "cm": return value / 100;
                case "mm": return value / 1000;
                case "km": return value * 1000;
                case "ft": return value * 0.3048;
                case "in": return value * 0.0254;
                case "mi": return value * 1609.34;
                case "yd": return value * 0.9144;
                case "nm": return value * 1e-9;
                default: return value; // m
            }
        } else if (isMassVariable(variable)) {
            switch (unit) {
                case "g": return value / 1000;
                case "mg": return value / 1e6;
                case "lb": return value * 0.453592;
                case "oz": return value * 0.0283495;
                case "ton": return value * 1000;
                default: return value; // kg
            }
        } else if (isTimeVariable(variable)) {
            switch (unit) {
                case "ms": return value / 1000;
                case "μs": return value / 1e6;
                case "min": return value * 60;
                case "h": return value * 3600;
                case "day": return value * 86400;
                case "week": return value * 604800;
                default: return value; // s
            }
        } else if (isForceVariable(variable)) {
            switch (unit) {
                case "dyne": return value * 1e-5;
                case "lbf": return value * 4.44822;
                case "kgf": return value * 9.80665;
                default: return value; // N
            }
        } else if (isEnergyVariable(variable)) {
            switch (unit) {
                case "kJ": return value * 1000;
                case "cal": return value * 4.184;
                case "kcal": return value * 4184;
                case "eV": return value * 1.602e-19;
                case "erg": return value * 1e-7;
                case "BTU": return value * 1055.06;
                default: return value; // J
            }
        } else if (isVelocityVariable(variable)) {
            switch (unit) {
                case "km/h": return value * 0.277778;
                case "mph": return value * 0.44704;
                case "ft/s": return value * 0.3048;
                case "knot": return value * 0.514444;
                default: return value; // m/s
            }
        } else if (isPowerVariable(variable)) {
            switch (unit) {
                case "kW": return value * 1000;
                case "MW": return value * 1e6;
                case "hp": return value * 745.7;
                default: return value; // W
            }
        } else if (isPressureVariable(variable)) {
            switch (unit) {
                case "kPa": return value * 1000;
                case "MPa": return value * 1e6;
                case "bar": return value * 1e5;
                case "atm": return value * 101325;
                case "psi": return value * 6894.76;
                default: return value; // Pa
            }
        } else if (isAngleVariable(variable)) {
            switch (unit) {
                case "°": return Math.toRadians(value);
                case "grad": return value * Math.PI / 200;
                default: return value; // rad
            }
        } else if (isCurrentVariable(variable)) {
            switch (unit) {
                case "mA": return value / 1000;
                case "μA": return value / 1e6;
                default: return value; // A
            }
        } else if (isVoltageVariable(variable)) {
            switch (unit) {
                case "mV": return value / 1000;
                case "kV": return value * 1000;
                default: return value; // V
            }
        } else if (isResistanceVariable(variable)) {
            switch (unit) {
                case "kΩ": return value * 1000;
                case "MΩ": return value * 1e6;
                default: return value; // Ω
            }
        } else if (isChargeVariable(variable)) {
            switch (unit) {
                case "mC": return value / 1000;
                case "μC": return value / 1e6;
                default: return value; // C
            }
        } else if (isTemperatureVariable(variable)) {
            // Temperature conversions are handled separately in performCalculation
            return value;
        }
        return value; // No conversion needed
    }

    private double convertFromBaseUnit(double value, String unit, String variable) {
        if (isLengthVariable(variable)) {
            switch (unit) {
                case "cm": return value * 100;
                case "mm": return value * 1000;
                case "km": return value / 1000;
                case "ft": return value / 0.3048;
                case "in": return value / 0.0254;
                case "mi": return value / 1609.34;
                case "yd": return value / 0.9144;
                case "nm": return value / 1e-9;
                default: return value; // m
            }
        } else if (isMassVariable(variable)) {
            switch (unit) {
                case "g": return value * 1000;
                case "mg": return value * 1e6;
                case "lb": return value / 0.453592;
                case "oz": return value / 0.0283495;
                case "ton": return value / 1000;
                default: return value; // kg
            }
        } else if (isTimeVariable(variable)) {
            switch (unit) {
                case "ms": return value * 1000;
                case "μs": return value * 1e6;
                case "min": return value / 60;
                case "h": return value / 3600;
                case "day": return value / 86400;
                case "week": return value / 604800;
                default: return value; // s
            }
        } else if (isForceVariable(variable)) {
            switch (unit) {
                case "dyne": return value / 1e-5;
                case "lbf": return value / 4.44822;
                case "kgf": return value / 9.80665;
                default: return value; // N
            }
        } else if (isEnergyVariable(variable)) {
            switch (unit) {
                case "kJ": return value / 1000;
                case "cal": return value / 4.184;
                case "kcal": return value / 4184;
                case "eV": return value / 1.602e-19;
                case "erg": return value / 1e-7;
                case "BTU": return value / 1055.06;
                default: return value; // J
            }
        } else if (isVelocityVariable(variable)) {
            switch (unit) {
                case "km/h": return value / 0.277778;
                case "mph": return value / 0.44704;
                case "ft/s": return value / 0.3048;
                case "knot": return value / 0.514444;
                default: return value; // m/s
            }
        } else if (isPowerVariable(variable)) {
            switch (unit) {
                case "kW": return value / 1000;
                case "MW": return value / 1e6;
                case "hp": return value / 745.7;
                default: return value; // W
            }
        } else if (isPressureVariable(variable)) {
            switch (unit) {
                case "kPa": return value / 1000;
                case "MPa": return value / 1e6;
                case "bar": return value / 1e5;
                case "atm": return value / 101325;
                case "psi": return value / 6894.76;
                default: return value; // Pa
            }
        } else if (isAngleVariable(variable)) {
            switch (unit) {
                case "°": return Math.toDegrees(value);
                case "grad": return value * 200 / Math.PI;
                default: return value; // rad
            }
        } else if (isCurrentVariable(variable)) {
            switch (unit) {
                case "mA": return value * 1000;
                case "μA": return value * 1e6;
                default: return value; // A
            }
        } else if (isVoltageVariable(variable)) {
            switch (unit) {
                case "mV": return value * 1000;
                case "kV": return value / 1000;
                default: return value; // V
            }
        } else if (isResistanceVariable(variable)) {
            switch (unit) {
                case "kΩ": return value / 1000;
                case "MΩ": return value / 1e6;
                default: return value; // Ω
            }
        } else if (isChargeVariable(variable)) {
            switch (unit) {
                case "mC": return value * 1000;
                case "μC": return value * 1e6;
                default: return value; // C
            }
        }
        return value; // No conversion needed
    }

    private String[] getAvailableUnitsForVariable(String variable) {
        if (isLengthVariable(variable)) {
            return new String[]{"m", "cm", "mm", "km", "ft", "in", "mi"};
        } else if (isMassVariable(variable)) {
            return new String[]{"kg", "g", "mg", "lb", "oz"};
        } else if (isTimeVariable(variable)) {
            return new String[]{"s", "ms", "min", "h", "day"};
        } else if (isForceVariable(variable)) {
            return new String[]{"N", "dyne", "lbf"};
        } else if (isEnergyVariable(variable)) {
            return new String[]{"J", "erg", "cal", "eV"};
        } else if (isVelocityVariable(variable)) {
            return new String[]{"m/s", "km/h", "mph", "ft/s"};
        } else if (isAngleVariable(variable)) {
            return new String[]{"°", "rad", "grad"};
        } else if (isCurrentVariable(variable)) {
            return new String[]{"A", "mA"};
        } else if (isVoltageVariable(variable)) {
            return new String[]{"V", "mV", "kV"};
        } else if (isResistanceVariable(variable)) {
            return new String[]{"Ω", "kΩ", "MΩ"};
        } else if (isChargeVariable(variable)) {
            return new String[]{"C", "mC", "μC"};
        }
        return new String[]{getUnitForVariable(variable)};
    }

    private void refreshCalculator() {
        for (JTextField field : inputFields.values()) {
            field.setText("");
        }
        for (JComboBox<String> comboBox : unitDropdowns.values()) {
            comboBox.setSelectedIndex(0);
        }
        outputPanel.setVisible(false);
        targetVariable = null;
    }

    private String getFormulaDisplay(String formula) {
        Map<String, String> formulaMap = new HashMap<>();
        formulaMap.put("Velocity", "v = d / t");
        formulaMap.put("Acceleration", "a = (v - u) / t");
        formulaMap.put("Force", "F = m × a");
        formulaMap.put("Work", "W = F × d × cos(θ)");
        formulaMap.put("Power", "P = W / t");
        formulaMap.put("Kinetic Energy", "KE = ½ × m × v²");
        formulaMap.put("Potential Energy", "PE = m × g × h");
        formulaMap.put("Momentum", "p = m × v");
        formulaMap.put("Time", "t = (v - u) / a");
        formulaMap.put("Mass", "m = F / a");
        formulaMap.put("Impulse", "J = F * t");
        formulaMap.put("Centripetal Force", "Fc = (m * v^2) / r");
        formulaMap.put("Centripetal Acceleration", "ac = v^2 / r");
        formulaMap.put("Torque", "τ = r * F * sin(θ)");
        formulaMap.put("Angular Momentum", "L = I * ω");
        formulaMap.put("Angular Velocity", "ω = θ / t");
        formulaMap.put("Angular Acceleration", "α = (ω - ω₀) / t");
        formulaMap.put("Rotational Kinetic Energy", "KE = (1/2) * I * ω^2");
        formulaMap.put("First Equation of Motion", "v = u + a * t");
        formulaMap.put("Second Equation of Motion", "s = u * t + (1/2) * a * t^2");
        formulaMap.put("Third Equation of Motion", "v^2 = u^2 + 2 * a * s");
        formulaMap.put("Maximum Height", "H = (u^2 * sin^2θ) / (2 * g)");
        formulaMap.put("Time of Flight", "T = (2 * u * sinθ) / g");
        formulaMap.put("Tension", "T = m * (g ± a)  ← use (+) when moving downward, (–) when upward");
        formulaMap.put("Friction", "f = μ * N");
        formulaMap.put("Viscosity", "F = η * A * (dv/dy)");
        formulaMap.put("Collision", "m₁u₁ + m₂u₂ = m₁v₁ + m₂v₂");
        formulaMap.put("Gravitational Force", "F = G × m₁ × m₂ / r²");
        formulaMap.put("Acceleration due to Gravity", "g = G × M / r²");
        formulaMap.put("Pressure", "P = F / A");
        formulaMap.put("Density", "ρ = m / V");
        formulaMap.put("Temperature Conversion", "K = °C + 273.15 | °F = °C × 9/5 + 32");
        formulaMap.put("Wave Speed", "v = f × λ");
        formulaMap.put("Lens Formula", "1/f = 1/v - 1/u");
        formulaMap.put("Ohm's Law", "V = I × R");
        formulaMap.put("Electric Power", "P = V × I");
        formulaMap.put("Coulomb's Law", "F = k × q₁ × q₂ / r²");
        formulaMap.put("Mass-Energy Equivalence", "E = m × c²");
        formulaMap.put("Photon Energy", "E = h × f");
        formulaMap.put("Displacement Vector", "|d| = √((x₂-x₁)² + (y₂-y₁)²)");
        
        return formulaMap.getOrDefault(formula, formula);
    }

    private String[] getVariablesForFormula(String formula) {
        Map<String, String[]> variables = new HashMap<>();
        
        // Mechanics
        variables.put("Velocity", new String[]{"v", "d", "t"});
        variables.put("Acceleration", new String[]{"a", "v", "u", "t"});
        variables.put("Force", new String[]{"F", "m", "a"});
        variables.put("Work", new String[]{"W", "F", "d", "θ"});
        variables.put("Power", new String[]{"P", "W", "t"});
        variables.put("Kinetic Energy", new String[]{"KE", "m", "v"});
        variables.put("Potential Energy", new String[]{"PE", "m", "g", "h"});
        variables.put("Momentum", new String[]{"p", "m", "v"});
        variables.put("Time", new String[]{"t", "v", "u", "a"});
        variables.put("Mass", new String[]{"m", "F", "a"});
        variables.put("Impulse", new String[]{"J", "F", "t"});
        variables.put("Centripetal Force", new String[]{"Fc", "m", "v", "r"});
        variables.put("Centripetal Acceleration", new String[]{"ac", "v", "r"});
        variables.put("Torque", new String[]{"τ", "r", "F", "θ"});
        variables.put("Angular Momentum", new String[]{"L", "I", "ω"});
        variables.put("Angular Velocity", new String[]{"ω", "θ", "t"});
        variables.put("Angular Acceleration", new String[]{"α", "ω", "ω₀", "t"});
        variables.put("Rotational Kinetic Energy", new String[]{"KE", "I", "ω"});
        variables.put("First Equation of Motion", new String[]{"v", "u", "a", "t"});
        variables.put("Second Equation of Motion", new String[]{"s", "u", "a", "t"});
        variables.put("Third Equation of Motion", new String[]{"v", "u", "a", "s"});
        variables.put("Maximum Height", new String[]{"H", "u", "θ", "g"});
        variables.put("Time of Flight", new String[]{"T", "u", "θ", "g"});
        variables.put("Tension", new String[]{"T", "m", "g", "a"});
        variables.put("Friction", new String[]{"f", "μ", "N"});
        variables.put("Viscosity", new String[]{"F", "η", "A", "dv", "dy"});
        variables.put("Collision", new String[]{"m1", "u1", "m2", "u2", "v1", "v2"});

        
        // Gravitation
        variables.put("Gravitational Force", new String[]{"F", "m1", "m2", "r"});
        variables.put("Acceleration due to Gravity", new String[]{"g", "M", "r"});
        
        // Fluid Mechanics
        variables.put("Pressure", new String[]{"P", "F", "A"});
        variables.put("Density", new String[]{"ρ", "m", "V"});
        
        // Thermodynamics
        variables.put("Temperature Conversion", new String[]{"K", "°C", "°F"});
        
        // Waves and Oscillations
        variables.put("Wave Speed", new String[]{"v", "f", "λ"});
        
        // Optics
        variables.put("Lens Formula", new String[]{"f", "u", "v"});
        
        // Electricity and Magnetism
        variables.put("Ohm's Law", new String[]{"V", "I", "R"});
        variables.put("Electric Power", new String[]{"P", "V", "I"});
        variables.put("Coulomb's Law", new String[]{"F", "q1", "q2", "r"});
        
        // Modern Physics
        variables.put("Mass-Energy Equivalence", new String[]{"E", "m"});
        variables.put("Photon Energy", new String[]{"E", "f"});
        
        // Vector
        variables.put("Displacement Vector", new String[]{"x1", "y1", "x2", "y2"});
        
        return variables.getOrDefault(formula, new String[]{});
    }

    private String getUnitForVariable(String variable) {
        Map<String, String> units = new HashMap<>();
        units.put("v", "m/s");
        units.put("u", "m/s");
        units.put("a", "m/s²");
        units.put("d", "m");
        units.put("t", "s");
        units.put("F", "N");
        units.put("m", "kg");
        units.put("W", "J");
        units.put("P", "W");
        units.put("KE", "J");
        units.put("PE", "J");
        units.put("p", "kg·m/s");
        units.put("v", "m/s");
        units.put("u", "m/s");
        units.put("t", "s");
        units.put("s", "m");
        units.put("F", "N");
        units.put("m", "kg");
        units.put("Fc", "N");
        units.put("J", "N·s");
        units.put("τ", "N·m");
        units.put("θ", "rad");
        units.put("ω", "rad/s");
        units.put("ω₀", "rad/s");
        units.put("α", "rad/s²");
        units.put("I", "kg·m²");
        units.put("L", "kg·m²/s");
        units.put("KE", "J");
        units.put("H", "m");
        units.put("T", "s");
        units.put("g", "m/s²");
        units.put("Tension", "N");
        units.put("f", "N");
        units.put("μ", "—"); 
        units.put("N", "N");
        units.put("η", "Pa·s");
        units.put("A", "m²");
        units.put("dv", "m/s");
        units.put("dy", "m");
        units.put("r", "m");
        units.put("m1", "kg");
        units.put("m2", "kg");
        units.put("u1", "m/s");
        units.put("u2", "m/s");
        units.put("v1", "m/s");
        units.put("v2", "m/s");
        units.put("θ", "°");
        units.put("g", "m/s²");
        units.put("h", "m");
        units.put("m1", "kg");
        units.put("m2", "kg");
        units.put("M", "kg");
        units.put("r", "m");
        units.put("ρ", "kg/m³");
        units.put("A", "m²");
        units.put("V", "m³");
        units.put("K", "K");
        units.put("°C", "°C");
        units.put("°F", "°F");
        units.put("f", "Hz");
        units.put("λ", "m");
        units.put("u", "m");
        units.put("v", "m");
        units.put("I", "A");
        units.put("R", "Ω");
        units.put("q1", "C");
        units.put("q2", "C");
        units.put("E", "J");
        units.put("x1", "m");
        units.put("y1", "m");
        units.put("x2", "m");
        units.put("y2", "m");
        
        return units.getOrDefault(variable, "");
    }

    private String getFormulaWithValues(String calculatedVariable, Map<String, Double> inputValues, double result) {
        StringBuilder sb = new StringBuilder();
        sb.append(getFormulaDisplay(formulaName)).append(" => ");
        
        switch (formulaName) {
            case "Gravitational Force":
                if (calculatedVariable.equals("F")) {
                    sb.append(String.format("F = (6.67430×10⁻¹¹ × %.2f × %.2f) / %.2f² = %.6f N", 
                        inputValues.get("m1"), inputValues.get("m2"), inputValues.get("r"), result));
                } else if (calculatedVariable.equals("m1")) {
                    sb.append(String.format("m₁ = (%.6f × %.2f²) / (6.67430×10⁻¹¹ × %.2f) = %.6f kg", 
                        inputValues.get("F"), inputValues.get("r"), inputValues.get("m2"), result));
                }
                break;
                
            case "Velocity":
                sb.append(String.format("v = %.2f / %.2f = %.2f m/s", 
                    inputValues.get("d"), inputValues.get("t"), result));
                break;
                
            case "Acceleration":
                sb.append(String.format("a = (%.2f - %.2f) / %.2f = %.2f m/s²", 
                    inputValues.get("v"), inputValues.get("u"), inputValues.get("t"), result));
                break;
                
            case "Mass-Energy Equivalence":
                sb.append(String.format("E = %.2f × (299792458)² = %.6f J", 
                    inputValues.get("m"), result));
                break;
                
            default:
                sb.append(String.format("Result: %.6f %s", result, getUnitForVariable(calculatedVariable)));
        }
        
        return sb.toString();
    }
}