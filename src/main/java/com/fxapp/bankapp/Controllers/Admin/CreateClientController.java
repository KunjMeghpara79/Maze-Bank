package com.fxapp.bankapp.Controllers.Admin;

import com.fxapp.bankapp.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.LocalDate;
import java.util.Random;
import java.util.ResourceBundle;

public class CreateClientController implements Initializable {

    private static final int TRANSACTION_LIMIT = 10;
    private static final double WITHDRAWAL_LIMIT = 2000.00;

    public TextField fName_fld;
    public TextField lName_fld;
    public TextField password_fld;
    public CheckBox pAddress_box;
    public Label pAddress_lbl;
    public CheckBox ch_acc_box;
    public TextField ch_amount_fld;
    public CheckBox sv_acc_box;
    public TextField sv_amount_fld;
    public Button create_client_btn;
    public Label error_lbl;

    private String payeeAddress = "empty";
    private boolean createCheckingAccountFlag = false;
    private boolean createSavingsAccountFlag = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pAddress_box.selectedProperty().addListener((observableValue, oldVal, newVal) -> {
            if(newVal) {
                createPayeeAddress();
                showPayeeAddress();
            }
        });
        ch_acc_box.selectedProperty().addListener((observableValue, oldVal, newVal) -> {
            if(newVal) {
                this.createCheckingAccountFlag = true;
            }
        });
        sv_acc_box.selectedProperty().addListener((observableValue, oldVal, newVal) -> {
            if(newVal) {
                this.createSavingsAccountFlag = true;
            }
        });
        create_client_btn.setOnAction(actionEvent -> createClient());
    }

    private void createPayeeAddress() {
        if(!this.fName_fld.getText().isEmpty() && !this.lName_fld.getText().isEmpty()) {
            int lastId = Model.getInstance().getDatabaseDriver().getLastClientsId();
            if(lastId == -1) {
                Model.getInstance().setErrorMsg("Couldn't retrieve last client ID!");
                Model.getInstance().getViewFactory().showErrorWindow();
                return;
            }
            int id = lastId + 1;
            char fChar = Character.toLowerCase(this.fName_fld.getText().charAt(0));
            this.payeeAddress = "@" + fChar + this.lName_fld.getText() + id;
        } else {
            this.pAddress_box.setSelected(false);
            Model.getInstance().setErrorMsg("First or last name missing!");
            Model.getInstance().getViewFactory().showErrorWindow();
        }
    }

    private void showPayeeAddress() {
        this.pAddress_lbl.setText(this.payeeAddress);
    }

    private void createAccount(String accountType) {
        double balance = Double.parseDouble(ch_amount_fld.getText());
        // Generate account number
        String accountNumber = Integer.toString((new Random()).nextInt(9999) + 1000);
        if(accountType.equals("Checking")) {
            // Create checking account
            Model.getInstance().getDatabaseDriver().createCheckingAccount(
                    payeeAddress, accountNumber, TRANSACTION_LIMIT, balance);
        } else {
            // Create savings account
            Model.getInstance().getDatabaseDriver().createSavingsAccount(
                    payeeAddress, accountNumber, WITHDRAWAL_LIMIT, balance);
        }
    }

    private void createClient() {
        if(this.fName_fld.getText().isEmpty() || this.lName_fld.getText().isEmpty()) {
            Model.getInstance().setErrorMsg("First or last name missing!");
            Model.getInstance().getViewFactory().showErrorWindow();
            return;
        }
        if(this.password_fld.getText().isEmpty()) {
            Model.getInstance().setErrorMsg("Password missing!");
            Model.getInstance().getViewFactory().showErrorWindow();
            return;
        }
        if(!this.pAddress_box.isSelected()) {
            Model.getInstance().setErrorMsg("Tick Payee Address checkbox!");
            Model.getInstance().getViewFactory().showErrorWindow();
            return;
        }
        if(this.createCheckingAccountFlag) {
            createAccount("Checking");
        }
        if(this.createSavingsAccountFlag) {
            createAccount("Savings");
        }
        String fName = this.fName_fld.getText();
        String lName = this.lName_fld.getText();
        String password = this.password_fld.getText();
        LocalDate date = LocalDate.now();
        Model.getInstance().getDatabaseDriver().createClient(fName, lName, this.payeeAddress, password, date);
        Model.getInstance().setErrorMsg("Client created successfully!");
        Model.getInstance().getViewFactory().showErrorWindow();
        emptyFields();
    }

    private void emptyFields() {
        this.fName_fld.setText("");
        this.lName_fld.setText("");
        this.password_fld.setText("");
        this.pAddress_box.setSelected(false);
        this.pAddress_lbl.setText("");
        this.ch_acc_box.setSelected(false);
        this.ch_amount_fld.setText("");
        this.sv_acc_box.setSelected(false);
        this.sv_amount_fld.setText("");
    }
}
