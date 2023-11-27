package com.fxapp.bankapp.Controllers;

import com.fxapp.bankapp.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ErrorWindowController implements Initializable {

    public Label error_lbl;
    public Button error_wdw_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String errorMsg = Model.getInstance().getErrorMsg();
        error_lbl.setText(errorMsg);
        addListener();
    }

    private void addListener() {
        error_wdw_btn.setOnAction(actionEvent -> onErrorWdwBtn());
    }

    private void onErrorWdwBtn() {
        Stage errorWdw = (Stage) error_wdw_btn.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(errorWdw);
    }
}
