package Ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class InputOutputPanel extends JPanel {

    private MainWindow mainWindow;
    private JComboBox<String> formulaSelector;
    private JComboBox<String> variableSelector;
    private JPanel inputPanel;
    private JLabel formulaLabel;
    private JLabel outputLabel;
    private JButton refreshButton;
    private Map<String, JTextField> inputFields = new HashMap<>();
    private List<Formula> formulas;

    public InputOutputPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(null);
        setBackground(Color.WHITE);

        initFormulas();

        formulaSelector = new JComboBox<>();
        for (Formula f : formulas) formulaSelector.addItem(f.name);
        formulaSelector.setBounds(50, 20, 200, 30);
        add(formulaSelector);

        formulaLabel = new JLabel("", SwingConstants.CENTER);
        formulaLabel.setBounds(300, 20, 500, 30);
        add(formulaLabel);

        variableSelector = new JComboBox<>();
        variableSelector.setBounds(50, 60, 200, 30);
        add(variableSelector);

        inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(10,2,10,10));
        inputPanel.setBounds(50, 110, 700, 300);
        add(inputPanel);

        outputLabel = new JLabel("<html>Output will appear here</html>");
        outputLabel.setBounds(50, 420, 700, 60);
        add(outputLabel);

        refreshButton = new JButton("Refresh");
        refreshButton.setBounds(50, 490, 100, 30);
        refreshButton.setVisible(false);
        refreshButton.addActionListener(e -> resetPanel());
        add(refreshButton);

        formulaSelector.addActionListener(e -> loadFormula());
        variableSelector.addActionListener(e -> prepareInputFields());

        loadFormula();
    }

    private void initFormulas() {
        formulas = new ArrayList<>();

        // ===== Mechanics Formulas =====
        formulas.add(new Formula("Velocity","V = d / t",
            new String[]{"V","d","t"},
            (inputs,target)->{
                double V=0,d=0,t=0; d=inputs.getOrDefault("d",0.0); t=inputs.getOrDefault("t",1.0);
                V=d/t;
                Map<String,Double> out=new HashMap<>();
                out.put("V",V); out.put("d",d); out.put("t",t);
                return out;
            }));

        formulas.add(new Formula("Acceleration","a = (v - u) / t",
            new String[]{"a","v","u","t"},
            (inputs,target)->{
                double a=0,v=0,u=0,t=0; v=inputs.getOrDefault("v",0.0); u=inputs.getOrDefault("u",0.0); t=inputs.getOrDefault("t",1.0);
                a=(v-u)/t;
                Map<String,Double> out=new HashMap<>();
                out.put("a",a); out.put("v",v); out.put("u",u); out.put("t",t);
                return out;
            }));

        formulas.add(new Formula("Time","t = (v - u)/a",
            new String[]{"t","v","u","a"},
            (inputs,target)->{
                double t=0,v=0,u=0,a=0; v=inputs.getOrDefault("v",0.0); u=inputs.getOrDefault("u",0.0); a=inputs.getOrDefault("a",1.0);
                t=(v-u)/a;
                Map<String,Double> out=new HashMap<>();
                out.put("t",t); out.put("v",v); out.put("u",u); out.put("a",a);
                return out;
            }));

        formulas.add(new Formula("Force","F = m * a",
            new String[]{"F","m","a"},
            (inputs,target)->{
                double F=0,m=0,a=0; m=inputs.getOrDefault("m",0.0); a=inputs.getOrDefault("a",0.0);
                F=m*a;
                Map<String,Double> out=new HashMap<>();
                out.put("F",F); out.put("m",m); out.put("a",a);
                return out;
            }));

        formulas.add(new Formula("Mass","m = F / a",
            new String[]{"m","F","a"},
            (inputs,target)->{
                double m=0,F=0,a=0; F=inputs.getOrDefault("F",0.0); a=inputs.getOrDefault("a",1.0);
                m=F/a;
                Map<String,Double> out=new HashMap<>();
                out.put("m",m); out.put("F",F); out.put("a",a);
                return out;
            }));

        formulas.add(new Formula("Work","W = F * d",
            new String[]{"W","F","d"},
            (inputs,target)->{
                double W=0,F=0,d=0; F=inputs.getOrDefault("F",0.0); d=inputs.getOrDefault("d",0.0);
                W=F*d;
                Map<String,Double> out=new HashMap<>();
                out.put("W",W); out.put("F",F); out.put("d",d);
                return out;
            }));

        formulas.add(new Formula("Power","P = W / t",
            new String[]{"P","W","t"},
            (inputs,target)->{
                double P=0,W=0,t=0; W=inputs.getOrDefault("W",0.0); t=inputs.getOrDefault("t",1.0);
                P=W/t;
                Map<String,Double> out=new HashMap<>();
                out.put("P",P); out.put("W",W); out.put("t",t);
                return out;
            }));

        formulas.add(new Formula("Momentum","p = m * v",
            new String[]{"p","m","v"},
            (inputs,target)->{
                double p=0,m=0,v=0; m=inputs.getOrDefault("m",0.0); v=inputs.getOrDefault("v",0.0);
                p=m*v;
                Map<String,Double> out=new HashMap<>();
                out.put("p",p); out.put("m",m); out.put("v",v);
                return out;
            }));

        formulas.add(new Formula("Impulse","J = F * t",
            new String[]{"J","F","t"},
            (inputs,target)->{
                double J=0,F=0,t=0; F=inputs.getOrDefault("F",0.0); t=inputs.getOrDefault("t",0.0);
                J=F*t;
                Map<String,Double> out=new HashMap<>();
                out.put("J",J); out.put("F",F); out.put("t",t);
                return out;
            }));

        formulas.add(new Formula("Kinetic Energy","KE = 0.5 * m * v^2",
            new String[]{"KE","m","v"},
            (inputs,target)->{
                double KE=0,m=0,v=0; m=inputs.getOrDefault("m",0.0); v=inputs.getOrDefault("v",0.0);
                KE=0.5*m*v*v;
                Map<String,Double> out=new HashMap<>();
                out.put("KE",KE); out.put("m",m); out.put("v",v);
                return out;
            }));

        formulas.add(new Formula("Potential Energy","PE = m * g * h",
            new String[]{"PE","m","h"},
            (inputs,target)->{
                double PE=0,m=0,h=0,g=9.81; m=inputs.getOrDefault("m",0.0); h=inputs.getOrDefault("h",0.0);
                PE=m*g*h;
                Map<String,Double> out=new HashMap<>();
                out.put("PE",PE); out.put("m",m); out.put("h",h);
                return out;
            }));

        formulas.add(new Formula("Mechanical Energy","E = KE + PE",
            new String[]{"E","KE","PE"},
            (inputs,target)->{
                double E=0,KE=0,PE=0; KE=inputs.getOrDefault("KE",0.0); PE=inputs.getOrDefault("PE",0.0);
                E=KE+PE;
                Map<String,Double> out=new HashMap<>();
                out.put("E",E); out.put("KE",KE); out.put("PE",PE);
                return out;
            }));

        formulas.add(new Formula("Centripetal Force","Fc = m * v^2 / r",
            new String[]{"Fc","m","v","r"},
            (inputs,target)->{
                double Fc=0,m=0,v=0,r=0; m=inputs.getOrDefault("m",0.0); v=inputs.getOrDefault("v",0.0); r=inputs.getOrDefault("r",1.0);
                Fc=m*v*v/r;
                Map<String,Double> out=new HashMap<>();
                out.put("Fc",Fc); out.put("m",m); out.put("v",v); out.put("r",r);
                return out;
            }));

        formulas.add(new Formula("Centripetal Acceleration","ac = v^2 / r",
            new String[]{"ac","v","r"},
            (inputs,target)->{
                double ac=0,v=0,r=1; v=inputs.getOrDefault("v",0.0); r=inputs.getOrDefault("r",1.0);
                ac=v*v/r;
                Map<String,Double> out=new HashMap<>();
                out.put("ac",ac); out.put("v",v); out.put("r",r);
                return out;
            }));

        formulas.add(new Formula("Torque","τ = r * F * sin(θ)",
            new String[]{"τ","r","F","θ"},
            (inputs,target)->{
                double τ=0,r=0,F=0,θ=0; r=inputs.getOrDefault("r",0.0); F=inputs.getOrDefault("F",0.0); θ=inputs.getOrDefault("θ",0.0);
                τ=r*F*Math.sin(Math.toRadians(θ));
                Map<String,Double> out=new HashMap<>();
                out.put("τ",τ); out.put("r",r); out.put("F",F); out.put("θ",θ);
                return out;
            }));

        formulas.add(new Formula("Angular Momentum","L = I * ω",
            new String[]{"L","I","ω"},
            (inputs,target)->{
                double L=0,I=0,ω=0; I=inputs.getOrDefault("I",0.0); ω=inputs.getOrDefault("ω",0.0);
                L=I*ω;
                Map<String,Double> out=new HashMap<>();
                out.put("L",L); out.put("I",I); out.put("ω",ω);
                return out;
            }));

        formulas.add(new Formula("Angular Velocity","ω = θ / t",
            new String[]{"ω","θ","t"},
            (inputs,target)->{
                double ω=0,θ=0,t=1.0; θ=inputs.getOrDefault("θ",0.0); t=inputs.getOrDefault("t",1.0);
                ω=θ/t;
                Map<String,Double> out=new HashMap<>();
                out.put("ω",ω); out.put("θ",θ); out.put("t",t);
                return out;
            }));

        formulas.add(new Formula("Angular Acceleration","α = (ω - ω0)/t",
            new String[]{"α","ω","ω0","t"},
            (inputs,target)->{
                double α=0,ω=0,ω0=0,t=1.0; ω=inputs.getOrDefault("ω",0.0); ω0=inputs.getOrDefault("ω0",0.0); t=inputs.getOrDefault("t",1.0);
                α=(ω-ω0)/t;
                Map<String,Double> out=new HashMap<>();
                out.put("α",α); out.put("ω",ω); out.put("ω0",ω0); out.put("t",t);
                return out;
            }));

        formulas.add(new Formula("Rotational Kinetic Energy","KE(rot) = 0.5 * I * ω^2",
            new String[]{"KE(rot)","I","ω"},
            (inputs,target)->{
                double KE=0,I=0,ω=0; I=inputs.getOrDefault("I",0.0); ω=inputs.getOrDefault("ω",0.0);
                KE=0.5*I*ω*ω;
                Map<String,Double> out=new HashMap<>();
                out.put("KE(rot)",KE); out.put("I",I); out.put("ω",ω);
                return out;
            }));

        formulas.add(new Formula("First Equation of Motion","v = u + a * t",
            new String[]{"v","u","a","t"},
            (inputs,target)->{
                double v=0,u=0,a=0,t=1.0; u=inputs.getOrDefault("u",0.0); a=inputs.getOrDefault("a",0.0); t=inputs.getOrDefault("t",1.0);
                v=u+a*t;
                Map<String,Double> out=new HashMap<>();
                out.put("v",v); out.put("u",u); out.put("a",a); out.put("t",t);
                return out;
            }));

        formulas.add(new Formula("Second Equation of Motion","s = u*t + 0.5*a*t^2",
            new String[]{"s","u","a","t"},
            (inputs,target)->{
                double s=0,u=0,a=0,t=0; u=inputs.getOrDefault("u",0.0); a=inputs.getOrDefault("a",0.0); t=inputs.getOrDefault("t",0.0);
                s=u*t+0.5*a*t*t;
                Map<String,Double> out=new HashMap<>();
                out.put("s",s); out.put("u",u); out.put("a",a); out.put("t",t);
                return out;
            }));

        formulas.add(new Formula("Third Equation of Motion","v^2 = u^2 + 2*a*s",
            new String[]{"v","u","a","s"},
            (inputs,target)->{
                double v=0,u=0,a=0,s=0; u=inputs.getOrDefault("u",0.0); a=inputs.getOrDefault("a",0.0); s=inputs.getOrDefault("s",0.0);
                v=Math.sqrt(u*u+2*a*s);
                Map<String,Double> out=new HashMap<>();
                out.put("v",v); out.put("u",u); out.put("a",a); out.put("s",s);
                return out;
            }));

        formulas.add(new Formula("Maximum Height","H = (u^2 * sin^2θ) / (2*g)",
            new String[]{"H","u","θ"},
            (inputs,target)->{
                double H=0,u=0,θ=0,g=9.81; u=inputs.getOrDefault("u",0.0); θ=inputs.getOrDefault("θ",0.0);
                H=u*u*Math.pow(Math.sin(Math.toRadians(θ)),2)/(2*g);
                Map<String,Double> out=new HashMap<>();
                out.put("H",H); out.put("u",u); out.put("θ",θ);
                return out;
            }));

        formulas.add(new Formula("Time of Flight","T = (2*u*sinθ)/g",
            new String[]{"T","u","θ"},
            (inputs,target)->{
                double T=0,u=0,θ=0,g=9.81; u=inputs.getOrDefault("u",0.0); θ=inputs.getOrDefault("θ",0.0);
                T=(2*u*Math.sin(Math.toRadians(θ)))/g;
                Map<String,Double> out=new HashMap<>();
                out.put("T",T); out.put("u",u); out.put("θ",θ);
                return out;
            }));

        formulas.add(new Formula("Tension","T = m*(g ± a)",
            new String[]{"T","m","a"},
            (inputs,target)->{
                double T=0,m=0,a=0,g=9.81; m=inputs.getOrDefault("m",0.0); a=inputs.getOrDefault("a",0.0);
                T=m*(g+a);
                Map<String,Double> out=new HashMap<>();
                out.put("T",T); out.put("m",m); out.put("a",a);
                return out;
            }));

        formulas.add(new Formula("Friction","f = μ * N",
            new String[]{"f","μ","N"},
            (inputs,target)->{
                double f=0,μ=0,N=0; μ=inputs.getOrDefault("μ",0.0); N=inputs.getOrDefault("N",0.0);
                f=μ*N;
                Map<String,Double> out=new HashMap<>();
                out.put("f",f); out.put("μ",μ); out.put("N",N);
                return out;
            }));

        formulas.add(new Formula("Viscosity","F = η*A*(dv/dy)",
            new String[]{"F","η","A","dv/dy"},
            (inputs,target)->{
                double F=0,η=0,A=0,dvdy=0; η=inputs.getOrDefault("η",0.0); A=inputs.getOrDefault("A",0.0); dvdy=inputs.getOrDefault("dv/dy",0.0);
                F=η*A*dvdy;
                Map<String,Double> out=new HashMap<>();
                out.put("F",F); out.put("η",η); out.put("A",A); out.put("dv/dy",dvdy);
                return out;
            }));

        formulas.add(new Formula("Collision","m1*u1 + m2*u2 = m1*v1 + m2*v2",
            new String[]{"v1","v2","m1","m2","u1","u2"},
            (inputs,target)->{
                double m1=inputs.getOrDefault("m1",0.0);
                double m2=inputs.getOrDefault("m2",0.0);
                double u1=inputs.getOrDefault("u1",0.0);
                double u2=inputs.getOrDefault("u2",0.0);
                double v1=(m1*u1+m2*u2)/m1; // simplified elastic
                double v2=(m1*u1+m2*u2)/m2;
                Map<String,Double> out=new HashMap<>();
                out.put("v1",v1); out.put("v2",v2); out.put("m1",m1); out.put("m2",m2); out.put("u1",u1); out.put("u2",u2);
                return out;
            }));
    }

    private void loadFormula() {
        Formula f = formulas.get(formulaSelector.getSelectedIndex());
        formulaLabel.setText(f.formula);
        variableSelector.removeAllItems();
        for(String var:f.variables) variableSelector.addItem(var);
        prepareInputFields();
    }

    private void prepareInputFields() {
        inputPanel.removeAll();
        inputFields.clear();
        Formula f = formulas.get(formulaSelector.getSelectedIndex());
        String targetVar = (String) variableSelector.getSelectedItem();

        for(String var:f.variables){
            if(!var.equals(targetVar)){
                JLabel lbl = new JLabel(var + ":");
                JTextField tf = new JTextField();
                inputFields.put(var, tf);
                inputPanel.add(lbl);
                inputPanel.add(tf);
            }
        }

        JButton calcBtn = new JButton("Calculate");
        calcBtn.addActionListener(e->calculateFormula());
        inputPanel.add(calcBtn);

        inputPanel.revalidate();
        inputPanel.repaint();
    }

    private void calculateFormula() {
        Formula f = formulas.get(formulaSelector.getSelectedIndex());
        String targetVar = (String) variableSelector.getSelectedItem();
        Map<String,Double> inputs = new HashMap<>();

        try{
            for(String var: inputFields.keySet()){
                inputs.put(var,Double.parseDouble(inputFields.get(var).getText()));
            }

            Map<String,Double> result = f.calculator.calculate(inputs,targetVar);

            StringBuilder sb = new StringBuilder("<html>");
            for(String var:result.keySet()){
                sb.append(var).append(" = ").append(String.format("%.4f", result.get(var))).append("<br>");
            }
            sb.append("</html>");
            outputLabel.setText(sb.toString());
            refreshButton.setVisible(true);

        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Please enter valid numbers!");
        }
    }

    private void resetPanel(){
        inputFields.clear();
        outputLabel.setText("<html>Output will appear here</html>");
        refreshButton.setVisible(false);
        prepareInputFields();
    }

    // ===== Formula Class =====
    class Formula {
        String name;
        String formula;
        String[] variables;
        Calculator calculator;

        public Formula(String name,String formula,String[] variables,Calculator calculator){
            this.name=name; this.formula=formula; this.variables=variables; this.calculator=calculator;
        }
    }

    interface Calculator {
        Map<String,Double> calculate(Map<String,Double> inputs,String targetVar);
    }
}
