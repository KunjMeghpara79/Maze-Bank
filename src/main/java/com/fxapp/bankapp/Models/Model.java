package com.fxapp.bankapp.Models;

import com.fxapp.bankapp.Views.AccountType;
import com.fxapp.bankapp.Views.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Model {

    private static Model model;
    private final ViewFactory viewFactory;
    private final DatabaseDriver databaseDriver;
    // Client Data Section
    private final Client client;
    private boolean clientLoginSuccessFlag;
    private final ObservableList<Transaction> latestTransactions;
    private final ObservableList<Transaction> allTransactions;
    // Admin Data Section
    private boolean adminLoginSuccessFlag;
    private final ObservableList<Client> clients;

    private String errorMsg;

    private Model() {
        this.viewFactory = new ViewFactory();
        this.databaseDriver = new DatabaseDriver();
        this.errorMsg = "";
        // Client Data Section
        this.clientLoginSuccessFlag = false;
        this.client = new Client("","","",null, null, null);
        this.latestTransactions = FXCollections.observableArrayList();
        this.allTransactions = FXCollections.observableArrayList();
        // Admin Data Section
        this.adminLoginSuccessFlag = false;
        this.clients = FXCollections.observableArrayList();
    }

    // Implements singleton
    public static synchronized Model getInstance() {
        if(model == null) {
            model = new Model();
        }
        return model;
    }

    public ViewFactory getViewFactory() {
        return this.viewFactory;
    }

    public DatabaseDriver getDatabaseDriver() {
        return this.databaseDriver;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /* Client Method Section */
    public boolean getClientLoginSuccessFlag() {
        return this.clientLoginSuccessFlag;
    }

    public void setClientLoginSuccessFlag(boolean flag) {
        this.clientLoginSuccessFlag = flag;
    }

    public Client getClient() {
        return this.client;
    }

    public void evaluateClientCred(String pAddress, String password) {
        CheckingAccount checkingAccount;
        SavingsAccount savingsAccount;
        ResultSet resultSet = getDatabaseDriver().getClientData(pAddress, password);
        try {
            if(resultSet.isBeforeFirst()) {
                // if resultSet is not empty read columns from database tables
                this.client.getFirstNameProperty().set(resultSet.getString("FirstName"));
                this.client.getLastNameProperty().set(resultSet.getString("LastName"));
                this.client.getPayeeAddressProperty().set(resultSet.getString("PayeeAddress"));

                String[] dateParts = resultSet.getString("Date").split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int day = Integer.parseInt(dateParts[2]);
                LocalDate date = LocalDate.of(year, month, day);
                this.client.getDateCreatedProperty().set(date);

                checkingAccount = getCheckingAccount(pAddress);
                savingsAccount = getSavingsAccount(pAddress);
                this.client.getCheckingAccountProperty().set(checkingAccount);
                this.client.getSavingsAccountProperty().set(savingsAccount);
                setClientLoginSuccessFlag(true);
            }
        } catch (SQLException e) {
            System.out.println("evaluateClientCred() failed: " + e.getMessage());
        }
    }

    private void prepareTransactions(ObservableList<Transaction> transactions, int limit) {
        String pAddress = this.client.getPayeeAddressProperty().get();
        ResultSet resultSet = getDatabaseDriver().getTransactions(pAddress, limit);
        if(resultSet == null) {
            setErrorMsg("Transaction result set is null!");
            getViewFactory().showErrorWindow();
            return;
        }
        try {
            while(resultSet.next()) {
                String sender = resultSet.getString("Sender");
                String receiver = resultSet.getString("Receiver");
                double amount = resultSet.getDouble("Amount");

                String[] dateParts = resultSet.getString("Date").split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int day = Integer.parseInt(dateParts[2]);
                LocalDate date = LocalDate.of(year, month, day);

                String message = resultSet.getString("Message");
                Transaction toAdd = new Transaction(sender, receiver, amount, date, message);
                transactions.add(toAdd);
            }
        } catch (SQLException e) {
            System.out.println("prepareTransactions() failed: " + e.getMessage());
        }
    }

    public void setLatestTransactions() {
        prepareTransactions(this.latestTransactions, 4);
    }

    public ObservableList<Transaction> getLatestTransactions() {
        return this.latestTransactions;
    }

    public void setAllTransactions() {
        // set limit to "-1" to get all transactions
        prepareTransactions(this.allTransactions, -1);
    }

    public ObservableList<Transaction> getAllTransactions() {
        return this.allTransactions;
    }

    /* Admin Method Section */
    public boolean getAdminLoginSuccessFlag() {
        return this.adminLoginSuccessFlag;
    }

    public void setAdminLoginSuccessFlag(boolean flag) {
        this.adminLoginSuccessFlag = flag;
    }

    public void evaluateAdminCred(String username, String password) {
        ResultSet resultSet = getDatabaseDriver().getAdminData(username, password);
        try {
            if(resultSet.isBeforeFirst()) {
                setAdminLoginSuccessFlag(true);
            }
        } catch (SQLException e) {
            System.out.println("Evaluate admin credentials failed...");
        }
    }

    public ObservableList<Client> getClients() {
        return this.clients;
    }

    public void setClients() {
        CheckingAccount checkingAccount;
        SavingsAccount savingsAccount;
        ResultSet resultSet = getDatabaseDriver().getAllClientsData();
        try {
            while(resultSet.next()) {
                String fName = resultSet.getString("FirstName");
                String lName = resultSet.getString("LastName");
                String pAddress = resultSet.getString("PayeeAddress");
                String[] dateParts = resultSet.getString("Date").split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int day = Integer.parseInt(dateParts[2]);
                LocalDate date = LocalDate.of(year, month, day);
                checkingAccount = getCheckingAccount(pAddress);
                savingsAccount = getSavingsAccount(pAddress);
                Client toAdd = new Client(fName, lName, pAddress, checkingAccount, savingsAccount, date);
                this.clients.add(toAdd);
            }
        } catch (SQLException e) {
            System.out.println("getAllClientsData() failed: " + e.getMessage());
        }
    }

    public ObservableList<Client> searchClient(String pAddress) {
        ObservableList<Client> searchResults = FXCollections.observableArrayList();
        ResultSet resultSet = getDatabaseDriver().searchClient(pAddress);
        try {
            if(!resultSet.isBeforeFirst()) {
                return null;
            }
            CheckingAccount checkingAccount = getCheckingAccount(pAddress);
            SavingsAccount savingsAccount = getSavingsAccount(pAddress);
            String fName = resultSet.getString("FirstName");
            String lName = resultSet.getString("LastName");
            String[] dateParts = resultSet.getString("Date").split("-");
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int day = Integer.parseInt(dateParts[2]);
            LocalDate date = LocalDate.of(year, month, day);
            Client toRetrieve = new Client(fName, lName, pAddress, checkingAccount, savingsAccount, date);
            searchResults.add(toRetrieve);
        } catch (Exception e) {
            System.out.println("searchClient() failed: " + e.getMessage());
            return null;
        }
        return searchResults;
    }

    /* Utility Method Section */
    public CheckingAccount getCheckingAccount(String pAddress) {
        CheckingAccount account = null;
        ResultSet resultSet = getDatabaseDriver().getCheckingAccountData(pAddress);
        try {
            String num = resultSet.getString("AccountNumber");
            int tLimit = resultSet.getInt("TransactionLimit");
            double balance = resultSet.getDouble("Balance");
            account = new CheckingAccount(pAddress, num, balance, tLimit);
        } catch (SQLException e) {
            System.out.println("getCheckingAccount() failed: " + e.getMessage());
        }
        return account;
    }

    public SavingsAccount getSavingsAccount(String pAddress) {
        SavingsAccount account = null;
        ResultSet resultSet = getDatabaseDriver().getSavingsAccountData(pAddress);
        try {
            String num = resultSet.getString("AccountNumber");
            double wLimit = resultSet.getInt("WithdrawalLimit");
            double balance = resultSet.getDouble("Balance");
            account = new SavingsAccount(pAddress, num, balance, wLimit);
        } catch (SQLException e) {
            System.out.println("getCheckingAccount() failed: " + e.getMessage());
        }
        return account;
    }

    public void clearClientData() {
        this.client.getFirstNameProperty().set("");
        this.client.getLastNameProperty().set("");
        this.client.getPayeeAddressProperty().set("");
        this.client.getDateCreatedProperty().set(null);
        this.client.getCheckingAccountProperty().set(null);
        this.client.getSavingsAccountProperty().set(null);
        this.allTransactions.clear();
        this.latestTransactions.clear();
        this.clientLoginSuccessFlag = false;
    }

    public void clearAdminData() {
        this.adminLoginSuccessFlag = false;
        getViewFactory().setLoginAccountType(AccountType.Client);
    }
}
