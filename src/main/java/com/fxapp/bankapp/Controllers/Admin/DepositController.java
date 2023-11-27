package com.fxapp.bankapp.Controllers.Admin;

import com.fxapp.bankapp.Models.Client;
import com.fxapp.bankapp.Models.Model;
import com.fxapp.bankapp.Views.ClientCellFactory;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class DepositController implements Initializable {

    public TextField pAddress_fld;
    public Button search_btn;
    public ListView<Client> result_listview;
    public TextField amount_fld;
    public Button deposit_btn;

    private Client client;
    private ObservableList<Client> searchResults;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        search_btn.setOnAction(actionEvent -> onClientSearch());
        deposit_btn.setOnAction(actionEvent -> onDeposit());
    }

    private void onClientSearch() {
        String pAddress = pAddress_fld.getText();
        if(pAddress.isEmpty()) {
            Model.getInstance().setErrorMsg("Payee address missing!");
            Model.getInstance().getViewFactory().showErrorWindow();
            return;
        }
        this.searchResults = Model.getInstance().searchClient(pAddress);
        if(this.searchResults == null) {
            Model.getInstance().setErrorMsg("Client not found!");
            Model.getInstance().getViewFactory().showErrorWindow();
            return;
        }
        result_listview.setItems(this.searchResults);
        result_listview.setCellFactory(e -> new ClientCellFactory());
        this.client = this.searchResults.get(0);
    }

    private void onDeposit() {
        if(this.searchResults == null) {
            Model.getInstance().setErrorMsg("Client not found!");
            Model.getInstance().getViewFactory().showErrorWindow();
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amount_fld.getText());
        } catch (NumberFormatException e) {
            Model.getInstance().setErrorMsg("Invalid amount format!");
            Model.getInstance().getViewFactory().showErrorWindow();
            return;
        }
        if(!amount_fld.getText().isEmpty()) {
            String pAddress = this.client.getPayeeAddressProperty().get();
            Model.getInstance().getDatabaseDriver().depositSavings(pAddress, amount);
            Model.getInstance().setErrorMsg("Added " + amount + " to Savings Account.");
            Model.getInstance().getViewFactory().showErrorWindow();
        } else {
            Model.getInstance().setErrorMsg("Amount must be positive!");
            Model.getInstance().getViewFactory().showErrorWindow();
            return;
        }
        emptyFields();
    }

    private void emptyFields() {
        this.pAddress_fld.setText("");
        this.amount_fld.setText("");
    }
}
