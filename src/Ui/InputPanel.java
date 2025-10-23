package Ui;

import java.awt.*;
import javax.swing.*;

public class InputPanel extends JPanel {

    private final MainWindow mainWindow;
    private static String selectedFormula;
    private JLabel formulaLabel, resultLabel;
    private JTextField input1, input2;

    public InputPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);

        formulaLabel = new JLabel("", SwingConstants.CENTER);
        formulaLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(formulaLabel, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(3, 2, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(40, 150, 40, 150));

        center.add(new JLabel("Input 1:"));
        input1 = new JTextField();
        center.add(input1);

        center.add(new JLabel("Input 2:"));
        input2 = new JTextField();
        center.add(input2);

        JButton calcBtn = new JButton("Calculate");
        resultLabel = new JLabel("Result: ", SwingConstants.CENTER);
        center.add(calcBtn);
        center.add(resultLabel);

        add(center, BorderLayout.CENTER);

        calcBtn.addActionListener(e -> {
            try {
                double a = Double.parseDouble(input1.getText());
                double b = Double.parseDouble(input2.getText());
                double result = a + b; // simple example
                resultLabel.setText("Result: " + result);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers!");
            }
        });

        JButton back = new JButton("Back to Categories");
        back.addActionListener(e -> mainWindow.showPanel("Category"));
        add(back, BorderLayout.SOUTH);
    }

    public static void setSelectedFormula(String formula) {
        selectedFormula = formula;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            formulaLabel.setText("Formula: " + selectedFormula);
        }
    }
}
