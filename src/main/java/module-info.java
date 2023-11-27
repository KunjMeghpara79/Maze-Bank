module com.fxapp.bankapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.fxapp.bankapp to javafx.fxml;
    exports com.fxapp.bankapp;
    exports com.fxapp.bankapp.Controllers;
    exports com.fxapp.bankapp.Controllers.Admin;
    exports com.fxapp.bankapp.Controllers.Client;
    exports com.fxapp.bankapp.Models;
    exports com.fxapp.bankapp.Views;
}
