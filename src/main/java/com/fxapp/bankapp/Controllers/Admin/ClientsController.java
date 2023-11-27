package com.fxapp.bankapp.Controllers.Admin;

import com.fxapp.bankapp.Models.Client;
import com.fxapp.bankapp.Models.Model;
import com.fxapp.bankapp.Views.ClientCellFactory;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientsController implements Initializable {
    public ListView<Client> clients_listview;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initClientsList();
        clients_listview.setItems(Model.getInstance().getClients());
        clients_listview.setCellFactory(e -> new ClientCellFactory());
    }

    // if avoids reloading data repeatedly into the list
    private void initClientsList() {
        if(Model.getInstance().getClients().isEmpty()) {
            Model.getInstance().setClients();
        }
    }
}
