package BankingManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class AccountManager {
    private Connection connection;
    private Scanner scanner;

    public AccountManager(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    // CREDIT MONEY
    public void credit_money(long account_number) {
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // consume newline

        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }

        String query = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, amount);
            preparedStatement.setLong(2, account_number);
            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                logTransaction(account_number, "CREDIT", amount, "Money deposited");
                System.out.println("₹" + amount + " credited successfully.");
            } else {
                System.out.println("Account not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DEBIT MONEY
    public void debit_money(long account_number) {
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }

        String checkBalance = "SELECT balance FROM Accounts WHERE account_number = ?";
        try {
            PreparedStatement balanceStmt = connection.prepareStatement(checkBalance);
            balanceStmt.setLong(1, account_number);
            ResultSet rs = balanceStmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance < amount) {
                    System.out.println("Insufficient balance.");
                    return;
                }

                String update = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";
                PreparedStatement updateStmt = connection.prepareStatement(update);
                updateStmt.setDouble(1, amount);
                updateStmt.setLong(2, account_number);
                updateStmt.executeUpdate();

                logTransaction(account_number, "DEBIT", amount, "Money withdrawn");
                System.out.println("₹" + amount + " debited successfully.");
            } else {
                System.out.println("Account not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // TRANSFER MONEY
    public void transfer_money(long from_account) {
        System.out.print("Enter recipient account number: ");
        long to_account = scanner.nextLong();
        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }

        try {
            connection.setAutoCommit(false);

            // Check sender balance
            String checkBalance = "SELECT balance FROM Accounts WHERE account_number = ?";
            PreparedStatement balanceStmt = connection.prepareStatement(checkBalance);
            balanceStmt.setLong(1, from_account);
            ResultSet rs = balanceStmt.executeQuery();

            if (!rs.next() || rs.getDouble("balance") < amount) {
                System.out.println("Insufficient balance or sender account not found.");
                connection.rollback();
                return;
            }

            // Debit sender
            PreparedStatement debitStmt = connection.prepareStatement(
                    "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?");
            debitStmt.setDouble(1, amount);
            debitStmt.setLong(2, from_account);
            debitStmt.executeUpdate();

            // Credit receiver
            PreparedStatement creditStmt = connection.prepareStatement(
                    "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?");
            creditStmt.setDouble(1, amount);
            creditStmt.setLong(2, to_account);
            int rows = creditStmt.executeUpdate();

            if (rows == 0) {
                System.out.println("Recipient account not found. Rolling back...");
                connection.rollback();
                return;
            }

            // Log transactions
            logTransaction(from_account, "TRANSFER_DEBIT", amount, "Transferred to " + to_account);
            logTransaction(to_account, "TRANSFER_CREDIT", amount, "Received from " + from_account);

            connection.commit();
            System.out.println("₹" + amount + " transferred successfully.");

        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // LOG TRANSACTION
    private void logTransaction(long account_number, String type, double amount, String description) {
        String insertTransaction = "INSERT INTO transactions (account_number, transaction_type, amount, transaction_date, description) VALUES (?, ?, ?, NOW(), ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(insertTransaction);
            stmt.setLong(1, account_number);
            stmt.setString(2, type);
            stmt.setDouble(3, amount);
            stmt.setString(4, description);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // GET BALANCE
    public double getBalance(long account_number) {
        String query = "SELECT balance FROM Accounts WHERE account_number = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setLong(1, account_number);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            } else {
                System.out.println("Account not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}

