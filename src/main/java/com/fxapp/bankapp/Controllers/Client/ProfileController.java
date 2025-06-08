package com.fxapp.bankapp.Controllers.Client;

import com.fxapp.bankapp.Models.Model;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    public Label first_name_lbl;
    public Label last_name_lbl;
    public Label payee_address_lbl;
    public Label date_created_lbl;
    public Label checking_account_lbl;
    public Label savings_account_lbl;
    public Label checking_balance_lbl;
    public Label savings_balance_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindClientData();
    }

    private void bindClientData() {
        // Bind client personal information
        StringProperty firstName = Model.getInstance().getClient().getFirstNameProperty();
        this.first_name_lbl.textProperty().bind(firstName);

        StringProperty lastName = Model.getInstance().getClient().getLastNameProperty();
        this.last_name_lbl.textProperty().bind(lastName);

        StringProperty payeeAddress = Model.getInstance().getClient().getPayeeAddressProperty();
        this.payee_address_lbl.textProperty().bind(payeeAddress);

        ObjectProperty<LocalDate> dateCreated = Model.getInstance().getClient().getDateCreatedProperty();
        this.date_created_lbl.textProperty().bind(dateCreated.asString());

        // Bind account information
        if (Model.getInstance().getClient().getCheckingAccountProperty().get() != null) {
            StringProperty checkingAccountNumber = 
                Model.getInstance().getClient().getCheckingAccountProperty().get().getAccountNumberProperty();
            this.checking_account_lbl.textProperty().bind(checkingAccountNumber);

            this.checking_balance_lbl.textProperty().bind(
                Model.getInstance().getClient().getCheckingAccountProperty().get().getBalanceProperty().asString());
        }

        if (Model.getInstance().getClient().getSavingsAccountProperty().get() != null) {
            StringProperty savingsAccountNumber = 
                Model.getInstance().getClient().getSavingsAccountProperty().get().getAccountNumberProperty();
            this.savings_account_lbl.textProperty().bind(savingsAccountNumber);

            this.savings_balance_lbl.textProperty().bind(
                Model.getInstance().getClient().getSavingsAccountProperty().get().getBalanceProperty().asString());
        }
    }
}