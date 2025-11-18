
























package Ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;

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

    // constants
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

                // Make constant fields read-only and pre-filled
            if (isConstantVariable(variable)) {
                textField.setText(getConstantValue(variable));
                textField.setEditable(false);
                textField.setBackground(new Color(240, 240, 240));
                textField.setToolTipText("Constant value: " + getConstantValue(variable) + " " + getUnitForConstant(variable));
            } else {
                textField.setToolTipText("Leave empty to calculate " + variable);
            }
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
            // MECHANICS
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
                if (targetVariable.equals("τ")) return values.get("r") * values.get("F") * Math.sin(Math.toRadians(values.get("θ")));
                if (targetVariable.equals("F")) return values.get("τ") / (values.get("r") * Math.sin(Math.toRadians(values.get("θ"))));
                if (targetVariable.equals("r")) return values.get("τ") / (values.get("F") * Math.sin(Math.toRadians(values.get("θ"))));
                return Math.toDegrees(Math.asin(values.get("τ") / (values.get("r") * values.get("F")))); // θ

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
                if (targetVariable.equals("H")) return Math.pow(values.get("u") * Math.sin(Math.toRadians(values.get("θ"))), 2) / (2 * values.get("g"));
                if (targetVariable.equals("u")) return Math.sqrt((2 * values.get("H") * values.get("g"))) / Math.sin(Math.toRadians(values.get("θ")));
                if (targetVariable.equals("θ")) return Math.toDegrees(Math.asin(Math.sqrt((2 * values.get("H") * values.get("g")) / Math.pow(values.get("u"), 2))));
                return (Math.pow(values.get("u") * Math.sin(Math.toRadians(values.get("θ"))), 2)) / (2 * values.get("H")); // g

            case "Time of Flight":
                if (targetVariable.equals("T")) return (2 * values.get("u") * Math.sin(Math.toRadians(values.get("θ")))) / values.get("g");
                if (targetVariable.equals("u")) return (values.get("T") * values.get("g")) / (2 * Math.sin(Math.toRadians(values.get("θ"))));
                if (targetVariable.equals("θ")) return Math.toDegrees(Math.asin((values.get("T") * values.get("g")) / (2 * values.get("u"))));
                return (2 * values.get("u") * Math.sin(Math.toRadians(values.get("θ")))) / values.get("T"); // g

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

            //  GRAVITATION
            case "Gravitational Force":
                if (targetVariable.equals("F")) return (G * values.get("m1") * values.get("m2")) / Math.pow(values.get("r"), 2);
                if (targetVariable.equals("m1")) return (values.get("F") * Math.pow(values.get("r"), 2)) / (G * values.get("m2"));
                if (targetVariable.equals("m2")) return (values.get("F") * Math.pow(values.get("r"), 2)) / (G * values.get("m1"));
                return Math.sqrt((G * values.get("m1") * values.get("m2")) / values.get("F")); // r
               
            case "Acceleration due to Gravity":
                if (targetVariable.equals("g")) return (G * values.get("M")) / Math.pow(values.get("r"), 2);
                if (targetVariable.equals("M")) return (values.get("g") * Math.pow(values.get("r"), 2)) / G;
                return Math.sqrt((G * values.get("M")) / values.get("g")); // r

            case "Gravitational Potential Energy":
               if (targetVariable.equals("U")) return - (G * values.get("M") * values.get("m")) / values.get("r");
               if (targetVariable.equals("M")) return - (values.get("U") * values.get("r")) / (G * values.get("m"));
               if (targetVariable.equals("m")) return - (values.get("U") * values.get("r")) / (G * values.get("M"));
               return - (G * values.get("M") * values.get("m")) / values.get("U"); // r

            case "Orbital Velocity":
               if (targetVariable.equals("v")) return Math.sqrt((G * values.get("M")) / values.get("r"));
               if (targetVariable.equals("M")) return (Math.pow(values.get("v"), 2) * values.get("r")) / G;
               return (G * values.get("M")) / Math.pow(values.get("v"), 2); // r

            case "Kepler's Laws":
               if (targetVariable.equals("T")) return 2 * Math.PI * Math.sqrt(Math.pow(values.get("r"), 3) / (G * values.get("M")));
               if (targetVariable.equals("r")) return Math.cbrt((Math.pow(values.get("T"), 2) * G * values.get("M")) / (4 * Math.PI * Math.PI));
               return (4 * Math.PI * Math.PI * Math.pow(values.get("r"), 3)) / (G * Math.pow(values.get("T"), 2)); // M

            case "Distance-Time Relation":
               if (targetVariable.equals("s")) return values.get("u") * values.get("t") + 0.5 * values.get("a") * Math.pow(values.get("t"), 2);
               if (targetVariable.equals("u")) return (values.get("s") - 0.5 * values.get("a") * Math.pow(values.get("t"), 2)) / values.get("t");
               if (targetVariable.equals("a")) return (2 * (values.get("s") - values.get("u") * values.get("t"))) / Math.pow(values.get("t"), 2);
               // For time, solve quadratic equation
               double a_val = 0.5 * values.get("a");
               double b_val = values.get("u");
               double c_val = -values.get("s");
               double discriminant = b_val * b_val - 4 * a_val * c_val;
               return (-b_val + Math.sqrt(discriminant)) / (2 * a_val); // t

            case "Velocity-Time Relation":
              if (targetVariable.equals("v")) return values.get("u") + values.get("a") * values.get("t");
              if (targetVariable.equals("u")) return values.get("v") - values.get("a") * values.get("t");
              if (targetVariable.equals("a")) return (values.get("v") - values.get("u")) / values.get("t");
              return (values.get("v") - values.get("u")) / values.get("a"); // t

            case "Moment of Inertia":
              if (targetVariable.equals("I")) return values.get("m") * Math.pow(values.get("r"), 2);
              if (targetVariable.equals("m")) return values.get("I") / Math.pow(values.get("r"), 2);
              return Math.sqrt(values.get("I") / values.get("m")); // r

            // ========== FLUID MECHANICS ==========
           case "Pressure":
              if (targetVariable.equals("P")) return values.get("F") / values.get("A");
              if (targetVariable.equals("F")) return values.get("P") * values.get("A");
              return values.get("F") / values.get("P"); // A

            case "Density":
              if (targetVariable.equals("ρ")) return values.get("m") / values.get("V");
              if (targetVariable.equals("m")) return values.get("ρ") * values.get("V");
              return values.get("m") / values.get("ρ"); // V

           case "Bernoulli's Principle":
   
              if (targetVariable.equals("P")) return values.get("constant") - (0.5 * values.get("ρ") * Math.pow(values.get("v"), 2)) - (values.get("ρ") * g * values.get("h"));
              if (targetVariable.equals("v")) return Math.sqrt(2 * (values.get("constant") - values.get("P") - values.get("ρ") * g * values.get("h")) / values.get("ρ"));
              if (targetVariable.equals("h")) return (values.get("constant") - values.get("P") - 0.5 * values.get("ρ") * Math.pow(values.get("v"), 2)) / (values.get("ρ") * g);
              return values.get("P") + (0.5 * values.get("ρ") * Math.pow(values.get("v"), 2)) + (values.get("ρ") * g * values.get("h"));

           case "Elasticity":
              if (targetVariable.equals("Y")) return (values.get("F") / values.get("A")) / (values.get("ΔL") / values.get("L"));
              if (targetVariable.equals("F")) return values.get("Y") * values.get("A") * (values.get("ΔL") / values.get("L"));
              if (targetVariable.equals("A")) return values.get("F") / (values.get("Y") * (values.get("ΔL") / values.get("L")));
              if (targetVariable.equals("ΔL")) return (values.get("F") / values.get("A")) * values.get("L") / values.get("Y");
              return (values.get("F") / values.get("A")) * values.get("L") / values.get("ΔL"); // L

           case "Pressure in Gases":
              if (targetVariable.equals("P")) return (1.0/3.0) * values.get("ρ") * Math.pow(values.get("v"), 2);
              if (targetVariable.equals("ρ")) return 3 * values.get("P") / Math.pow(values.get("v"), 2);
              return Math.sqrt(3 * values.get("P") / values.get("ρ")); // v

           case "Fluid Pressure (Hydrostatic Pressure)":
               if (targetVariable.equals("P")) return values.get("ρ") * g * values.get("h");
               if (targetVariable.equals("ρ")) return values.get("P") / (g * values.get("h"));
               if (targetVariable.equals("h")) return values.get("P") / (values.get("ρ") * g);
               return 0.0;

           case "Atmospheric Pressure":
               if (targetVariable.equals("P")) return values.get("h") * values.get("ρ") * g;
               if (targetVariable.equals("h")) return values.get("P") / (values.get("ρ") * g);
               if (targetVariable.equals("ρ")) return values.get("P") / (values.get("h") * g);
               return 0.0;

            case "Pascal's Law":
               if (targetVariable.equals("F1")) return values.get("F2") * values.get("A1") / values.get("A2");
               if (targetVariable.equals("F2")) return values.get("F1") * values.get("A2") / values.get("A1");
               if (targetVariable.equals("A1")) return values.get("F1") * values.get("A2") / values.get("F2");
               return values.get("F2") * values.get("A1") / values.get("F1"); // A2

           case "Pressure due to Depth in Fluid":
               if (targetVariable.equals("P")) return values.get("P0") + values.get("ρ") * g * values.get("h");
               if (targetVariable.equals("P0")) return values.get("P") - values.get("ρ") * g * values.get("h");
               if (targetVariable.equals("ρ")) return (values.get("P") - values.get("P0")) / (g * values.get("h"));
               if (targetVariable.equals("h")) return (values.get("P") - values.get("P0")) / (values.get("ρ") * g);
               return 0.0;

           case "Boyle's Law":
               if (targetVariable.equals("P1")) return values.get("P2") * values.get("V2") / values.get("V1");
               if (targetVariable.equals("P2")) return values.get("P1") * values.get("V1") / values.get("V2");
               if (targetVariable.equals("V1")) return values.get("P2") * values.get("V2") / values.get("P1");
               return values.get("P1") * values.get("V1") / values.get("P2"); // V2

           case "Charles's Law":
               if (targetVariable.equals("V1")) return values.get("V2") * values.get("T1") / values.get("T2");
               if (targetVariable.equals("V2")) return values.get("V1") * values.get("T2") / values.get("T1");
               if (targetVariable.equals("T1")) return values.get("V1") * values.get("T2") / values.get("V2");
               return values.get("V2") * values.get("T1") / values.get("V1"); // T2

           case "Gay-Lussac's Law":
               if (targetVariable.equals("P1")) return values.get("P2") * values.get("T1") / values.get("T2");
               if (targetVariable.equals("P2")) return values.get("P1") * values.get("T2") / values.get("T1");
               if (targetVariable.equals("T1")) return values.get("P1") * values.get("T2") / values.get("P2");
               return values.get("P2") * values.get("T1") / values.get("P1"); // T2

           case "Avogadro's Law":
               if (targetVariable.equals("V1")) return values.get("V2") * values.get("n1") / values.get("n2");
               if (targetVariable.equals("V2")) return values.get("V1") * values.get("n2") / values.get("n1");
               if (targetVariable.equals("n1")) return values.get("V1") * values.get("n2") / values.get("V2");
               return values.get("V2") * values.get("n1") / values.get("V1"); // n2

           case "Combined Gas Law":
               if (targetVariable.equals("P1")) return values.get("P2") * values.get("V2") * values.get("T1") / (values.get("V1") * values.get("T2"));
               if (targetVariable.equals("V1")) return values.get("P2") * values.get("V2") * values.get("T1") / (values.get("P1") * values.get("T2"));
               if (targetVariable.equals("T1")) return values.get("P1") * values.get("V1") * values.get("T2") / (values.get("P2") * values.get("V2"));
               if (targetVariable.equals("P2")) return values.get("P1") * values.get("V1") * values.get("T2") / (values.get("V2") * values.get("T1"));
               if (targetVariable.equals("V2")) return values.get("P1") * values.get("V1") * values.get("T2") / (values.get("P2") * values.get("T1"));
               return values.get("P2") * values.get("V2") * values.get("T1") / (values.get("P1") * values.get("V1")); // T2

           case "Ideal Gas Law":
                double R = 8.314; // J/mol·K
                if (targetVariable.equals("P")) return values.get("n") * R * values.get("T") / values.get("V");
                if (targetVariable.equals("V")) return values.get("n") * R * values.get("T") / values.get("P");
                if (targetVariable.equals("n")) return values.get("P") * values.get("V") / (R * values.get("T"));
                return values.get("P") * values.get("V") / (values.get("n") * R); // T

           case "Dalton's Law of Partial Pressure":
   
               double totalPressure = 0;
               for (String key : values.keySet()) {
               if (key.startsWith("P") && !key.equals("P_total")) {
               totalPressure += values.get(key);
            }
    }
                if (targetVariable.equals("P_total")) return totalPressure;
   
   
                if (targetVariable.startsWith("P")) {
                double otherPressures = 0;
                for (String key : values.keySet()) {
                if (key.startsWith("P") && !key.equals(targetVariable) && !key.equals("P_total")) {
                otherPressures += values.get(key);
                   }
            }
                return values.get("P_total") - otherPressures;
    }
                return 0.0;

            // ========== THERMODYNAMICS ==========
            case "Temperature Conversion":
                if (targetVariable.equals("K")) {
                    if (values.containsKey("°C")) return values.get("°C") + 273.15;
                    if (values.containsKey("°F")) return (values.get("°F") - 32) * 5/9 + 273.15;
                }
                if (targetVariable.equals("°C")) {
                    if (values.containsKey("K")) return values.get("K") - 273.15;
                    if (values.containsKey("°F")) return (values.get("°F") - 32) * 5/9;
                }
                if (targetVariable.equals("°F")) {
                    if (values.containsKey("°C")) return values.get("°C") * 9/5 + 32;
                    if (values.containsKey("K")) return (values.get("K") - 273.15) * 9/5 + 32;
                }
                return 0.0;

            case "First Law of Thermodynamics":
                if (targetVariable.equals("ΔQ")) return values.get("ΔU") + values.get("W");
                if (targetVariable.equals("ΔU")) return values.get("ΔQ") - values.get("W");
                return values.get("ΔQ") - values.get("ΔU"); // W

            case "Heat Engine Efficiency":
                if (targetVariable.equals("η")) return 1 - (values.get("Qc") / values.get("Qh"));
                if (targetVariable.equals("Qc")) return values.get("Qh") * (1 - values.get("η"));
                return values.get("Qc") / (1 - values.get("η")); // Qh

            case "Carnot Efficiency":
                if (targetVariable.equals("η")) return 1 - (values.get("Tc") / values.get("Th"));
                if (targetVariable.equals("Tc")) return values.get("Th") * (1 - values.get("η"));
                return values.get("Tc") / (1 - values.get("η")); // Th

            case "Entropy":
                if (targetVariable.equals("ΔS")) return values.get("ΔQ") / values.get("T");
                if (targetVariable.equals("ΔQ")) return values.get("ΔS") * values.get("T");
                return values.get("ΔQ") / values.get("ΔS"); // T

            // ========== WAVES AND OSCILLATIONS ==========
            case "Wave Speed":
                if (targetVariable.equals("v")) return values.get("f") * values.get("λ");
                if (targetVariable.equals("f")) return values.get("v") / values.get("λ");
                return values.get("v") / values.get("f"); // λ

            case "Period":
                if (targetVariable.equals("T")) return 1.0 / values.get("f");
                if (targetVariable.equals("f")) return 1.0 / values.get("T");
                return 1.0 / values.get("T"); // Default return

            case "Hooke's Law (Spring)":
                if (targetVariable.equals("F")) return values.get("k") * values.get("x");
                if (targetVariable.equals("k")) return values.get("F") / values.get("x");
                return values.get("F") / values.get("k"); // x

            case "Energy in Simple Harmonic Oscillator":
                if (targetVariable.equals("E")) return 0.5 * values.get("k") * Math.pow(values.get("A"), 2);
                if (targetVariable.equals("k")) return 2 * values.get("E") / Math.pow(values.get("A"), 2);
                return Math.sqrt(2 * values.get("E") / values.get("k")); // A

            case "Resonance Frequency":
                if (targetVariable.equals("f")) return (1.0 / (2 * Math.PI)) * Math.sqrt(values.get("k") / values.get("m"));
                if (targetVariable.equals("k")) return Math.pow(2 * Math.PI * values.get("f"), 2) * values.get("m");
                return values.get("k") / Math.pow(2 * Math.PI * values.get("f"), 2); // m

            case "Doppler Effect":
                // For simplicity, assuming moving toward each other (use + for vo and - for vs)
                double v_sound = values.get("v");  // speed of sound
                double vo = values.get("vo"); // observer speed
                double vs = values.get("vs"); // source speed
                double f = values.get("f");   // original frequency
               
                if (targetVariable.equals("f'")) {
                    return f * (v_sound + vo) / (v_sound - vs);
                }
                if (targetVariable.equals("f")) {
                    return values.get("f'") * (v_sound - vs) / (v_sound + vo);
                }
                if (targetVariable.equals("vo")) {
                    return (values.get("f'") * (v_sound - vs) / f) - v_sound;
                }
                return v_sound - (values.get("f'") * (v_sound + vo) / f); // vs

            case "Sound Intensity":
                if (targetVariable.equals("I")) return values.get("P") / values.get("A");
                if (targetVariable.equals("P")) return values.get("I") * values.get("A");
                return values.get("P") / values.get("I"); // A

            case "Decibel Formula":
                double I0 = 1e-12; // Reference intensity (W/m²)
                if (targetVariable.equals("β")) return 10 * Math.log10(values.get("I") / I0);
                if (targetVariable.equals("I")) return I0 * Math.pow(10, values.get("β") / 10);
                return 0.0;

            // ========== OPTICS ==========
            case "Lens Formula":
                if (targetVariable.equals("f")) return 1.0 / ((1.0 / values.get("v")) - (1.0 / values.get("u")));
                if (targetVariable.equals("v")) return 1.0 / ((1.0 / values.get("f")) + (1.0 / values.get("u")));
                return 1.0 / ((1.0 / values.get("f")) - (1.0 / values.get("v"))); // u

            case "Mirror Formula":
                if (targetVariable.equals("f")) return 1.0 / ((1.0 / values.get("u")) + (1.0 / values.get("v")));
                if (targetVariable.equals("v")) return 1.0 / ((1.0 / values.get("f")) - (1.0 / values.get("u")));
                return 1.0 / ((1.0 / values.get("f")) - (1.0 / values.get("v"))); // u

            case "Magnification":
                if (targetVariable.equals("m")) return -values.get("v") / values.get("u");
                if (targetVariable.equals("h_prime")) return values.get("m") * values.get("h");
                if (targetVariable.equals("h")) return values.get("h_prime") / values.get("m");
                if (targetVariable.equals("v")) return -values.get("m") * values.get("u");
                return -values.get("v") / values.get("m"); // u

            case "Snell's Law":
                if (targetVariable.equals("n1")) return values.get("n2") * Math.sin(Math.toRadians(values.get("r"))) / Math.sin(Math.toRadians(values.get("i")));
                if (targetVariable.equals("n2")) return values.get("n1") * Math.sin(Math.toRadians(values.get("i"))) / Math.sin(Math.toRadians(values.get("r")));
                if (targetVariable.equals("i")) return Math.toDegrees(Math.asin(values.get("n2") * Math.sin(Math.toRadians(values.get("r"))) / values.get("n1")));
                return Math.toDegrees(Math.asin(values.get("n1") * Math.sin(Math.toRadians(values.get("i"))) / values.get("n2"))); // r

            case "Critical Angle":
                if (targetVariable.equals("C")) return Math.toDegrees(Math.asin(values.get("n2") / values.get("n1")));
                if (targetVariable.equals("n1")) return values.get("n2") / Math.sin(Math.toRadians(values.get("C")));
                return values.get("n1") * Math.sin(Math.toRadians(values.get("C"))); // n2

            case "Total Internal Reflection":
                // Check if total internal reflection occurs
                double n1 = values.get("n1");
                double n2 = values.get("n2");
                double i = values.get("i");
               
                if (n1 <= n2) return 0.0; // TIR only occurs when n1 > n2
               
                double criticalAngle = Math.toDegrees(Math.asin(n2 / n1));
               
                if (targetVariable.equals("occurs")) {
                    return (i > criticalAngle) ? 1.0 : 0.0;
                }
               
                // For angle of refraction during TIR (returns 90° when TIR occurs)
                if (targetVariable.equals("r")) {
                    if (i > criticalAngle) {
                        return 90.0; // Total internal reflection - no refraction
                    } else {
                        return Math.toDegrees(Math.asin(n1 * Math.sin(Math.toRadians(i)) / n2));
                    }
                }
               
                return 0.0;

            // ========== ELECTRICITY AND MAGNETISM ==========
            case "Electric Potential":
                if (targetVariable.equals("V")) return k * values.get("Q") / values.get("r");
                if (targetVariable.equals("Q")) return values.get("V") * values.get("r") / k;
                return k * values.get("Q") / values.get("V"); // r

            case "Electric Field":
                if (targetVariable.equals("E")) {
                    if (values.containsKey("F") && values.containsKey("q")) {
                        return values.get("F") / values.get("q");
                    } else {
                        return k * values.get("Q") / Math.pow(values.get("r"), 2);
                    }
                }
                if (targetVariable.equals("F")) return values.get("E") * values.get("q");
                if (targetVariable.equals("q")) return values.get("F") / values.get("E");
                if (targetVariable.equals("Q")) return values.get("E") * Math.pow(values.get("r"), 2) / k;
                return Math.sqrt(k * values.get("Q") / values.get("E")); // r

            case "Coulomb's Law":
                if (targetVariable.equals("F")) return k * values.get("q1") * values.get("q2") / Math.pow(values.get("r"), 2);
                if (targetVariable.equals("q1")) return values.get("F") * Math.pow(values.get("r"), 2) / (k * values.get("q2"));
                if (targetVariable.equals("q2")) return values.get("F") * Math.pow(values.get("r"), 2) / (k * values.get("q1"));
                return Math.sqrt(k * values.get("q1") * values.get("q2") / values.get("F")); // r

            case "Ohm's Law":
                if (targetVariable.equals("V")) return values.get("I") * values.get("R");
                if (targetVariable.equals("I")) return values.get("V") / values.get("R");
                return values.get("V") / values.get("I"); // R

            case "Electric Power":
                if (targetVariable.equals("P")) {
                    if (values.containsKey("V") && values.containsKey("I")) {
                        return values.get("V") * values.get("I");
                    } else if (values.containsKey("I") && values.containsKey("R")) {
                        return Math.pow(values.get("I"), 2) * values.get("R");
                    } else if (values.containsKey("V") && values.containsKey("R")) {
                        return Math.pow(values.get("V"), 2) / values.get("R");
                    }
                }
                if (targetVariable.equals("V")) {
                    if (values.containsKey("I") && values.containsKey("R")) {
                        return values.get("I") * values.get("R");
                    } else {
                        return Math.sqrt(values.get("P") * values.get("R"));
                    }
                }
                if (targetVariable.equals("I")) {
                    if (values.containsKey("V") && values.containsKey("R")) {
                        return values.get("V") / values.get("R");
                    } else {
                        return Math.sqrt(values.get("P") / values.get("R"));
                    }
                }
                if (targetVariable.equals("R")) {
                    if (values.containsKey("V") && values.containsKey("I")) {
                        return values.get("V") / values.get("I");
                    } else if (values.containsKey("P") && values.containsKey("I")) {
                        return values.get("P") / Math.pow(values.get("I"), 2);
                    } else {
                        return Math.pow(values.get("V"), 2) / values.get("P");
                    }
                }
                return 0.0;

            case "Series Resistance":
                // For simplicity, assuming R1, R2, R3 as inputs
                double seriesSum = 0;
                for (String key : values.keySet()) {
                    if (key.startsWith("R")) {
                        seriesSum += values.get(key);
                    }
                }
                return seriesSum; // Rs

            case "Parallel Resistance":
                // For simplicity, assuming R1, R2, R3 as inputs
                double parallelSum = 0;
                for (String key : values.keySet()) {
                    if (key.startsWith("R")) {
                        parallelSum += 1.0 / values.get(key);
                    }
                }
                return 1.0 / parallelSum; // Rp

            case "Magnetic Field (Due to a Straight Wire)":
                double mu0 = 4 * Math.PI * 1e-7; // μ0 constant
                if (targetVariable.equals("B")) return (mu0 * values.get("I")) / (2 * Math.PI * values.get("r"));
                if (targetVariable.equals("I")) return values.get("B") * 2 * Math.PI * values.get("r") / mu0;
                return (mu0 * values.get("I")) / (2 * Math.PI * values.get("B")); // r

            case "Charge":
                if (targetVariable.equals("Q")) return values.get("I") * values.get("t");
                if (targetVariable.equals("I")) return values.get("Q") / values.get("t");
                return values.get("Q") / values.get("I"); // t

            case "Inductance":
                // Simplified version - assumes dI/dt is provided
                if (targetVariable.equals("V")) return values.get("L") * values.get("dI_dt");
                if (targetVariable.equals("L")) return values.get("V") / values.get("dI_dt");
                return values.get("V") / values.get("L"); // dI_dt

            case "Conductivity":
                if (targetVariable.equals("σ")) return 1.0 / values.get("ρ");
                return 1.0 / values.get("σ"); // ρ

            case "Faraday's Law of Electromagnetic Induction":
                // Simplified version - assumes dΦ/dt is provided
                if (targetVariable.equals("ε")) return -values.get("dΦ_dt");
                return -values.get("ε"); // dΦ_dt
               
            // ========== MODERN PHYSICS ==========
            case "Mass-Energy Equivalence":
                if (targetVariable.equals("E")) return values.get("m") * Math.pow(c, 2);
                return values.get("E") / Math.pow(c, 2); // m

            case "Photon Energy":
                if (targetVariable.equals("E")) return h * values.get("f");
                if (targetVariable.equals("f")) return values.get("E") / h;
                return values.get("E") / values.get("f"); // h

            case "de Broglie Wavelength":
                if (targetVariable.equals("λ")) return h / values.get("p");
                if (targetVariable.equals("p")) return h / values.get("λ");
                return h / values.get("p"); // h (though usually we know h)

            case "Photoelectric Effect (Einstein's Equation)":
                if (targetVariable.equals("E_k")) return h * values.get("f") - values.get("φ");
                if (targetVariable.equals("f")) return (values.get("E_k") + values.get("φ")) / h;
                if (targetVariable.equals("φ")) return h * values.get("f") - values.get("E_k");
                return (values.get("E_k") + values.get("φ")) / values.get("f"); // h

            case "Nuclear Fission":
                // Simplified - energy released based on mass defect
                if (targetVariable.equals("E")) return (values.get("m_initial") - values.get("m_final")) * Math.pow(c, 2);
                if (targetVariable.equals("m_initial")) return values.get("E") / Math.pow(c, 2) + values.get("m_final");
                return values.get("m_initial") - values.get("E") / Math.pow(c, 2); // m_final

            case "Nuclear Fusion":
                // Simplified - energy released based on mass defect
                if (targetVariable.equals("E")) return (values.get("m_initial") - values.get("m_final")) * Math.pow(c, 2);
                if (targetVariable.equals("m_initial")) return values.get("E") / Math.pow(c, 2) + values.get("m_final");
                return values.get("m_initial") - values.get("E") / Math.pow(c, 2); // m_final

            case "Half-Life Formula":
                if (targetVariable.equals("T1/2")) return 0.693 / values.get("λ");
                if (targetVariable.equals("λ")) return 0.693 / values.get("T1/2");
                return 0.693 / values.get("λ"); // Default return

            case "Radioactive Decay Law":
                if (targetVariable.equals("N")) return values.get("N0") * Math.exp(-values.get("λ") * values.get("t"));
                if (targetVariable.equals("N0")) return values.get("N") / Math.exp(-values.get("λ") * values.get("t"));
                if (targetVariable.equals("λ")) return -Math.log(values.get("N") / values.get("N0")) / values.get("t");
                return -Math.log(values.get("N") / values.get("N0")) / values.get("λ"); // t

            case "Electromagnetic Wave":
                if (targetVariable.equals("c")) return values.get("λ") * values.get("f");
                if (targetVariable.equals("λ")) return values.get("c") / values.get("f");
                return values.get("c") / values.get("λ"); // f

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
        } else if (isFrequencyVariable(variable)) {
            return new String[]{"Hz", "kHz", "MHz", "GHz"};
        } else if (isSpringConstantVariable(variable)) {
            return new String[]{"N/m", "N/cm", "N/mm"};
        } else if (isRefractiveIndexVariable(variable)) {
            return new String[]{"—"};
        } else if (isHeatVariable(variable)) {
            return new String[]{"J", "kJ", "cal", "kcal", "eV", "BTU"};
        } else if (isVolumeVariable(variable)) {
            return new String[]{"m³", "L", "mL", "cm³", "ft³", "gal"};
        } else if (isAmountOfSubstanceVariable(variable)) {
            return new String[]{"mol", "mmol", "kmol"};
        } else if (isEntropyVariable(variable)) {
            return new String[]{"J/K"};
        } else if (isEfficiencyVariable(variable)) {
            return new String[]{"—"};
        }
       
        return new String[]{getUnitForVariable(variable)};
    }

    // Helper methods for variable type detection
    private boolean isLengthVariable(String variable) {
        return variable.equals("d") || variable.equals("r") || variable.equals("h") ||
               variable.equals("s") || variable.equals("λ") || variable.equals("dy") ||
               variable.contains("x") || variable.contains("y") || variable.equals("A") ||
               variable.equals("L") || variable.equals("ΔL") || variable.equals("x");
    }
    private boolean isVolumeVariable(String variable) {
         return variable.equals("V") || variable.equals("V1") || variable.equals("V2") ||
                variable.startsWith("V") && variable.length() > 1;
    }

    private boolean isAmountOfSubstanceVariable(String variable) {
         return variable.equals("n") || variable.equals("n1") || variable.equals("n2");
    }

    private boolean isMassVariable(String variable) {
        return variable.equals("m") || variable.equals("m1") || variable.equals("m2") ||
               variable.equals("M") || variable.equals("m_initial") || variable.equals("m_final");
    }

    private boolean isTimeVariable(String variable) {
        return variable.equals("t") || variable.equals("T") || variable.equals("T1/2");
    }

    private boolean isForceVariable(String variable) {
    return variable.equals("F") || variable.equals("Fc") || variable.equals("N") ||
           variable.equals("f") || variable.equals("Tension") ||
           variable.equals("F1") || variable.equals("F2");
}

    private boolean isEnergyVariable(String variable) {
        return variable.equals("W") || variable.equals("KE") || variable.equals("PE") ||
               variable.equals("E") || variable.equals("E_k") || variable.equals("U") ||
               variable.equals("ΔU") || variable.equals("ΔQ");
    }

    private boolean isVelocityVariable(String variable) {
        return variable.equals("v") || variable.equals("u") || variable.equals("dv") ||
               variable.equals("vo") || variable.equals("vs");
    }

    private boolean isPowerVariable(String variable) {
        return variable.equals("P");
    }

    private boolean isPressureVariable(String variable) {
        return variable.equals("P") || variable.equals("P0") || variable.equals("P1") ||
               variable.equals("P2") || variable.equals("P3") || variable.equals("P_total");
    }

    private boolean isAngleVariable(String variable) {
        return variable.equals("θ") || variable.equals("i") || variable.equals("r") ||
               variable.equals("C") || variable.equals("α") || variable.equals("ω") ||
               variable.equals("ω₀");
    }

    private boolean isTemperatureVariable(String variable) {
        return variable.equals("T") || variable.equals("T1") || variable.equals("T2") ||
               variable.equals("Tc") || variable.equals("Th");
                //variable.startsWith("T") && variable.length() > 1;
    }

    private boolean isCurrentVariable(String variable) {
        return variable.equals("I");
    }

    private boolean isVoltageVariable(String variable) {
        return variable.equals("V") || variable.equals("ε");
    }

    private boolean isResistanceVariable(String variable) {
        return variable.equals("R") || variable.equals("R1") || variable.equals("R2") ||
               variable.equals("R3") || variable.equals("Rs") || variable.equals("Rp");
    }

    private boolean isChargeVariable(String variable) {
        return variable.equals("q1") || variable.equals("q2") || variable.equals("q") ||
               variable.equals("Q");
    }

    private boolean isFrequencyVariable(String variable) {
        return variable.equals("f") || variable.equals("f'");
    }

    private boolean isSpringConstantVariable(String variable) {
        return variable.equals("k");
    }

    private boolean isRefractiveIndexVariable(String variable) {
        return variable.equals("n1") || variable.equals("n2");
    }

    private boolean isHeatVariable(String variable) {
        return variable.equals("ΔQ") || variable.equals("Qc") || variable.equals("Qh") ||
               variable.equals("ΔU") || variable.equals("W");
    }

    private boolean isEntropyVariable(String variable) {
        return variable.equals("ΔS");
    }

    private boolean isEfficiencyVariable(String variable) {
        return variable.equals("η");
    }
    private boolean isConstantVariable(String variable) {
    return variable.equals("G") || variable.equals("k") || variable.equals("c") ||
           variable.equals("h") || variable.equals("g") || variable.equals("epsilon") ||
           variable.equals("K") || variable.equals("RHO") || variable.equals("R") ||
           variable.equals("I0") || variable.equals("mu0");
    }
    private String getConstantValue(String constant) {
    switch (constant) {
        case "G": return "6.67430e-11";
        case "k": return "9e9";
        case "c": return "3e8";
        case "h": return "6.62607015e-34";
        case "g": return "9.8";
        case "epsilon": return "8.854e-12";
        case "K": return "1.381e-23";
        case "RHO": return "1.1e3";
        case "R": return "8.314";
        case "I0": return "1e-12";
        case "mu0": return String.valueOf(4 * Math.PI * 1e-7);
        case "constant": return "0"; // User will input this
        default: return "0";
    }
}
    private String getUnitForConstant(String constant) {
    switch (constant) {
        case "G": return "m³/kg·s²";
        case "k": return "N·m²/C²";
        case "c": return "m/s";
        case "h": return "J·s";
        case "g": return "m/s²";
        case "epsilon": return "F/m";
        case "K": return "J/K";
        case "RHO": return "kg/m³";
        case "R": return "J/mol·K";
        case "I0": return "W/m²";
        case "mu0": return "N/A²";
        default: return "—";
    }
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
        } else if (isEnergyVariable(variable) || isHeatVariable(variable)) {
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
        } else if (isFrequencyVariable(variable)) {
            switch (unit) {
                case "kHz": return value * 1000;
                case "MHz": return value * 1e6;
                case "GHz": return value * 1e9;
                default: return value; // Hz
            }
        } else if (isSpringConstantVariable(variable)) {
            switch (unit) {
                case "N/cm": return value * 100;
                case "N/mm": return value * 1000;
                default: return value; // N/m
            }
        } else if (isVolumeVariable(variable)) {
              switch (unit) {
              case "L": return value / 1000;
              case "mL": return value / 1e6;
              case "cm³": return value / 1e6;
              case "ft³": return value * 0.0283168;
              case "gal": return value * 0.00378541;
              default: return value; // m³
            }
        } else if (isAmountOfSubstanceVariable(variable)) {
              switch (unit) {
              case "mmol": return value / 1000;
              case "kmol": return value * 1000;
              default: return value; // mol
            }
        }
        else if (isTemperatureVariable(variable)) {
            // Temperature conversions are handled separately in performCalculation
            return value;
        } else if (isEntropyVariable(variable)) {
            return value; // No conversion needed for basic units
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
        } else if (isEnergyVariable(variable) || isHeatVariable(variable)) {
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
        } else if (isFrequencyVariable(variable)) {
            switch (unit) {
                case "kHz": return value / 1000;
                case "MHz": return value / 1e6;
                case "GHz": return value / 1e9;
                default: return value; // Hz
            }
        }else if (isVolumeVariable(variable)) {
           switch (unit) {
                 case "L": return value * 1000;
                 case "mL": return value * 1e6;
                 case "cm³": return value * 1e6;
                 case "ft³": return value / 0.0283168;
                 case "gal": return value / 0.00378541;
                default: return value; // m³
           }
       } else if (isAmountOfSubstanceVariable(variable)) {
               switch (unit) {
               case "mmol": return value * 1000;
               case "kmol": return value / 1000;
               default: return value; // mol
    }
}
        else if (isSpringConstantVariable(variable)) {
            switch (unit) {
                case "N/cm": return value / 100;
                case "N/mm": return value / 1000;
                default: return value; // N/m
            }
        }
        return value; // No conversion needed
    }

    private String[] getAvailableUnitsForVariable(String variable) {
         if (isConstantVariable(variable)) {
        return new String[]{getUnitForConstant(variable)};
    }
        if (isLengthVariable(variable)) {
            return new String[]{"m", "cm", "mm", "km", "ft", "in", "mi"};
        } else if (isMassVariable(variable)) {
            return new String[]{"kg", "g", "mg", "lb", "oz"};
        } else if (isTimeVariable(variable)) {
            return new String[]{"s", "ms", "min", "h", "day"};
        } else if (isForceVariable(variable)) {
            return new String[]{"N", "dyne", "lbf"};
        } else if (isEnergyVariable(variable) || isHeatVariable(variable)) {
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
        } else if (isFrequencyVariable(variable)) {
            return new String[]{"Hz", "kHz", "MHz"};
        } else if (isSpringConstantVariable(variable)) {
            return new String[]{"N/m", "N/cm", "N/mm"};
        } else if (isRefractiveIndexVariable(variable)) {
            return new String[]{"—"};
        } else if (isHeatVariable(variable)) {
            return new String[]{"J", "kJ", "cal", "kcal", "eV", "BTU"};
        } else if (isEntropyVariable(variable)) {
            return new String[]{"J/K"};
        }else if (isVolumeVariable(variable)) {
            return new String[]{"m³", "L", "mL", "cm³"};
        } else if (isAmountOfSubstanceVariable(variable)) {
               return new String[]{"mol", "mmol"};
        }
         else if (isEfficiencyVariable(variable)) {
            return new String[]{"—"};
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
        formulaMap.put("Gravitational Potential Energy", "U = - (G × M × m) / r");
        formulaMap.put("Orbital Velocity", "v = √(G × M / r)");
        formulaMap.put("Kepler's Laws", "T² = (4π²/GM) × r³");
        formulaMap.put("Distance-Time Relation", "s = u × t + ½ × a × t²");
        formulaMap.put("Velocity-Time Relation", "v = u + a × t");
        formulaMap.put("Moment of Inertia", "I = m × r²");
        formulaMap.put("Pressure", "P = F / A");
        formulaMap.put("Density", "ρ = m / V");
        formulaMap.put("Bernoulli's Principle", "P + (1/2) × ρ × v² = - ρ × g × h ");
        formulaMap.put("Elasticity", "Y = (F / A) / (ΔL / L)");
        formulaMap.put("Pressure in Gases", "P = (1/3) × ρ × v²");
        formulaMap.put("Fluid Pressure (Hydrostatic Pressure)", "P = ρ × g × h");
        formulaMap.put("Atmospheric Pressure", "P = h × ρ × g");
        formulaMap.put("Pascal's Law", "F₁ / A₁ = F₂ / A₂");
        formulaMap.put("Pressure due to Depth in Fluid", "P = P₀ + ρ × g × h");
        formulaMap.put("Boyle's Law", "P₁ × V₁ = P₂ × V₂");
        formulaMap.put("Charles's Law", "V₁ / T₁ = V₂ / T₂");
        formulaMap.put("Avogadro's Law", "V₁ / n₁ = V₂ / n₂");
        formulaMap.put("Combined Gas Law", "(P₁ × V₁) / T₁ = (P₂ × V₂) / T₂");
        formulaMap.put("Ideal Gas Law", "P × V = n × R × T");
        formulaMap.put("Dalton's Law of Partial Pressure", "P_total = P₁ + P₂ + P₃ + ...");
        formulaMap.put("Temperature Conversion", "K = °C + 273.15 | °F = °C × 9/5 + 32");
        formulaMap.put("First Law of Thermodynamics", "ΔQ = ΔU + W");
        formulaMap.put("Heat Engine Efficiency", "η = 1 - (Qc / Qh)");
        formulaMap.put("Carnot Efficiency", "η = 1 - (Tc / Th)");
        formulaMap.put("Entropy", "ΔS = ΔQ / T");
        formulaMap.put("Wave Speed", "v = f × λ");
        formulaMap.put("Period", "T = 1 / f");
        formulaMap.put("Hooke's Law (Spring)", "F = -k × x");
        formulaMap.put("Energy in Simple Harmonic Oscillator", "E = (1/2) × k × A²");
        formulaMap.put("Resonance Frequency", "f = (1 / 2π) × √(k / m)");
        formulaMap.put("Doppler Effect", "f' = f × (v ± vo) / (v ∓ vs)");
        formulaMap.put("Sound Intensity", "I = P / A");
        formulaMap.put("Decibel Formula", "β = 10 × log₁₀(I / I₀)");
        formulaMap.put("Lens Formula", "1/f = 1/v - 1/u");
        formulaMap.put("Mirror Formula", "1/f = 1/u + 1/v");
        formulaMap.put("Magnification", "m = h'/h = -v/u");
        formulaMap.put("Snell's Law", "n1 * sin(i) = n2 * sin(r)");
        formulaMap.put("Critical Angle", "sin(C) = n2 / n1");
        formulaMap.put("Total Internal Reflection", "n1 * sin(i) = n2 * sin(r)");
        formulaMap.put("Ohm's Law", "V = I × R");
        formulaMap.put("Electric Potential", "V = k × Q / r");
        formulaMap.put("Electric Field", "E = F / q = k × Q / r²");
        formulaMap.put("Coulomb's Law", "F = k × (q₁ × q₂) / r²");
        formulaMap.put("Electric Power", "P = V × I = I² × R = V² / R");
        formulaMap.put("Series Resistance", "R_s = R₁ + R₂ + R₃ + ...");
        formulaMap.put("Parallel Resistance", "1/R_p = 1/R₁ + 1/R₂ + 1/R₃ + ...");
        formulaMap.put("Magnetic Field (Due to a Straight Wire)", "B = (μ₀ × I) / (2 × π × r)");
        formulaMap.put("Charge", "Q = I × t");
        formulaMap.put("Inductance", "V = L × (dI/dt)");
        formulaMap.put("Conductivity", "σ = 1 / ρ");
        formulaMap.put("Faraday's Law of Electromagnetic Induction", "ε = -dΦ/dt");
        formulaMap.put("Electric Power", "P = V × I");
       
        formulaMap.put("Mass-Energy Equivalence", "E = m × c²");
        formulaMap.put("Photon Energy", "E = h × f");
        formulaMap.put("de Broglie Wavelength", "λ = h / p");
        formulaMap.put("Photoelectric Effect (Einstein's Equation)", "E_k = h × f - φ");
        formulaMap.put("Nuclear Fission", "Heavy nucleus → lighter nuclei + energy (E = Δm × c²)");
        formulaMap.put("Nuclear Fusion", "Light nuclei → heavier nucleus + energy (E = Δm × c²)");
        formulaMap.put("Half-Life Formula", "T₁/₂ = 0.693 / λ");
        formulaMap.put("Radioactive Decay Law", "N = N₀ × e^(-λt)");
        formulaMap.put("Electromagnetic Wave", "c = λ × f");

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
        variables.put("Gravitational Potential Energy", new String[]{"U", "M", "m", "r"});
        variables.put("Orbital Velocity", new String[]{"v", "M", "r"});
        variables.put("Kepler's Laws", new String[]{"T", "r", "M"});
        variables.put("Distance-Time Relation", new String[]{"s", "u", "t", "a"});
        variables.put("Velocity-Time Relation", new String[]{"v", "u", "a", "t"});
        variables.put("Moment of Inertia", new String[]{"I", "m", "r"});
       
        // Fluid Mechanics
        variables.put("Pressure", new String[]{"P", "F", "A"});
        variables.put("Density", new String[]{"ρ", "m", "V"});
        variables.put("Bernoulli's Principle", new String[]{"P", "ρ", "v", "h"});
        variables.put("Elasticity", new String[]{"Y", "F", "A", "ΔL", "L"});
        variables.put("Pressure in Gases", new String[]{"P", "ρ", "v"});
        variables.put("Fluid Pressure (Hydrostatic Pressure)", new String[]{"P", "ρ", "h"});
        variables.put("Atmospheric Pressure", new String[]{"P", "h", "ρ"});
        variables.put("Pascal's Law", new String[]{"F1", "F2", "A1", "A2"});
        variables.put("Pressure due to Depth in Fluid", new String[]{"P", "P0", "ρ", "h"});
        variables.put("Boyle's Law", new String[]{"P1", "P2", "V1", "V2"});
        variables.put("Charles's Law", new String[]{"V1", "V2", "T1", "T2"});
        variables.put("Gay-Lussac's Law", new String[]{"P1", "P2", "T1", "T2"});
        variables.put("Avogadro's Law", new String[]{"V1", "V2", "n1", "n2"});
        variables.put("Combined Gas Law", new String[]{"P1", "V1", "T1", "P2", "V2", "T2"});
        variables.put("Ideal Gas Law", new String[]{"P", "V", "n", "T"});
        variables.put("Dalton's Law of Partial Pressure", new String[]{"P_total", "P1", "P2", "P3"});
       
        // Thermodynamics
        variables.put("Temperature Conversion", new String[]{"K", "°C", "°F"});
        variables.put("First Law of Thermodynamics", new String[]{"ΔQ", "ΔU", "W"});
        variables.put("Heat Engine Efficiency", new String[]{"η", "Qc", "Qh"});
        variables.put("Carnot Efficiency", new String[]{"η", "Tc", "Th"});
        variables.put("Entropy", new String[]{"ΔS", "ΔQ", "T"});
       
        // Waves and Oscillations
        variables.put("Wave Speed", new String[]{"v", "f", "λ"});
        variables.put("Period", new String[]{"T", "f"});
        variables.put("Hooke's Law (Spring)", new String[]{"F", "k", "x"});
        variables.put("Energy in Simple Harmonic Oscillator", new String[]{"E", "k", "A"});
        variables.put("Resonance Frequency", new String[]{"f", "k", "m"});
        variables.put("Doppler Effect", new String[]{"f'", "f", "v", "vo", "vs"});
        variables.put("Sound Intensity", new String[]{"I", "P", "A"});
        variables.put("Decibel Formula", new String[]{"β", "I"});
       
        // Optics
        variables.put("Lens Formula", new String[]{"f", "u", "v"});
        variables.put("Mirror Formula", new String[]{"f", "u", "v"});
        variables.put("Magnification", new String[]{"m", "h", "h_prime", "u", "v"});
        variables.put("Snell's Law", new String[]{"n1", "n2", "i", "r"});
        variables.put("Critical Angle", new String[]{"C", "n1", "n2"});
        variables.put("Total Internal Reflection", new String[]{"n1", "n2", "i", "r"});
       
        // Electricity and Magnetism
        variables.put("Electric Potential", new String[]{"V", "Q", "r"});
        variables.put("Electric Field", new String[]{"E", "F", "q", "Q", "r"});
        variables.put("Coulomb's Law", new String[]{"F", "q1", "q2", "r"});
        variables.put("Ohm's Law", new String[]{"V", "I", "R"});
        variables.put("Electric Power", new String[]{"P", "V", "I", "R"});
        variables.put("Series Resistance", new String[]{"Rs", "R1", "R2", "R3"});
        variables.put("Parallel Resistance", new String[]{"Rp", "R1", "R2", "R3"});
        variables.put("Magnetic Field (Due to a Straight Wire)", new String[]{"B", "I", "r"});
        variables.put("Charge", new String[]{"Q", "I", "t"});
        variables.put("Inductance", new String[]{"V", "L", "dI_dt"});
        variables.put("Conductivity", new String[]{"σ", "ρ"});
        variables.put("Faraday's Law of Electromagnetic Induction", new String[]{"ε", "dΦ_dt"});
       
        // Modern Physics
        variables.put("Mass-Energy Equivalence", new String[]{"E", "m"});
        variables.put("Photon Energy", new String[]{"E", "f", "h"});
        variables.put("de Broglie Wavelength", new String[]{"λ", "p", "h"});
        variables.put("Photoelectric Effect (Einstein's Equation)", new String[]{"E_k", "f", "φ", "h"});
        variables.put("Nuclear Fission", new String[]{"E", "m_initial", "m_final"});
        variables.put("Nuclear Fusion", new String[]{"E", "m_initial", "m_final"});
        variables.put("Half-Life Formula", new String[]{"T1/2", "λ"});
        variables.put("Radioactive Decay Law", new String[]{"N", "N0", "λ", "t"});
        variables.put("Electromagnetic Wave", new String[]{"c", "λ", "f"});
       
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
        units.put("θ", "°");
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
        units.put("U", "J");        
        units.put("M", "kg");      
        units.put("m", "kg");        
        units.put("r", "m");        
        units.put("v", "m/s");      
        units.put("T", "s");        
        units.put("s", "m");        
        units.put("u", "m/s");      
        units.put("a", "m/s²");    
        units.put("I", "kg·m²");    
        units.put("P", "Pa");          
        units.put("F", "N");            
        units.put("A", "m²");          
        units.put("ρ", "kg/m³");        
        units.put("V", "m³");          
        units.put("v", "m/s");          
        units.put("h", "m");            
        units.put("g", "m/s²");        
        units.put("Y", "Pa");          
        units.put("ΔL", "m");          
        units.put("L", "m");            
        units.put("P0", "Pa");          
        units.put("T", "K");            
        units.put("n", "mol");          
        units.put("R", "J/(mol·K)");    
        units.put("P_total", "Pa");    
        units.put("P1", "Pa");          
        units.put("P2", "Pa");          
        units.put("P3", "Pa");            
        units.put("k", "N/m");          
        units.put("x", "m");            
        units.put("A", "m");            
        units.put("f", "Hz");          
        units.put("f'", "Hz");          
        units.put("vo", "m/s");        
        units.put("vs", "m/s");        
        units.put("β", "dB");          
        units.put("n1", "—");          
        units.put("n2", "—");          
        units.put("i", "°");            
        units.put("r", "°");            
        units.put("C", "°");            
        units.put("ΔQ", "J");          
        units.put("ΔU", "J");          
        units.put("W", "J");            
        units.put("η", "—");            
        units.put("Qc", "J");        
        units.put("Qh", "J");          
        units.put("Tc", "K");          
        units.put("Th", "K");          
        units.put("ΔS", "J/K");
        units.put("V", "m³");
        units.put("V1", "m³");
        units.put("V2", "m³");
        units.put("n", "mol");
        units.put("n1", "mol");
        units.put("n2", "mol");
        units.put("F1", "N");
        units.put("F2", "N");
        units.put("T1", "K");
        units.put("T2", "K");
        units.put("G", "m³/kg·s²");
        units.put("k", "N·m²/C²");
        units.put("c", "m/s");
        units.put("h", "J·s");
        units.put("g", "m/s²");
        units.put("epsilon", "F/m");
        units.put("K", "J/K");
        units.put("RHO", "kg/m³");
        units.put("R", "J/mol·K");
        units.put("I0", "W/m²");
        units.put("mu0", "N/A²");        
       
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
               
            case "Kepler's Laws":
                if (calculatedVariable.equals("T")) {
                    sb.append(String.format("T = 2π × √(%.2f³ / (6.67430×10⁻¹¹ × %.2f)) = %.6f s",
                        inputValues.get("r"), inputValues.get("M"), result));
                } else if (calculatedVariable.equals("r")) {
                    sb.append(String.format("r = ∛((%.2f² × 6.67430×10⁻¹¹ × %.2f) / (4π²)) = %.6f m",
                        inputValues.get("T"), inputValues.get("M"), result));
                }
                break;
               
            case "Snell's Law":
                if (calculatedVariable.equals("n1")) {
                    sb.append(String.format("n₁ = %.2f × sin(%.2f°) / sin(%.2f°) = %.6f",
                        inputValues.get("n2"), inputValues.get("r"), inputValues.get("i"), result));
                } else if (calculatedVariable.equals("n2")) {
                    sb.append(String.format("n₂ = %.2f × sin(%.2f°) / sin(%.2f°) = %.6f",
                        inputValues.get("n1"), inputValues.get("i"), inputValues.get("r"), result));
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
