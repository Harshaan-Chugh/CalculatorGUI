import java.util.Scanner;

/**
    @author Harshaan Chugh
    Tester class for the Calculator class that provides a nice UI.
    Seperate from CalculatorGUI / meant for testing.
*/
public class CalculatorTester {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Please enter an integer-based expression: ");
            String expression = scanner.nextLine();
            Calculator calc1 = new Calculator(expression);

            try {
                String postfixExpression = calc1.convertToPostFix();
                System.out.println("Postfix Expression: " + postfixExpression);
    
                double result = calc1.evaluate();
                System.out.println("Result: " + result);
            }
            catch (Exception e) {
                System.out.println("Error evaluating expression. " + e.getMessage());
            }

            System.out.println("Options:");
            System.out.println("1. Play again");
            System.out.println("2. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> { }
                case 2 -> {
                    System.out.println("Exiting the calculator. Goodbye!");
                    scanner.close();
                    return;
                }
                default -> {
                    System.out.println("Invalid choice. Exiting the calculator. Goodbye!");
                    scanner.close();
                    return;
                }
            }
        }
    }
}