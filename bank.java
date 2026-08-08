package banking;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Bank {
    public static void main(String[] args) {
        BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
        String name;
        int passCode;
        int choice;

        while (true) {
            System.out.println("\n===============================");
            System.out.println("        Welcome to InBank      ");
            System.out.println("===============================");
            System.out.println("1) Create Account");
            System.out.println("2) Login Account");
            System.out.println("3) Exit");

            try {
                System.out.print("\nEnter Choice: ");
                choice = Integer.parseInt(sc.readLine());

                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter Unique Username: ");
                        name = sc.readLine();
                        System.out.print("Enter Password (numeric): ");
                        passCode = Integer.parseInt(sc.readLine());
                        BankManagement.createAccount(name, passCode);
                    }
                    case 2 -> {
                        System.out.print("Enter Username: ");
                        name = sc.readLine();
                        System.out.print("Enter Password: ");
                        passCode = Integer.parseInt(sc.readLine());
                        BankManagement.loginAccount(name, passCode);
                    }
                    case 3 -> {
                        System.out.println("Thank you for using InBank! Goodbye.");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice! Please select 1, 2, or 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input format! Please enter numbers where required.");
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }
}
