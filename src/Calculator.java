import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

/**
 * The Calculator Class tokenizes the expression, then converts it to postfix,
 * (Reverse Polish Notation) and finally evaluates the postfix. Valid expressions
 * include integer expressions, though the evaluation of the expression will involve double.
 */
public class Calculator {
    String expression;
    private static final List<String> history = new ArrayList<>();

    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        Calculator calculator = new Calculator("69 + sin(69)");
        String postfix = calculator.convertToPostFix();
        System.out.println("Postfix: " + postfix);
        double result = calculator.evaluate();
        System.out.println("Result: " + String.format("%.8f", result));
    }

    /**
     * @param input Constructs the calculator for the given expression.
     *              Checks if the expression is valid.
     */
    public Calculator(String input) {
        expression = input.replaceAll(" ", "");
        if (!isValidExpression()) {
            throw new IllegalArgumentException("Error: Improper expression format.");
        }
    }

    /**
     * Checks validity of expression.
     *
     * @return true if valid, false otherwise.
     */
    private boolean isValidExpression() {
        return hasMatchingParentheses();
    }

    /**
     * Checks use of parenthesis.
     *
     * @return true if parenthesis are used appropriately, false otherwise
     */
    private boolean hasMatchingParentheses() {
        Stack<Character> parenthesesStack = new Stack<>();
        for (char c : expression.toCharArray()) {
            if (c == '(') {
                parenthesesStack.push(c);
            }
            else if (c == ')') {
                try {
                    parenthesesStack.pop();
                }
                catch (EmptyStackException e) {
                    return false;
                }
            }
        }
        return parenthesesStack.isEmpty();
    }

    /**
     * Converts the expression input to viable postfix string
     *
     * @return postfix expression
     */
    public String convertToPostFix() {
        Stack<String> operators = new Stack<>();
        Stack<String> numbers = new Stack<>();
        StringBuilder currentNumber = new StringBuilder();
        StringBuilder currentFunction = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c)) {
                currentNumber.append(c);
            } else {
                if (!currentNumber.isEmpty()) {
                    numbers.push(currentNumber.toString());
                    currentNumber.setLength(0);
                }

                if (Character.isLetter(c)) {
                    currentFunction.append(c);
                    while (i + 1 < expression.length() && Character.isLetter(expression.charAt(i + 1))) {
                        currentFunction.append(expression.charAt(++i));
                    }
                    operators.push(currentFunction.toString());
                    currentFunction.setLength(0);
                    continue;
                }

                switch (c) {
                    case '+', '-', '*', '/', '^' -> {
                        while (!operators.isEmpty() && hasPrecedence(operators.peek(), Character.toString(c))) {
                            numbers.push(operators.pop());
                        }
                        operators.push(Character.toString(c));
                    }
                    case '(' -> operators.push("(");
                    case ')' -> {
                        while (!operators.isEmpty() && !operators.peek().equals("(")) {
                            numbers.push(operators.pop());
                        }
                        operators.pop();
                    }
                    case '!' -> numbers.push(Character.toString(c));
                    default -> throw new IllegalArgumentException("Unsupported character: " + c);
                }
            }
        }

        if (!currentNumber.isEmpty()) {
            numbers.push(currentNumber.toString());
        }

        while (!operators.isEmpty()) {
            numbers.push(operators.pop());
        }

        StringBuilder result = new StringBuilder();
        while (!numbers.isEmpty()) {
            result.insert(0, " " + numbers.pop());
        }

        return result.toString().trim();
    }

    /**
     * Helper method to check operator precedence
     *
     * @return true if op1 higher or equal precedence than op2, false if lower
     */
    private boolean hasPrecedence(String op1, String op2) {
        int precedenceOp1 = getOperatorPrecedence(op1);
        int precedenceOp2 = getOperatorPrecedence(op2);
        return precedenceOp1 >= precedenceOp2;
    }

    /**
     * Helper method to get operator precedence
     *
     * @return precedence number
     */
    private int getOperatorPrecedence(String operator) {
        return switch (operator) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "^" -> 3;
            default -> 0;
        };
    }

    /**
     * Evaluates the expression.
     *
     * @return result of the evaluation
     */
    public double evaluate() {
        String postfixExpression = convertToPostFix();
        double result = evaluatePostFix(postfixExpression);
        addToHistory(expression, postfixExpression, result);
        return result;
    }

    /**
     * Solves the math expression using the postfix expression.
     *
     * @return evaluated postfix expression
     */
    public static double evaluatePostFix(String postfix) {
        Stack<Double> stack = new Stack<>();
        String[] tokens = postfix.split("\\s+");

        for (String token : tokens) {
            if (token.matches("\\d+")) {
                stack.push(Double.valueOf(token));
            }
            else if (token.matches("[+\\-*/^]")) {
                double operand2 = stack.pop();
                double operand1 = stack.pop();
                double result = applyOperator(token, operand1, operand2);
                stack.push(result);
            }
            else if (token.matches("sin|cos|tan|log|sqrt|!")) {
                double operand = stack.pop();
                double result = applyFunction(token, operand);
                stack.push(result);
            }
            else {
                throw new IllegalArgumentException("Invalid token: " + token);
            }
        }

        return stack.pop();
    }

    /**
     * Helper method that performs the operation.
     *
     * @return the result of the current operation
     */
    private static double applyOperator(String operator, double operand1, double operand2) {
        return switch (operator) {
            case "+" -> operand1 + operand2;
            case "-" -> operand1 - operand2;
            case "*" -> operand1 * operand2;
            case "/" -> operand1 / operand2;
            case "^" -> Math.pow(operand1, operand2);
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }

    /**
     * Helper method
     *
     * @return the result of the trigonometric and other mathematical functions on an operand
     */
    private static double applyFunction(String function, double operand) {
        function = function.replaceAll("\\s+", "");
        return switch (function) {
            case "sin" -> Math.sin(Math.toRadians(operand));
            case "cos" -> Math.cos(Math.toRadians(operand));
            case "tan" -> Math.tan(Math.toRadians(operand));
            case "log" -> Math.log10(operand);
            case "sqrt" -> Math.sqrt(operand);
            case "!" -> factorial((int) operand);
            default -> throw new IllegalArgumentException("Unsupported function: " + function);
        };
    }

    /**
     * Helper method that uses recursion to compute a number's factorial.
     *
     * @return factorial of a number
     */
    private static double factorial(int n) {
        if (n == 0 || n == 1) {
            return 1;
        }
        return n * factorial(n - 1);
    }

    /**
     * Adds an evaluated expression to the history.
     *
     * @param expression Original expression
     * @param postfix    Reverse Polish form of the expression
     * @param result     Result of evaluating the expression
     */
    public static void addToHistory(String expression, String postfix, double result) {
        history.add("Expression: " + expression + ", Postfix: " + postfix + ", Result: " + String.format("%.8f", result));
    }

    /**
     * Retrieves the history of evaluated expressions.
     *
     * @return A list of strings representing the history.
     */
    public static ArrayList<String> getHistory() {
        return new ArrayList<>(history);
    }

}