package BankingManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class AccountStatement {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_system";
    private static final String DB_USER = "root";  // change if needed
    private static final String DB_PASS = "Prosit@1234";  // change if needed

    
	public AccountStatement(Connection connection, Scanner scanner) {
		// TODO Auto-generated constructor stub
	}


	public void viewStatement(long accountNumber) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of recent transactions to view: ");
        int limit = sc.nextInt();

        String query = "SELECT transaction_type, amount, transaction_date, description " +
                       "FROM transactions " +
                       "WHERE account_number = ? " +
                       "ORDER BY transaction_date DESC LIMIT ?";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setLong(1, accountNumber);
            ps.setInt(2, limit);

            ResultSet rs = ps.executeQuery();

            System.out.println("\n==== Account Statement for Account No: " + accountNumber + " ====");
            System.out.printf("%-15s %-10s %-20s %-30s%n", "Type", "Amount", "Date", "Description");
            System.out.println("----------------------------------------------------------------------");

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                String type = rs.getString("transaction_type");
                double amount = rs.getDouble("amount");
                Timestamp date = rs.getTimestamp("transaction_date");
                String desc = rs.getString("description");

                System.out.printf("%-15s %-10.2f %-20s %-30s%n", type, amount, date, desc);
            }

            if (!hasData) {
                System.out.println("No transactions found for this account.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
