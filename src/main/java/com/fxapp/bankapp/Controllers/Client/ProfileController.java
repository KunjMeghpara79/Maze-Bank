package com.fxapp.bankapp.Controllers.Client;

import com.fxapp.bankapp.Models.Model;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    // Header section labels
    public Label first_name_lbl;
    public Label last_name_lbl;
    public Label payee_address_lbl;
    public Label date_created_lbl;
    
    // Detail section labels
    public Label first_name_detail;
    public Label last_name_detail;
    public Label payee_address_detail;
    public Label date_created_detail;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindClientPersonalData();
    }

    private void bindClientPersonalData() {
        // Get client properties
        StringProperty firstName = Model.getInstance().getClient().getFirstNameProperty();
        StringProperty lastName = Model.getInstance().getClient().getLastNameProperty();
        StringProperty payeeAddress = Model.getInstance().getClient().getPayeeAddressProperty();
        ObjectProperty<LocalDate> dateCreated = Model.getInstance().getClient().getDateCreatedProperty();

        // Bind header section
        this.first_name_lbl.textProperty().bind(firstName);
        this.last_name_lbl.textProperty().bind(lastName);
        this.payee_address_lbl.textProperty().bind(payeeAddress);
        
        // Format and bind date for header
        if (dateCreated.get() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            String formattedDate = dateCreated.get().format(formatter);
            this.date_created_lbl.setText(formattedDate);
        }

        // Bind detail section
        this.first_name_detail.textProperty().bind(firstName);
        this.last_name_detail.textProperty().bind(lastName);
        this.payee_address_detail.textProperty().bind(payeeAddress);
        
        // Format and bind date for details section
        if (dateCreated.get() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            String formattedDate = dateCreated.get().format(formatter);
            this.date_created_detail.setText(formattedDate);
        }
    }
}