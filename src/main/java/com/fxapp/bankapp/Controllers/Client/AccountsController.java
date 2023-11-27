package com.fxapp.bankapp.Controllers.Client;

import com.fxapp.bankapp.Models.CheckingAccount;
import com.fxapp.bankapp.Models.Model;
import com.fxapp.bankapp.Models.SavingsAccount;
import com.fxapp.bankapp.Models.Transaction;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AccountsController implements Initializable {
    public Label ch_acc_num;
    public Label transaction_limit;
    public Label ch_acc_date;
    public Label ch_acc_bal;
    public Label sv_acc_num;
    public Label withdrawal_limit;
    public Label sv_acc_date;
    public Label sv_acc_bal;
    public TextField amount_to_sv;
    public Button trans_to_sv_btn;
    public TextField amount_to_ch;
    public Button trans_to_ch_btn;

    private final StringProperty pAddress = Model.getInstance().getClient().getPayeeAddressProperty();
    private enum transferTo {
        SAVINGS,
        CHECKING
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindData();
        this.trans_to_ch_btn.setOnAction(actionEvent -> onTransfer(transferTo.CHECKING));
        this.trans_to_sv_btn.setOnAction(actionEvent -> onTransfer(transferTo.SAVINGS));
    }

    private void bindData() {
        StringProperty ch_num =
                Model.getInstance().getClient().getCheckingAccountProperty().get().getAccountNumberProperty();
        this.ch_acc_num.textProperty().bind(ch_num);

        CheckingAccount checkingAccount = Model.getInstance().getCheckingAccount(this.pAddress.get());
        IntegerProperty tLimit = checkingAccount.getTransactionLimitProp();
        this.transaction_limit.textProperty().bind(tLimit.asString());

        ObjectProperty<LocalDate> date = Model.getInstance().getClient().getDateCreatedProperty();
        this.ch_acc_date.textProperty().bind(date.asString());
        this.sv_acc_date.textProperty().bind(date.asString());

        DoubleProperty ch_balance =
                Model.getInstance().getClient().getCheckingAccountProperty().get().getBalanceProperty();
        this.ch_acc_bal.textProperty().bind(ch_balance.asString());

        StringProperty sv_num =
                Model.getInstance().getClient().getSavingsAccountProperty().get().getAccountNumberProperty();
        this.sv_acc_num.textProperty().bind(sv_num);

        SavingsAccount savingsAccount = Model.getInstance().getSavingsAccount(this.pAddress.get());
        DoubleProperty wLimit = savingsAccount.getWithdrawLimitProp();
        this.withdrawal_limit.textProperty().bind(wLimit.asString());

        DoubleProperty sv_balance =
                Model.getInstance().getClient().getSavingsAccountProperty().get().getBalanceProperty();
        this.sv_acc_bal.textProperty().bind(sv_balance.asString());
    }

    private void onTransfer(transferTo target) {
        double amount = 0;
        String pAddress = this.pAddress.get();
        if(target.equals(transferTo.CHECKING)) {
            try {
                amount = Double.parseDouble(this.amount_to_ch.getText());
            } catch (Exception e) {
                Model.getInstance().setErrorMsg("Invalid amount format!");
                Model.getInstance().getViewFactory().showErrorWindow();
                return;
            }
            boolean fromSavings =
                    Model.getInstance().getDatabaseDriver().updateSavingsBalance(pAddress, amount, "SUBTRACT");
            if(!fromSavings) {
                Model.getInstance().setErrorMsg("Transaction failed!");
                Model.getInstance().getViewFactory().showErrorWindow();
                return;
            }
            boolean toChecking =
                    Model.getInstance().getDatabaseDriver().updateCheckingBalance(pAddress, amount, "ADD");
            if(!toChecking) {
                Model.getInstance().setErrorMsg("Transaction failed!");
                Model.getInstance().getViewFactory().showErrorWindow();
                return;
            }
        }
        if(target.equals(transferTo.SAVINGS)) {
            try {
                amount = Double.parseDouble(this.amount_to_sv.getText());
            } catch (Exception e) {
                Model.getInstance().setErrorMsg("Invalid amount format!");
                Model.getInstance().getViewFactory().showErrorWindow();
                return;
            }
            boolean fromChecking =
                    Model.getInstance().getDatabaseDriver().updateCheckingBalance(pAddress, amount, "SUBTRACT");
            if(!fromChecking) {
                Model.getInstance().setErrorMsg("Transaction failed!");
                Model.getInstance().getViewFactory().showErrorWindow();
                return;
            }
            boolean toSavings =
                    Model.getInstance().getDatabaseDriver().updateSavingsBalance(pAddress, amount, "ADD");
            if(!toSavings) {
                Model.getInstance().setErrorMsg("Transaction failed!");
                Model.getInstance().getViewFactory().showErrorWindow();
                return;
            }
        }
        // Update current instance of client (logged in client);
        double newSavings = Model.getInstance().getDatabaseDriver().getSavingsAccountBalance(pAddress);
        if(newSavings == -1) {
            Model.getInstance().setErrorMsg("Transaction failed!");
            Model.getInstance().getViewFactory().showErrorWindow();
            return;
        }
        Model.getInstance().getClient().getSavingsAccountProperty().get().setBalance(newSavings);
        double newChecking = Model.getInstance().getDatabaseDriver().getCheckingAccountBalance(pAddress);
        if(newChecking == -1) {
            Model.getInstance().setErrorMsg("Transaction failed!");
            Model.getInstance().getViewFactory().showErrorWindow();
            return;
        }
        Model.getInstance().getClient().getCheckingAccountProperty().get().setBalance(newChecking);
        // Record transaction
        Transaction transaction =
                Model.getInstance().getDatabaseDriver().newTransaction(
                        pAddress, pAddress, amount, "Internal transfer");
        if(transaction == null) {
            Model.getInstance().setErrorMsg("Transaction was not recorded!");
            Model.getInstance().getViewFactory().showErrorWindow();
            return;
        }
        Model.getInstance().getLatestTransactions().add(0, transaction);
        Model.getInstance().getAllTransactions().add(0,transaction);
        // Empty text fields
        this.amount_to_ch.setText("");
        this.amount_to_sv.setText("");
    }
}
