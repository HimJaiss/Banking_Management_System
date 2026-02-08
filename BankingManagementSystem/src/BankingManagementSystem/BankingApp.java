package BankingManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class BankingApp {
    private static final String url = "jdbc:mysql://localhost:3306/banking_system";
    private static final String username = "root";
    private static final String password = "Prosit@1234";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Scanner scanner = new Scanner(System.in);

            User user = new User(connection, scanner);
            Accounts accounts = new Accounts(connection, scanner);
            AccountManager accountManager = new AccountManager(connection, scanner);
            AccountStatement accountStatement = new AccountStatement(connection, scanner);

            String email;
            long account_number;

            while (true) {
                System.out.println("*** WELCOME TO BANKING SYSTEM ***");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int choice1 = scanner.nextInt();

                switch (choice1) {
                    case 1:
                        user.register();
                        break;

                    case 2:
                        email = user.login();
                        if (email != null) {
                            System.out.println("\nUser Logged In!");
                            if (!accounts.account_exist(email)) {
                                System.out.println("\n1. Open a new Bank Account");
                                System.out.println("2. Exit");
                                if (scanner.nextInt() == 1) {
                                    account_number = accounts.open_account(email);
                                    System.out.println("Account Created Successfully");
                                    System.out.println("Your Account Number is: " + account_number);
                                } else {
                                    continue;
                                }
                            }

                            account_number = accounts.getAccount_number(email);

                            int choice2 = 0;
                            while (choice2 != 6) {
                                System.out.println("\n1. Debit Money");
                                System.out.println("2. Credit Money");
                                System.out.println("3. Transfer Money");
                                System.out.println("4. Check Balance");
                                System.out.println("5. View Account Statement");
                                System.out.println("6. Log Out");
                                System.out.print("Enter your choice: ");
                                choice2 = scanner.nextInt();

                                switch (choice2) {
                                    case 1:
                                        accountManager.debit_money(account_number);
                                        break;
                                    case 2:
                                        accountManager.credit_money(account_number);
                                        break;
                                    case 3:
                                        accountManager.transfer_money(account_number);
                                        break;
                                    case 4:
                                        accountManager.getBalance(account_number);
                                        break;
                                    case 5:
                                        accountStatement.viewStatement(account_number);
                                        break;
                                    case 6:
                                        System.out.println("Logging out...");
                                        break;
                                    default:
                                        System.out.println("Enter Valid Choice!");
                                }
                            }
                        }
                        break;

                    case 3:
                        System.out.println("Exiting...");
                        connection.close();
                        return;

                    default:
                        System.out.println("Enter Valid Choice!");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            
        }
    }
}
