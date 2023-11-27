package com.fxapp.bankapp.Views;

import com.fxapp.bankapp.Models.Model;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ViewFactory {

    private AccountType loginAccountType;

    // client views
    private final ObjectProperty<ClientMenuOptions> clientSelectedMenuItem;
    private AnchorPane dashboardView;
    private AnchorPane transactionsView;
    private AnchorPane accountsView;
    private AnchorPane profileView;

    // admin views
    private final ObjectProperty<AdminMenuOptions> adminSelectedMenuItem;
    private AnchorPane createClientView;
    private AnchorPane clientsView;
    private AnchorPane depositView;

    public ViewFactory() {
        this.loginAccountType = AccountType.Client;
        this.clientSelectedMenuItem = new SimpleObjectProperty<>();
        this.adminSelectedMenuItem = new SimpleObjectProperty<>();
    }

    public AccountType getLoginAccountType() {
        return this.loginAccountType;
    }

    public void setLoginAccountType(AccountType newType) {
        this.loginAccountType = newType;
    }

    public ObjectProperty<ClientMenuOptions> getClientSelectedMenuItem() {
        return this.clientSelectedMenuItem;
    }

    public ObjectProperty<AdminMenuOptions> getAdminSelectedMenuItem() {
        return this.adminSelectedMenuItem;
    }

    // get client views section
    public AnchorPane getDashboardView() {
        if(this.dashboardView == null) {
            try {
                this.dashboardView = new FXMLLoader(getClass().getResource("/Fxml/Dashboard.fxml")).load();
            } catch (Exception e) {
                System.out.println("Couldn't load dashboardView...");
            }
        }
        return this.dashboardView;
    }

    public AnchorPane getTransactionsView() {
        if(this.transactionsView == null) {
            try {
                this.transactionsView = new FXMLLoader(getClass().getResource("/Fxml/Transactions.fxml")).load();
            } catch (Exception e) {
                System.out.println("Couldn't load transactionView...");
            }
        }
        return this.transactionsView;
    }

    public AnchorPane getAccountsView() {
        if(this.accountsView == null) {
            try {
                this.accountsView = new FXMLLoader(getClass().getResource("/Fxml/Accounts.fxml")).load();
            } catch (Exception e) {
                System.out.println("Couldn't load accountsView...");
            }
        }
        return this.accountsView;
    }

    public AnchorPane getProfileView() {
        if(this.profileView == null) {
            try {
                this.profileView = new FXMLLoader(getClass().getResource("/Fxml/Profile.fxml")).load();
            } catch (Exception e) {
                System.out.println("Couldn't load profileView");
            }
        }
        return this.profileView;
    }

    public void showClientWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client.fxml"));
        createStage(loader);
    }

    // get admin views section
    public AnchorPane getCreateClientView() {
        if(this.createClientView == null) {
            try {
                this.createClientView = new FXMLLoader(getClass().getResource("/Fxml/CreateClient.fxml")).load();
            } catch (Exception e) {
                System.out.println("Couldn't load createClientView...");
            }
        }
        return this.createClientView;
    }

    public AnchorPane getClientsView() {
        if(this.clientsView == null) {
            try {
                this.clientsView = new FXMLLoader(getClass().getResource("/Fxml/Clients.fxml")).load();
            } catch (Exception e) {
                Model.getInstance().setErrorMsg("Couldn't load clientsView...");
                showErrorWindow();
            }
        }
        return this.clientsView;
    }

    public AnchorPane getDepositView() {
        if(this.depositView == null) {
            try {
                this.depositView = new FXMLLoader(getClass().getResource("/Fxml/Deposit.fxml")).load();
            } catch (Exception e) {
                Model.getInstance().setErrorMsg("Couldn't load depositView...");
                showErrorWindow();
            }
        }
        return this.depositView;
    }

    public void showAdminWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin.fxml"));
        createStage(loader);
    }

    public void showLoginWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        createStage(loader);
    }

    public void showErrorWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/ErrorWindow.fxml"));
        createStage(loader);
    }

    public void closeStage(Stage stage) {
        stage.close();
    }

    private void createStage(FXMLLoader loader) {
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (Exception e) {
            System.out.println("Couldn't load window...");
        }
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/Images/icon.png"))));
        stage.setResizable(false);
        stage.setTitle("DEV Bank");
        stage.show();
    }
}
