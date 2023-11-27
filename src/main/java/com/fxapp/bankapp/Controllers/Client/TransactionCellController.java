package com.fxapp.bankapp.Controllers.Client;

import com.fxapp.bankapp.Models.Model;
import com.fxapp.bankapp.Models.Transaction;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class TransactionCellController implements Initializable {

    public FontAwesomeIconView in_icon;
    public FontAwesomeIconView out_icon;
    public Label trans_date_lbl;
    public Label sender_lbl;
    public Label receiver_lbl;
    public Label amount_lbl;
    public Label message_lbl;

    private final Transaction transaction;

    public TransactionCellController(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindData();
        transactionIcons();
    }

    private void bindData() {
        this.trans_date_lbl.textProperty().bind(this.transaction.getDateProperty().asString());
        this.sender_lbl.textProperty().bind(this.transaction.getSenderProperty());
        this.receiver_lbl.textProperty().bind(this.transaction.getReceiverProperty());
        this.amount_lbl.textProperty().bind(this.transaction.getAmountProperty().asString());
        this.message_lbl.textProperty().bind(this.transaction.getMessageProperty());
    }

    private void transactionIcons() {
        String pAddressCurrent = Model.getInstance().getClient().getPayeeAddressProperty().get();
        String sender = this.transaction.getSenderProperty().get();
        String receiver = this.transaction.getReceiverProperty().get();
        if(sender.equals(pAddressCurrent)) {
            this.in_icon.setFill(Color.GRAY);
            this.out_icon.setFill(Color.RED);
        }
        if(receiver.equals(pAddressCurrent)) {
            this.in_icon.setFill(Color.GREEN);
            this.out_icon.setFill(Color.GRAY);
        }
        if(sender.equals(receiver)) {
            this.in_icon.setFill(Color.GRAY);
            this.out_icon.setFill(Color.GRAY);
        }
    }
}
