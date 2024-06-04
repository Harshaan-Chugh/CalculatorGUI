import java.util.EmptyStackException;
import java.util.Stack;

/**
    The Calculator Class tokenizes the expression, then converts it to postfix,
    (Reverse Polish Notation) and finally evaluates the postfix. Valid expressions
    include integer expressions, though the evalutaion of the expression will involve double.
*/
public class Calculator {
    String expression;

    public Calculator(String input) {
        expression = input.replaceAll(" ", "");
        if (!isValidExpression()) {
            throw new IllegalArgumentException("Error: Improper expression format.");
        }
    }

    private boolean isValidExpression() {
        return hasMatchingParentheses();
    }

    private boolean hasMatchingParentheses() {
        Stack<Character> parenthesesStack = new Stack<>();
        for (char c : expression.toCharArray()) {
            if (c == '(') {
                parenthesesStack.push(c);
            } else if (c == ')') {
                try {
                    parenthesesStack.pop();
                } catch (EmptyStackException e) {
                    return false;
                }
            }
        }
        return parenthesesStack.isEmpty();
    }

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
                if (currentNumber.length() > 0) {
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
                    default -> throw new IllegalArgumentException("Unsupported character: " + c);
                }
            }
        }

        if (currentNumber.length() > 0) {
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

    private boolean hasPrecedence(String op1, String op2) {
        int precedenceOp1 = getOperatorPrecedence(op1);
        int precedenceOp2 = getOperatorPrecedence(op2);
        return precedenceOp1 >= precedenceOp2;
    }

    private int getOperatorPrecedence(String operator) {
        return switch (operator) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "^" -> 3;
            default -> 0;
        };
    }

    public double evaluate() {
        String postfixExpression = convertToPostFix();
        return evaluatePostFix(postfixExpression);
    }

    public static double evaluatePostFix(String postfix) {
        Stack<Double> stack = new Stack<>();
        String[] tokens = postfix.split("\\s+");

        for (String token : tokens) {
            if (token.matches("\\d+")) {
                stack.push(Double.valueOf(token));
            } else if (token.matches("[+\\-*/^]")) {
                double operand2 = stack.pop();
                double operand1 = stack.pop();
                double result = applyOperator(token, operand1, operand2);
                stack.push(result);
            } else if (token.matches("sin|cos|tan|log|sqrt|!")) {
                double operand = stack.pop();
                double result = applyFunction(token, operand);
                stack.push(result);
            } else {
                throw new IllegalArgumentException("Invalid token: " + token);
            }
        }

        return stack.pop();
    }

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

    private static double applyFunction(String function, double operand) {
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

    private static double factorial(int n) {
        if (n == 0 || n == 1) {
            return 1;
        }
        return n * factorial(n - 1);
    }

    public static void main(String[] args) {
        Calculator calculator = new Calculator("3 + sin(30)");
        String postfix = calculator.convertToPostFix();
        System.out.println("Postfix: " + postfix);
        double result = calculator.evaluate();
        System.out.println("Result: " + result);
    }
}
