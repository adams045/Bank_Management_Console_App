package BankManagementApp;

import java.sql.*;
import java.util.Scanner;

public class BankManagementApp {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Shanadams1@";

    public static void main(String[] args) {
        int random_num= (int) (Math.random()*900000000)+100000000;
        System.out.println(random_num);
        Scanner scanner = new Scanner(System.in);
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Welcome to the Bank Management Console Application!");
            boolean isTrue = true;

            while (isTrue) {
                System.out.println("\nMenu:");
                System.out.println("1. Create Account");
                System.out.println("2. Delete Account");
                System.out.println("3. Update Account");
                System.out.println("4. Deposit Funds");
                System.out.println("5. Withdraw Funds");
                System.out.println("6. Transfer Funds");
                System.out.println("7. Check Balance");
                System.out.println("8. Details");
                System.out.println("9. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> createAccount(connection, scanner);
                    case 2 -> deleteAccount(connection, scanner);
                    case 3 -> updateAccount(connection, scanner);
                    case 4 -> depositFunds(connection, scanner);
                    case 5 -> withdrawFunds(connection, scanner);
                    case 6 -> transferFunds(connection, scanner);
                    case 7 -> checkBalance(connection, scanner);
                    case 8 -> details(connection,scanner);
                    case 9 -> {
                        System.out.println("Thank you for using the Bank Management App. Goodbye!");
                        isTrue = false;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    private static void createAccount(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.println("Enter the Email_id");
        String email = scanner.next();
        System.out.println("Set password");
        String password = scanner.next();
        System.out.println("Enter Your Mobile Number");
        long number = scanner.nextLong();
        System.out.print("Enter initial balance: ");
        double balance = scanner.nextDouble();
        int random_num= (int) (Math.random()*900000000)+100000000;
        String query = "INSERT INTO accounts  VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1,random_num);
            statement.setString(2, name);
            statement.setDouble(3, balance);
            statement.setString(4,email);
            statement.setString(5,password);
            statement.setLong(6,number);

            statement.executeUpdate();
            System.out.println("Account created successfully.");
            System.out.println("Your Account Id is: "+ random_num+ " Note it for Further process");
        }
    }

    private static void deleteAccount(Connection connection, Scanner scanner) throws SQLException {

        System.out.print("Enter account ID to delete: ");
        int accountId = scanner.nextInt();

        String query = "DELETE FROM accounts WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, accountId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Account deleted successfully.");
            } else {
                System.out.println("Account not found.");
            }
        }
    }

    private static void updateAccount(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter account ID to update: ");
        int accountId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();

        String query = "UPDATE accounts SET name = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, newName);
            statement.setInt(2, accountId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Account updated successfully.");
            } else {
                System.out.println("Account not found.");
            }
        }
    }

    private static void depositFunds(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter account ID to deposit into: ");
        int accountId = scanner.nextInt();
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();

        String query = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, amount);
            statement.setInt(2, accountId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Deposit successful.");
            } else {
                System.out.println("Account not found.");
            }
        }
    }

    private static void withdrawFunds(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter account ID to withdraw from: ");
        int accountId = scanner.nextInt();
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();

        String query = "UPDATE accounts SET balance = balance - ? WHERE id = ? AND balance >= ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, amount);
            statement.setInt(2, accountId);
            statement.setDouble(3, amount);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Withdrawal successful.");
            } else {
                System.out.println("Insufficient balance or account not found.");
            }
        }
    }

    private static void transferFunds(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter source account ID: ");
        int sourceAccountId = scanner.nextInt();
        System.out.print("Enter destination account ID: ");
        int destinationAccountId = scanner.nextInt();
        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();

        connection.setAutoCommit(false);
        try {
            String withdrawQuery = "UPDATE accounts SET balance = balance - ? WHERE id = ? AND balance >= ?";
            try (PreparedStatement withdrawStmt = connection.prepareStatement(withdrawQuery)) {
                withdrawStmt.setDouble(1, amount);
                withdrawStmt.setInt(2, sourceAccountId);
                withdrawStmt.setDouble(3, amount);
                if (withdrawStmt.executeUpdate() > 0) {
                    String depositQuery = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
                    try (PreparedStatement depositStmt = connection.prepareStatement(depositQuery)) {
                        depositStmt.setDouble(1, amount);
                        depositStmt.setInt(2, destinationAccountId);
                        depositStmt.executeUpdate();
                        connection.commit();
                        System.out.println("Transfer successful.");
                    }
                } else {
                    System.out.println("Insufficient balance or account not found.");
                    connection.rollback();
                }
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static void checkBalance(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter account ID to check balance: ");
        int accountId = scanner.nextInt();

        String query = "SELECT balance FROM accounts WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Balance: " + resultSet.getDouble("balance"));
                } else {
                    System.out.println("Account not found.");
                }
            }
        }
    }
    private static void details(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter account ID to check details: ");
        int accountId = scanner.nextInt();
        int count =0;
        String query = "SELECT * FROM accounts WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    count++;
                    System.out.println("Account_Id: " + resultSet.getInt("id"));
                    System.out.println("Name: " + resultSet.getString("name"));
                    System.out.println("Balance: " + resultSet.getDouble("balance"));
                    System.out.println("E-mail Id: " + resultSet.getString("email_id"));

                }
                if (count==0){
                    System.out.println("Account Not Found");
                }
            }
        }
    }
}

