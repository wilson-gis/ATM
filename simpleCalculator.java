import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class ScientificCalculator extends JFrame implements ActionListener {
    private JTextField display;
    private JTextArea historyArea;
    private double currentNumber = 0;
    private String currentOperation = "";
    private boolean startNewNumber = true;
    private boolean degreeMode = true; // true for degrees, false for radians
    private boolean secondFunction = false;
    private Stack<Double> memoryStack = new Stack<>();
    
    // Button labels matching the image layout
    private final String[][] buttonLabels = {
        {"2nd", "deg", "sin", "cos", "tan"},
        {"x^y", "lg", "ln", "(", ")"},
        {"√x", "AC", "⬆", "%", "÷"},
        {"x!", "7", "8", "9", "×"},
        {"1/x", "4", "5", "6", "−"},
        {"π", "1", "2", "3", "+"},
        {"e", "0", ".", "="}
    };
    
    private JButton[][] buttons = new JButton[7][5];
    
    public ScientificCalculator() {
        createGUI();
        setTitle("Scientific Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void createGUI() {
        setLayout(new BorderLayout());
        
        // Display area
        JPanel displayPanel = new JPanel(new BorderLayout());
        display = new JTextField("0");
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        displayPanel.add(display, BorderLayout.NORTH);
        
        // History area
        historyArea = new JTextArea(3, 20);
        historyArea.setFont(new Font("Arial", Font.PLAIN, 12));
        historyArea.setEditable(false);
        JScrollPane historyScroll = new JScrollPane(historyArea);
        displayPanel.add(historyScroll, BorderLayout.CENTER);
        
        add(displayPanel, BorderLayout.NORTH);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(7, 5, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create buttons
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 5; j++) {
                buttons[i][j] = new JButton(buttonLabels[i][j]);
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 16));
                buttons[i][j].addActionListener(this);
                
                // Color coding for different button types
                if (Character.isDigit(buttonLabels[i][j].charAt(0)) {
                    buttons[i][j].setBackground(Color.WHITE);
                } else if (buttonLabels[i][j].equals("=")) {
                    buttons[i][j].setBackground(new Color(255, 200, 100));
                } else if (buttonLabels[i][j].equals("AC")) {
                    buttons[i][j].setBackground(new Color(255, 150, 150));
                } else {
                    buttons[i][j].setBackground(new Color(200, 220, 255));
                }
                
                buttonPanel.add(buttons[i][j]);
            }
        }
        
        add(buttonPanel, BorderLayout.CENTER);
        
        // Status bar
        JLabel statusLabel = new JLabel("DEG Mode");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        try {
            // Handle digit input
            if (Character.isDigit(command.charAt(0))) {
                if (startNewNumber) {
                    display.setText(command);
                    startNewNumber = false;
                } else {
                    display.setText(display.getText() + command);
                }
                return;
            }
            
            // Handle decimal point
            if (command.equals(".")) {
                if (startNewNumber) {
                    display.setText("0.");
                    startNewNumber = false;
                } else if (!display.getText().contains(".")) {
                    display.setText(display.getText() + ".");
                }
                return;
            }
            
            switch (command) {
                case "AC":
                    display.setText("0");
                    currentNumber = 0;
                    currentOperation = "";
                    startNewNumber = true;
                    break;
                    
                case "⬆": // Memory recall/stack operations
                    if (!memoryStack.isEmpty()) {
                        display.setText(String.valueOf(memoryStack.peek()));
                        startNewNumber = true;
                    }
                    break;
                    
                case "2nd":
                    secondFunction = !secondFunction;
                    updateSecondFunctionButtons();
                    break;
                    
                case "deg":
                    degreeMode = !degreeMode;
                    updateStatus();
                    break;
                    
                case "=":
                    calculateResult();
                    break;
                    
                case "π":
                    display.setText(String.valueOf(Math.PI));
                    startNewNumber = true;
                    break;
                    
                case "e":
                    display.setText(String.valueOf(Math.E));
                    startNewNumber = true;
                    break;
                    
                case "+":
                case "−":
                case "×":
                case "÷":
                case "x^y":
                    if (!currentOperation.isEmpty()) {
                        calculateResult();
                    }
                    currentNumber = Double.parseDouble(display.getText());
                    currentOperation = command;
                    startNewNumber = true;
                    break;
                    
                default:
                    handleScientificFunction(command);
                    break;
            }
            
        } catch (Exception ex) {
            display.setText("Error");
            startNewNumber = true;
        }
    }
    
    private void handleScientificFunction(String function) {
        double value = Double.parseDouble(display.getText());
        double result = 0;
        
        if (secondFunction) {
            // Handle second functions
            switch (function) {
                case "sin": // arcsin
                    result = Math.asin(degreeMode ? Math.toRadians(value) : value);
                    if (degreeMode) result = Math.toDegrees(result);
                    break;
                case "cos": // arccos
                    result = Math.acos(degreeMode ? Math.toRadians(value) : value);
                    if (degreeMode) result = Math.toDegrees(result);
                    break;
                case "tan": // arctan
                    result = Math.atan(degreeMode ? Math.toRadians(value) : value);
                    if (degreeMode) result = Math.toDegrees(result);
                    break;
                case "lg": // 10^x
                    result = Math.pow(10, value);
                    break;
                case "ln": // e^x
                    result = Math.exp(value);
                    break;
                case "√x": // x^2
                    result = value * value;
                    break;
                case "x!": // Not typically a second function, but we'll use it for memory store
                    memoryStack.push(value);
                    return;
                case "1/x": // Not typically a second function
                    result = -value;
                    break;
            }
        } else {
            // Handle primary functions
            switch (function) {
                case "sin":
                    result = Math.sin(degreeMode ? Math.toRadians(value) : value);
                    break;
                case "cos":
                    result = Math.cos(degreeMode ? Math.toRadians(value) : value);
                    break;
                case "tan":
                    result = Math.tan(degreeMode ? Math.toRadians(value) : value);
                    break;
                case "lg":
                    result = Math.log10(value);
                    break;
                case "ln":
                    result = Math.log(value);
                    break;
                case "√x":
                    result = Math.sqrt(value);
                    break;
                case "x!":
                    result = factorial((int) value);
                    break;
                case "1/x":
                    result = 1.0 / value;
                    break;
                case "%":
                    result = value / 100.0;
                    break;
                case "(":
                case ")":
                    // Parentheses handling would require a full expression parser
                    // For simplicity, we'll just add them to display
                    if (startNewNumber) {
                        display.setText(command);
                        startNewNumber = false;
                    } else {
                        display.setText(display.getText() + command);
                    }
                    return;
            }
        }
        
        // Format and display result
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            display.setText("Error");
        } else {
            display.setText(formatResult(result));
        }
        startNewNumber = true;
        
        // Add to history
        addToHistory(function + "(" + value + ") = " + display.getText());
    }
    
    private void calculateResult() {
        if (currentOperation.isEmpty()) return;
        
        double secondNumber = Double.parseDouble(display.getText());
        double result = 0;
        
        switch (currentOperation) {
            case "+":
                result = currentNumber + secondNumber;
                break;
            case "−":
                result = currentNumber - secondNumber;
                break;
            case "×":
                result = currentNumber * secondNumber;
                break;
            case "÷":
                if (secondNumber != 0) {
                    result = currentNumber / secondNumber;
                } else {
                    display.setText("Error");
                    currentOperation = "";
                    startNewNumber = true;
                    return;
                }
                break;
            case "x^y":
                result = Math.pow(currentNumber, secondNumber);
                break;
        }
        
        display.setText(formatResult(result));
        addToHistory(currentNumber + " " + currentOperation + " " + secondNumber + " = " + display.getText());
        
        currentOperation = "";
        startNewNumber = true;
    }
    
    private String formatResult(double result) {
        if (result == (long) result) {
            return String.format("%d", (long) result);
        } else {
            return String.format("%.10g", result).replaceAll("\\.?0+(?=$|e)", "");
        }
    }
    
    private double factorial(int n) {
        if (n < 0) return Double.NaN;
        if (n == 0) return 1;
        double result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }
    
    private void updateSecondFunctionButtons() {
        // Update button labels for second function mode
        buttons[0][2].setText(secondFunction ? "sin⁻¹" : "sin");
        buttons[0][3].setText(secondFunction ? "cos⁻¹" : "cos");
        buttons[0][4].setText(secondFunction ? "tan⁻¹" : "tan");
        buttons[1][0].setText(secondFunction ? "y√x" : "x^y");
        buttons[1][1].setText(secondFunction ? "10^x" : "lg");
        buttons[1][2].setText(secondFunction ? "e^x" : "ln");
        buttons[2][0].setText(secondFunction ? "x²" : "√x");
        buttons[3][0].setText(secondFunction ? "STO" : "x!");
        buttons[4][0].setText(secondFunction ? "±" : "1/x");
    }
    
    private void updateStatus() {
        // This would update a status label to show DEG/RAD mode
        // Implementation depends on your UI design
    }
    
    private void addToHistory(String entry) {
        historyArea.append(entry + "\n");
        historyArea.setCaretPosition(historyArea.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ScientificCalculator().setVisible(true);
        });
    }
}