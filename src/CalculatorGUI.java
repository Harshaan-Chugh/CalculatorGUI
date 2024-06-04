import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class CalculatorGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalculatorGUI::new);
    }

    private final JFrame frame;
    private final JTextField inputField;
    private final JTextArea outputArea;
    private final JScrollPane scrollPane;
    private Calculator calculator;

    public CalculatorGUI() {
        frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 600);

        inputField = new JTextField();
        inputField.setFont(new Font("Times", Font.PLAIN, 24));
        inputField.setHorizontalAlignment(JTextField.RIGHT);
        inputField.addKeyListener(new EnterKeyListener());

        outputArea = new JTextArea();
        outputArea.setFont(new Font("Times", Font.PLAIN, 18));
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        outputArea.setText("Welcome to the calculator!\nResults are computed as doubles, but inputted numbers must be integers.\nDisplayed keys and keyboard input are supported.\n\n");

        scrollPane = new JScrollPane(outputArea);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(7, 4, 10, 10));

        String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", "(", ")", "+",
                "C", "CE", "=", "^",
                "sqrt", "log", "cos", "tan",
                "sin", "!", "Graph"
        };

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.PLAIN, 24));
            button.addActionListener(new ButtonClickListener());
            button.setForeground(Color.WHITE);
            button.setBackground(Color.DARK_GRAY);
            button.setFocusPainted(false);
            buttonPanel.add(button);
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputField, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set dark mode colors
        mainPanel.setBackground(Color.BLACK);
        inputField.setBackground(Color.DARK_GRAY);
        inputField.setForeground(Color.WHITE);
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.WHITE);
        scrollPane.setBackground(Color.BLACK);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            switch (command) {
                case "=" -> evaluateExpression();
                case "C" -> inputField.setText("");
                case "CE" -> {
                    String currentText = inputField.getText();
                    if (currentText.length() > 0) {
                        inputField.setText(currentText.substring(0, currentText.length() - 1));
                    }
                }
                case "Graph" -> launchGraphingWindow();
                default -> inputField.setText(inputField.getText() + command);
            }
        }
    }

    private class EnterKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                evaluateExpression();
            }
        }
    }

    private void evaluateExpression() {
        try {
            calculator = new Calculator(inputField.getText());
            String postfixExpression = calculator.convertToPostFix();
            double result = calculator.evaluate();
            String outputText = "Expression: " + inputField.getText() + "\nPostfix: " + postfixExpression + "\nResult: " + result + "\n\n";

            String currentOutput = outputArea.getText();
            outputArea.setText(outputText + currentOutput);

            inputField.setText("");
            inputField.requestFocus();

            // Scroll to the top
            SwingUtilities.invokeLater(() -> {
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                verticalScrollBar.setValue(verticalScrollBar.getMinimum());
            });

            if (outputArea.getLineCount() > 20) {
                outputArea.setText(removeOldestExpression(outputArea.getText()));
            }
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage() + "\n\n" + outputArea.getText());
        }
    }

    private String removeOldestExpression(String text) {
        String[] lines = text.split("\n");
        StringBuilder newText = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (i < lines.length - 3) {
                newText.append(lines[i + 3]).append("\n");
            }
        }
        return newText.toString();
    }

    private void launchGraphingWindow() {
        String expression = inputField.getText();
        if (expression.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter an expression to graph.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        new Thread(() -> {
            GraphPlotter.launch(GraphPlotter.class, expression);
        }).start();
    }
}