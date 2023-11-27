package com.fxapp.bankapp.Controllers;

import com.fxapp.bankapp.Models.Model;
import com.fxapp.bankapp.Views.AccountType;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    public ChoiceBox<AccountType> acc_selector;
    public Label payee_address_lbl;
    public TextField payee_address_fld;
    public TextField password_fld;
    public Button login_btn;
    public Label error_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        acc_selector.setItems(FXCollections.observableArrayList(AccountType.Client, AccountType.Administrator));
        acc_selector.setValue(Model.getInstance().getViewFactory().getLoginAccountType());
        acc_selector.valueProperty().addListener(observable -> setAcc_selector());
        login_btn.setOnAction(actionEvent -> onLogin());
    }

    private void onLogin() {
        // Using login_btn field (or any other field) to get access to current (login) stage
        Stage loginStage = (Stage) login_btn.getScene().getWindow();

        if(Model.getInstance().getViewFactory().getLoginAccountType().equals(AccountType.Client)) {
            // Evaluate client login credentials
            String pAddress = payee_address_fld.getText();
            Model.getInstance().evaluateClientCred(pAddress, password_fld.getText());
            if(Model.getInstance().getClientLoginSuccessFlag()) {
                // Close login stage
                Model.getInstance().getViewFactory().closeStage(loginStage);
                // Show client interface window
                Model.getInstance().getViewFactory().showClientWindow();
            } else {
                payee_address_fld.setText("");
                password_fld.setText("");
                Model.getInstance().setErrorMsg("Client: No Such Login Credentials");
                Model.getInstance().getViewFactory().showErrorWindow();
            }
        } else {
            // Evaluate admin login credentials
            String username = payee_address_fld.getText();
            Model.getInstance().evaluateAdminCred(username, password_fld.getText());
            if(Model.getInstance().getAdminLoginSuccessFlag()) {
                // Close login stage
                Model.getInstance().getViewFactory().closeStage(loginStage);
                // Show admin interface window
                Model.getInstance().getViewFactory().showAdminWindow();
            } else {
                payee_address_fld.setText("");
                password_fld.setText("");
                Model.getInstance().setErrorMsg("Admin: No Such Login Credentials");
                Model.getInstance().getViewFactory().showErrorWindow();
            }
        }
    }

    private void setAcc_selector() {
        Model.getInstance().getViewFactory().setLoginAccountType(acc_selector.getValue());
        // Change Payee Address label accordingly
        if(acc_selector.getValue().equals(AccountType.Administrator)) {
            payee_address_lbl.setText("Username:");
        } else {
            payee_address_lbl.setText("Payee Address:");
        }
    }
}
