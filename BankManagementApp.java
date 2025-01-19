package BankManagementApp;

import java.sql.*;
import java.util.Scanner;

public class BankManagementApp {
    static Scanner scanner = new Scanner(System.in);
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Shanadams1@";
    private static Connection connection;
    static int count =0;
    static boolean isTrue = true;
    static boolean condition = true;


    public static void main(String[] args) throws SQLException {
        int random_num= (int) (Math.random()*900000000)+100000000;

            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Welcome to the Bank Management Console Application!");


            while (isTrue) {

                System.out.println("\nMenu:");
                System.out.println("1. Create Account");
                System.out.println("2. Log-In");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> createAccount(connection, scanner);
                    case 2 -> login(connection,scanner);


                    case 3 -> {
                        System.out.println("Thank you for using the Bank Management App. Goodbye!");
                        isTrue = false;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
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


    private static void deleteAccount(Connection connection, Scanner scanner,int id) throws SQLException {

        System.out.println();
        System.out.println("Are You Willing to Delete Your Account");
        System.out.print("Choose an option(Y/N):");
        String ch1 ="Y";
        String ch = scanner.next();
        ch.toUpperCase();
        if(ch.equals(ch1)){
            String query = "DELETE FROM accounts WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("-----------------------------Account deleted successfully--------------------");
                    condition = false;
                }
            }
        }



    }

    private static void updateAccount(Connection connection, Scanner scanner, int id) throws SQLException {
        System.out.println();
        System.out.println("Choose Option to Update Details");

        System.out.println("1. Name");
        System.out.println("2. Email-id");
        System.out.println("3. Password");

        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice){
            case 1:
                System.out.print("Enter new Name: ");
                String newName = scanner.nextLine();

                String query = "UPDATE accounts SET name = ? WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, newName);
                    statement.setInt(2, id);
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("----------------------------Name updated successfully--------------------");
                    }
                }
                break;

            case 2:
                System.out.print("Enter new Email-id: ");
                String newEmail = scanner.nextLine();

                String query1 = "UPDATE accounts SET email_id = ? WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(query1)) {
                    statement.setString(1, newEmail);
                    statement.setInt(2, id);
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("----------------------Email-Id updated successfully----------------------");
                    }
                }
                break;
            case 3:
                System.out.print("Enter old password: ");
                String oldPass = scanner.next();


                String query3 = "Select password from accounts WHERE id=?";
                PreparedStatement statement1 = connection.prepareStatement(query3);
                statement1.setInt(1,id);
                ResultSet resultSet = statement1.executeQuery();
                if(resultSet.next()){
                    if(resultSet.getString("password").equals(oldPass)){

                    }
                    else{
                        break;
                    }
                }
                System.out.print("Enter new password: ");
                String newPass = scanner.next();

                if(oldPass.equals(newPass)){
                    System.out.println("Old Password Cannot be New Password");
                    break;
                }

                String query2 = "UPDATE accounts SET password = ? WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(query2)) {
                    statement.setString(1, newPass);
                    statement.setInt(2, id);
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("----------------------Password updated successfully----------------------");
                    }
                }
        }

        scanner.nextLine();

    }

    private static void depositFunds(Connection connection, Scanner scanner, int id) throws SQLException {



        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();

        System.out.print("Enter Password for Verification: ");
        String pass = scanner.next();
        scanner.nextLine();
        String query1 ="SELECT password from accounts WHERE id = ?";
        PreparedStatement statement1 = connection.prepareStatement(query1);
        statement1.setInt(1,id);
        ResultSet resultSet = statement1.executeQuery();
        if(resultSet.next()){
            if(resultSet.getString("password").equals(pass)){
                String query = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setDouble(1, amount);
                    statement.setInt(2, id);
                    int rowsAffected = statement.executeUpdate();
                    System.out.println();
                    System.out.println();
                    if (rowsAffected > 0) {
                        System.out.println("-------------------Deposit successful-----------------");
                    } else {
                        System.out.println("-------------------Account not found------------------");
                    }
                }
            }
        }

    }

    private static void withdrawFunds(Connection connection, Scanner scanner, int id) throws SQLException {

        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        System.out.print("Enter Password for Verification");
        String pass = scanner.next();
        scanner.nextLine();
        String query2="SELECT password FROM accounts WHERE id = ?";
        PreparedStatement statement1 = connection.prepareStatement(query2);
        statement1.setInt(1,id);
        ResultSet resultSet = statement1.executeQuery();

        String query = "UPDATE accounts SET balance = balance - ? WHERE id = ? AND balance >= ?";
        if(resultSet.next()){
            if(resultSet.getString("password").equals(pass)){
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setDouble(1, amount);
                    statement.setInt(2, id);
                    statement.setDouble(3, amount);
                    int rowsAffected = statement.executeUpdate();
                    System.out.println();
                    System.out.println();
                    if (rowsAffected > 0) {
                        System.out.println("--------------------------Withdrawal successful-----------------------");
                    } else {
                        System.out.println("--------------------------Insufficient balance or account not found------------------------");
                    }
                    System.out.println();
                    System.out.println();
                }
            }
        }

    }

    private static void transferFunds(Connection connection, Scanner scanner,int id) throws SQLException {
        System.out.print("Enter destination account ID: ");
        int destinationAccountId = scanner.nextInt();
        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();

        System.out.print("Re-Enter The Password for Verification: ");
        String pass = scanner.next();
        connection.setAutoCommit(false);
        try {
            String withdrawQuery = "UPDATE accounts SET balance = balance - ? WHERE id = ? AND balance >= ?";
            try (PreparedStatement withdrawStmt = connection.prepareStatement(withdrawQuery)) {
                withdrawStmt.setDouble(1, amount);
                withdrawStmt.setInt(2, id);
                withdrawStmt.setDouble(3, amount);
                String query ="SELECT password from accounts WHERE id = ?";

                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1,id);
                ResultSet resultSet = statement.executeQuery();
                System.out.println(amount);
                if(resultSet.next()){
                if(resultSet.getString("password").equals(pass)) {

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
                else {
                    count++;
                    if(count==2){
                        System.out.println("Last Try");
                        transferFunds(connection,scanner,id);
                        connection.rollback();
                    } else if (count<2) {
                        System.out.println("Retry");
                        transferFunds(connection,scanner,id);
                        connection.rollback();
                    } else if (count==3) {
                        System.out.println("Limit Over");
                        connection.rollback();
                    }

                }
                }
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static void checkBalance(Connection connection, Scanner scanner,int id) throws SQLException {

        System.out.println();
        String query = "SELECT balance FROM accounts WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Balance: " + resultSet.getDouble("balance"));
                } else {
                    System.out.println("Account not found.");
                }
                System.out.println();
                System.out.println("--------------------------------------------------");
            }
        }
    }
    private static void details(Connection connection, Scanner scanner,int id) throws SQLException {

        int count =0;
        String query = "SELECT * FROM accounts WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
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

    private static void login(Connection connection, Scanner scanner) throws SQLException{
        System.out.print("Enter the Account Number: ");
        int acc_number = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter the Password: ");
        String pass = scanner.next();

        String query1 ="SELECT id,password FROM accounts WHERE id=?";

        try (PreparedStatement statement = connection.prepareStatement(query1)) {
            statement.setInt(1, acc_number);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    if (resultSet.getInt("id")==acc_number && resultSet.getString("password").equals(pass)){

                        int id = resultSet.getInt("id");

                        System.out.println();
                        System.out.println();
                        System.out.println();
                        System.out.println();
                        System.out.println("-------------------------------Account loged-in----------------------------");
                        System.out.println();
                        System.out.println();
                        System.out.println();

                        condition=true;
                         while(condition){
                             System.out.println("1. Cash Withdraw");
                             System.out.println("2. Transfer Funds");
                             System.out.println("3. Balance Enquiry");
                             System.out.println("4. Deposit Funds");
                             System.out.println("5. Update Account Details");
                             System.out.println("6. Account Information");
                             System.out.println("7. Delete Account");
                             System.out.println("8. Go-To-Main Menu");
                             System.out.println();
                             System.out.print("Choose an Option: ");
                             int choice = scanner.nextInt();
                             scanner.nextLine();
                             switch(choice){
                                 case 7 -> deleteAccount(connection, scanner,id);
                                 case 5 -> updateAccount(connection, scanner,id);
                                 case 4 -> depositFunds(connection, scanner,id);
                                 case 1 -> withdrawFunds(connection, scanner,id);
                                 case 2 -> transferFunds(connection, scanner,id);
                                 case 3 -> checkBalance(connection, scanner,id);
                                 case 8 -> condition =false;
                                 case 6 -> details(connection, scanner,id);
                             }

                         }

                    }
                    else {
                        System.out.println("Invalid Password or Account Number");
                    }
                } else {
                    System.out.println("Account not found.");
                }
            }
        }

    }
}

