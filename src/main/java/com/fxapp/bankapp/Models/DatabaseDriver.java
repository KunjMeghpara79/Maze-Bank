package com.fxapp.bankapp.Models;

import java.sql.*;
import java.time.LocalDate;

public class DatabaseDriver {

    private Connection conn;

    public DatabaseDriver() {
        try {
            this.conn = DriverManager.getConnection("jdbc:sqlite:mazebank.db");
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database...");
        }
    }

    /* Client Section */
    public ResultSet getClientData(String pAddress, String password) {
        Statement statement;
        ResultSet resultSet;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery(
                    "SELECT * FROM Clients WHERE PayeeAddress='"+pAddress+"' AND Password='"+password+"';");
        } catch (SQLException e) {
            System.out.println("Couldn't get client data..." + e.getMessage());
            return null;
        }
        return resultSet;
    }

    public ResultSet getTransactions(String pAddress, int transactCount) {
        Statement statement;
        ResultSet resultSet;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery(
                    "SELECT * FROM Transactions " +
                            "WHERE Sender='"+pAddress+"' " + "OR Receiver='"+pAddress+"' " +
                            "ORDER BY ID DESC LIMIT "+transactCount+";");
        } catch (SQLException e) {
            System.out.println("Couldn't retrieve transactions: " + e.getMessage());
            return null;
        }
        return resultSet;
    }

    public double getCheckingAccountBalance(String pAddress) {
        Statement statement;
        ResultSet resultSet;
        double balance;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery(
                    "SELECT * FROM CheckingAccounts WHERE Owner='"+pAddress+"';");
            balance = resultSet.getDouble("Balance");
        } catch (SQLException e) {
            System.out.println("getCheckingAccountBalance() failed: " + e.getMessage());
            return -1;
        }
        return balance;
    }

    public double getSavingsAccountBalance(String pAddress) {
        Statement statement;
        ResultSet resultSet;
        double balance;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery(
                    "SELECT * FROM SavingsAccounts WHERE Owner='"+pAddress+"';");
            balance = resultSet.getDouble("Balance");
        } catch (SQLException e) {
            System.out.println("getSavingsAccountBalance() failed: " + e.getMessage());
            return -1;
        }
        return balance;
    }

    public boolean updateCheckingBalance(String pAddress, double amount, String operation) {
        Statement statement;
        ResultSet resultSet;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery(
                    "SELECT * FROM CheckingAccounts WHERE Owner='"+pAddress+"';");
            double newBalance;
            if(operation.equals("ADD")) {
                newBalance = resultSet.getDouble("Balance") + amount;
            } else {
                newBalance = resultSet.getDouble("Balance") - amount;
            }
            if(newBalance < 0) {
                Model.getInstance().setErrorMsg("Not enough funds!");
                Model.getInstance().getViewFactory().showErrorWindow();
                return false;
            }
            statement.executeUpdate(
                    "UPDATE CheckingAccounts SET Balance='"+newBalance+"' WHERE Owner='"+pAddress+"';");
        } catch (SQLException e) {
            System.out.println("updateBalance() failed: " + e.getMessage());
        }
        return true;
    }

    public boolean updateSavingsBalance(String pAddress, double amount, String operation) {
        Statement statement;
        ResultSet resultSet;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery(
                    "SELECT * FROM SavingsAccounts WHERE Owner='"+pAddress+"';");
            double newBalance;
            if(operation.equals("ADD")) {
                newBalance = resultSet.getDouble("Balance") + amount;
            } else {
                double wLimit = resultSet.getDouble("WithdrawalLimit");
                if(amount > wLimit) {
                    Model.getInstance().setErrorMsg("Amount is over withdrawal limit!");
                    Model.getInstance().getViewFactory().showErrorWindow();
                    return false;
                }
                newBalance = resultSet.getDouble("Balance") - amount;
            }
            if(newBalance < 0) {
                Model.getInstance().setErrorMsg("Not enough funds!");
                Model.getInstance().getViewFactory().showErrorWindow();
                return false;
            }
            statement.executeUpdate(
                    "UPDATE SavingsAccounts SET Balance='"+newBalance+"' WHERE Owner='"+pAddress+"';");
        } catch (SQLException e) {
            System.out.println("updateSavingsBalance() failed: " + e.getMessage());
            return false;
        }
        return true;
    }

    public Transaction newTransaction(String sender, String receiver, double amount, String message) {
        Statement statement;
        LocalDate date = LocalDate.now();
        Transaction toAdd = new Transaction(sender, receiver, amount, date, message);
        try {
            statement = this.conn.createStatement();
            statement.executeUpdate(
                    "INSERT INTO Transactions (Sender, Receiver, Amount, Date, Message)" +
                    "VALUES ('"+sender+"', '"+receiver+"', "+amount+", '"+date+"', '"+message+"');");
        } catch (SQLException e) {
            return null;
        }
        return toAdd;
    }

    /* Administrator Section */
    public ResultSet getAdminData(String username, String password) {
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery(
                    "SELECT * FROM Admins WHERE Username='"+username+"' AND Password='"+password+"';");
        } catch (SQLException e) {
            System.out.println("Couldn't get admin data...");
        }
        return resultSet;
    }

    public void createClient(String fName, String lName, String pAddress, String password, LocalDate date) {
        Statement statement;
        try {
            statement = this.conn.createStatement();
            statement.executeUpdate("INSERT INTO " +
                    "Clients (FirstName, LastName, PayeeAddress, Password, Date) " +
                    "VALUES ('"+fName+"', '"+lName+"', '"+pAddress+"', '"+password+"', '"+date.toString()+"');");
        } catch (SQLException e) {
            System.out.println("Create client: Couldn't update database...");
        }
    }

    public void createCheckingAccount(String owner, String number, double tLimit, double balance) {
        Statement statement;
        try {
            statement = this.conn.createStatement();
            statement.executeUpdate("INSERT INTO " +
                    "CheckingAccounts (Owner, AccountNumber, TransactionLimit, Balance) " +
                    "VALUES ('"+owner+"', '"+number+"', "+tLimit+", "+balance+");");
        } catch (SQLException e) {
            System.out.println("Create chkAcc: Couldn't create checking account...");
        }
    }

    public void createSavingsAccount(String owner, String number, double wLimit, double balance) {
        Statement statement;
        try {
            statement = this.conn.createStatement();
            statement.executeUpdate("INSERT INTO " +
                    "SavingsAccounts (Owner, AccountNumber, WithdrawalLimit, Balance) " +
                    "VALUES ('"+owner+"', '"+number+"', "+wLimit+", "+balance+");");
        } catch (SQLException e) {
            System.out.println("Create svAcc: Couldn't create checking account...");
        }
    }

    public ResultSet getAllClientsData() {
        Statement statement;
        ResultSet resultSet;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM Clients;");
        } catch (SQLException e) {
            System.out.println("getAllClientsData() failed: " + e.getMessage());
            return null;
        }
        return resultSet;
    }

    public void depositSavings(String pAddress, double amount) {
        Statement statement;
        ResultSet resultSet;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery(
                    "SELECT * FROM SavingsAccounts WHERE Owner='"+pAddress+"';");
            double newBalance = resultSet.getDouble("Balance") + amount;
            statement.executeUpdate(
                    "UPDATE SavingsAccounts SET Balance='"+newBalance+"' WHERE Owner='"+pAddress+"';");
        } catch (SQLException e) {
            System.out.println("depositSavings() failed: " + e.getMessage());
        }
    }

    /* Utility Methods */
    public ResultSet searchClient(String pAddress) {
        Statement statement;
        ResultSet resultSet;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM Clients WHERE PayeeAddress='"+pAddress+"';");
        } catch (SQLException e) {
            System.out.println("searchClient() failed: " + e.getMessage());
            return null;
        }
        return resultSet;
    }

    public int getLastClientsId() {
        Statement statement;
        ResultSet resultSet;
        int id = -1;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM sqlite_sequence WHERE name='Clients';");
            if(!resultSet.isBeforeFirst()) {
                return id;
            }
            id = resultSet.getInt("seq");
        } catch (SQLException e) {
            System.out.println("getLastClientsId() failed: " + e.getMessage());
        }
        return id;
    }

    public ResultSet getCheckingAccountData(String pAddress) {
        Statement statement;
        ResultSet resultSet;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM CheckingAccounts WHERE Owner='"+pAddress+"';");
        } catch (SQLException e) {
            System.out.println("getCheckingAccountData() failed: " + e.getMessage());
            return null;
        }
        return resultSet;
    }

    public ResultSet getSavingsAccountData(String pAddress) {
        Statement statement;
        ResultSet resultSet;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM SavingsAccounts WHERE Owner='"+pAddress+"';");
        } catch (SQLException e) {
            System.out.println("getSavingsAccountData() failed: " + e.getMessage());
            return null;
        }
        return resultSet;
    }
}
