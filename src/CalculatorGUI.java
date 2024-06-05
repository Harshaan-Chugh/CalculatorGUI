import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JSplitPane;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ScrollPaneConstants;
import java.util.List;

/**
 * The CalculatorGUI class creates a graphical user interface for a calculator.
 * The calculator supports basic arithmetic operations and several additional functions.
 */
public class CalculatorGUI {
    /**
     * The main method that starts the calculator application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalculatorGUI::new);
    }

    private final JTextField inputField;
    private final JTextArea outputArea;
    private final JScrollPane outputScrollPane;
    private final JList<String> historyList;
    private final DefaultListModel<String> historyListModel;
    private final JScrollPane historyScrollPane;

    /**
     * Constructs the CalculatorGUI and initializes the components.
     */
    public CalculatorGUI() {
        JFrame frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);  // Set initial size to a more balanced width and height

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

        outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        outputScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(7, 4, 10, 10));

        String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", "(", ")", "+",
                "C", "CE", "=", "^",
                "sqrt", "log", "cos",
                "tan", "sin", "!"
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

        historyListModel = new DefaultListModel<>();
        historyList = new JList<>(historyListModel);
        historyList.setFont(new Font("Times", Font.PLAIN, 18));
        historyList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        historyList.addListSelectionListener(new HistorySelectionListener());

        historyScrollPane = new JScrollPane(historyList);
        historyScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        historyScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JLabel historyLabel = new JLabel("History");
        historyLabel.setFont(new Font("Arial", Font.BOLD, 20));
        historyLabel.setForeground(Color.WHITE);

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.add(historyLabel, BorderLayout.NORTH);
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);

        JLabel outputLabel = new JLabel("Output");
        outputLabel.setFont(new Font("Arial", Font.BOLD, 20));
        outputLabel.setForeground(Color.WHITE);

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.add(outputLabel, BorderLayout.NORTH);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, outputPanel, historyPanel);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);
        splitPane.setContinuousLayout(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputField, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set dark mode colors
        mainPanel.setBackground(Color.BLACK);
        inputField.setBackground(Color.DARK_GRAY);
        inputField.setForeground(Color.WHITE);
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.WHITE);
        outputScrollPane.setBackground(Color.BLACK);
        historyList.setBackground(Color.BLACK);
        historyList.setForeground(Color.WHITE);
        historyScrollPane.setBackground(Color.BLACK);
        historyPanel.setBackground(Color.BLACK);
        outputPanel.setBackground(Color.BLACK);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);

        updateHistory();
    }

    /**
     * ActionListener for handling button clicks in the calculator.
     */
    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            switch (command) {
                case "=" -> evaluateExpression();
                case "C" -> inputField.setText("");
                case "CE" -> {
                    String currentText = inputField.getText();
                    if (!currentText.isEmpty()) {
                        inputField.setText(currentText.substring(0, currentText.length() - 1));
                    }
                }
                default -> inputField.setText(inputField.getText() + command);
            }
        }
    }

    /**
     * KeyAdapter for handling Enter key press in the input field.
     */
    private class EnterKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                evaluateExpression();
            }
        }
    }

    /**
     * ListSelectionListener for handling history list selection.
     */
    private class HistorySelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                String selectedHistory = historyList.getSelectedValue();
                if (selectedHistory != null) {
                    String expression = selectedHistory.split(",")[0].split(":")[1].trim();
                    inputField.setText(expression);
                }
            }
        }
    }

    /**
     * Evaluates the expression entered in the input field, updates the output area with the result,
     * and handles any errors that may occur during the evaluation.
     */
    private void evaluateExpression() {
        try {
            Calculator calculator = new Calculator(inputField.getText());
            String postfixExpression = calculator.convertToPostFix();
            double result = calculator.evaluate();
            String resultText = result % 1 == 0 ? String.format("%.0f", result) : String.format("%.8f", result);
            String outputText = "Expression: " + inputField.getText() + "\nPostfix: " + postfixExpression + "\nResult: " + resultText + "\n\n";

            String currentOutput = outputArea.getText();
            outputArea.setText(outputText + currentOutput);

            inputField.setText("");
            inputField.requestFocus();

            updateHistory();

            // Scroll to the top
            SwingUtilities.invokeLater(() -> {
                JScrollBar verticalScrollBar = outputScrollPane.getVerticalScrollBar();
                verticalScrollBar.setValue(verticalScrollBar.getMinimum());
            });
        }
        catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage() + "\n\n" + outputArea.getText());
        }
    }

    /**
     * Updates the history list with the latest evaluated expressions.
     */
    private void updateHistory() {
        List<String> history = Calculator.getHistory();
        historyListModel.clear();
        for (String entry : history) {
            String[] parts = entry.split(",");
            String expression = parts[0].split(":")[1].trim();
            double result = Double.parseDouble(parts[2].split(":")[1].trim());
            String resultText = result % 1 == 0 ? String.format("%.0f", result) : String.format("%.8f", result);
            historyListModel.addElement("Expression: " + expression + ", Result: " + resultText);
        }
    }

}