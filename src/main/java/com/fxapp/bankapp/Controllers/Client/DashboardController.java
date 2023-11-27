package com.fxapp.bankapp.Controllers.Client;

import com.fxapp.bankapp.Models.Model;
import com.fxapp.bankapp.Models.Transaction;
import com.fxapp.bankapp.Views.TransactionCellFactory;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    public Text user_name;
    public Label login_date;
    public Label checking_balance;
    public Label checking_acc_num;
    public Label savings_bal;
    public Label savings_acc_num;
    public Label income_lbl;
    public Label expense_lbl;
    public ListView<Transaction> transaction_listview;
    public TextField payee_fld;
    public TextField amount_fld;
    public TextArea message_fld;
    public Button send_money_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindData();
        initLatestTransactionsList();
        this.transaction_listview.setItems(Model.getInstance().getLatestTransactions());
        this.transaction_listview.setCellFactory(e -> new TransactionCellFactory());
        this.send_money_btn.setOnAction(actionEvent -> onSendMoney());
    }

    private void bindData() {
        StringProperty firstNameProp = Model.getInstance().getClient().getFirstNameProperty();
        this.user_name.textProperty().bind(Bindings.concat("Hi, ", firstNameProp));

        this.login_date.setText("Today, " + LocalDate.now());

        DoubleProperty ch_balance =
                Model.getInstance().getClient().getCheckingAccountProperty().get().getBalanceProperty();
        this.checking_balance.textProperty().bind(ch_balance.asString());

        StringProperty ch_num =
                Model.getInstance().getClient().getCheckingAccountProperty().get().getAccountNumberProperty();
        this.checking_acc_num.textProperty().bind(ch_num);

        DoubleProperty sv_balance =
                Model.getInstance().getClient().getSavingsAccountProperty().get().getBalanceProperty();
        this.savings_bal.textProperty().bind(sv_balance.asString());

        StringProperty sv_num =
                Model.getInstance().getClient().getSavingsAccountProperty().get().getAccountNumberProperty();
        this.savings_acc_num.textProperty().bind(sv_num);

        DoubleProperty expenses = getExpenses();
        this.expense_lbl.textProperty().bind(Bindings.concat("-", expenses.asString()));

        DoubleProperty income = getIncome();
        this.income_lbl.textProperty().bind(Bindings.concat("+", income.asString()));
    }

    private void initLatestTransactionsList() {
        if(Model.getInstance().getLatestTransactions().isEmpty()) {
            Model.getInstance().setLatestTransactions();
        }
    }

    private void onSendMoney() {
        String receiver = payee_fld.getText();
        if(receiver.isEmpty()) {
            Model.getInstance().setErrorMsg("Payee Address required!");
            Model.getInstance().getViewFactory().showErrorWindow();
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amount_fld.getText());
        } catch (Exception e) {
            Model.getInstance().setErrorMsg("Invalid amount format!");
            Model.getInstance().getViewFactory().showErrorWindow();
            return;
        }
        String message = message_fld.getText();
        String sender = Model.getInstance().getClient().getPayeeAddressProperty().get();
        ResultSet resultSet = Model.getInstance().getDatabaseDriver().searchClient(receiver);
        if(resultSet == null) {
            Model.getInstance().setErrorMsg("Client not found!");
            Model.getInstance().getViewFactory().showErrorWindow();
            return;
        }
        try {
            if(!resultSet.isBeforeFirst()) {
                Model.getInstance().setErrorMsg("Client not found!");
                Model.getInstance().getViewFactory().showErrorWindow();
                return;
            }
            boolean moneySent =
                    Model.getInstance().getDatabaseDriver().updateCheckingBalance(sender, amount, "SUBTRACT");
            if(!moneySent) {
                return;
            }
            Model.getInstance().getDatabaseDriver().updateCheckingBalance(receiver, amount, "ADD");
            // Update current instance of client (logged in client);
            double crtBalance = Model.getInstance().getDatabaseDriver().getCheckingAccountBalance(sender);
            Model.getInstance().getClient().getCheckingAccountProperty().get().setBalance(crtBalance);
            // Record new transaction
            Transaction transaction =
                    Model.getInstance().getDatabaseDriver().newTransaction(sender, receiver, amount, message);
            if(transaction == null) {
                Model.getInstance().setErrorMsg("Transaction was not recorded!");
                Model.getInstance().getViewFactory().showErrorWindow();
                return;
            }
            Model.getInstance().getLatestTransactions().add(0, transaction);
            Model.getInstance().getAllTransactions().add(0,transaction);
            // Refresh transaction list view
            this.transaction_listview.setItems(Model.getInstance().getLatestTransactions());
            // Clear fields
            this.payee_fld.setText("");
            this.amount_fld.setText("");
            this.message_fld.setText("");
        } catch (SQLException e) {
            Model.getInstance().setErrorMsg("Client not found!");
            Model.getInstance().getViewFactory().showErrorWindow();
        }
    }

    private DoubleProperty getExpenses() {
        if(Model.getInstance().getAllTransactions().isEmpty()) {
            Model.getInstance().setAllTransactions();
        }
        ObservableList<Transaction> transactions = Model.getInstance().getAllTransactions();
        String pAddressCurrent = Model.getInstance().getClient().getPayeeAddressProperty().get();
        String sender;
        String receiver;
        double expenses = 0;
        for(Transaction transaction : transactions) {
            sender = transaction.getSenderProperty().get();
            receiver = transaction.getReceiverProperty().get();
            if(sender.equals(pAddressCurrent) && !receiver.equals(sender)) {
                expenses += transaction.getAmountProperty().get();
            }
        }
        return new SimpleDoubleProperty(expenses);
    }

    private DoubleProperty getIncome() {
        if(Model.getInstance().getAllTransactions().isEmpty()) {
            Model.getInstance().setAllTransactions();
        }
        ObservableList<Transaction> transactions = Model.getInstance().getAllTransactions();
        String pAddressCurrent = Model.getInstance().getClient().getPayeeAddressProperty().get();
        String sender;
        String receiver;
        double income = 0;
        for(Transaction transaction : transactions) {
            sender = transaction.getSenderProperty().get();
            receiver = transaction.getReceiverProperty().get();
            if(receiver.equals(pAddressCurrent) && !receiver.equals(sender)) {
                income += transaction.getAmountProperty().get();
            }
        }
        return new SimpleDoubleProperty(income);
    }
}
