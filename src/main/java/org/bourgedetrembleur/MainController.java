package org.bourgedetrembleur;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import org.bourgedetrembleur.snmp.Ping;
import org.bourgedetrembleur.snmp.SnmpListener;
import org.snmp4j.smi.OID;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable
{
    @FXML
    private TextField ipAgentTextField;

    @FXML
    private TextField communityTextField;

    @FXML
    private Spinner<Integer> timeoutSpinner;

    @FXML
    private Spinner<Integer> retriesSpinner;

    @FXML
    private TextField oidTextField;

    @FXML
    private CheckBox asynchronousCheckBox;

    @FXML
    private Button pingButton;

    @FXML
    private Button addOidButton;

    @FXML
    private Button getButton;

    @FXML
    private Button getNextButton;


    @FXML
    private ListView<OID> oidsListView;

    @FXML
    private TextArea resultTextArea;

    @FXML
    private TextField newValuesTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        retriesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10));
        timeoutSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1000, 5000, 1000, 100));

        App.getSnmpManager().setSnmpListener(new SnmpListener(resultTextArea));
    }


    @FXML
    public void get_Action()
    {
        add_Action();
        App.getSnmpManager().setIp(ipAgentTextField.getText());
        App.getSnmpManager().setCommunity(communityTextField.getText());
        App.getSnmpManager().setRetries(retriesSpinner.getValue());
        App.getSnmpManager().setTimeout(timeoutSpinner.getValue());
        if(!asynchronousCheckBox.isSelected())
        {
            String buffer = "";
            var variables = App.getSnmpManager().get(oidsListView.getItems());
            for(var v : variables)
            {
                buffer += v.toString() + "\n";
            }
            resultTextArea.setText(buffer + "\n\n" + resultTextArea.getText());
        }
        else
        {
            App.getSnmpManager().getAsync(oidsListView.getItems());
        }
    }

    @FXML
    public void getNext_Action()
    {
        add_Action();
        App.getSnmpManager().setIp(ipAgentTextField.getText());
        App.getSnmpManager().setCommunity(communityTextField.getText());
        App.getSnmpManager().setRetries(retriesSpinner.getValue());
        App.getSnmpManager().setTimeout(timeoutSpinner.getValue());
        if(!asynchronousCheckBox.isSelected())
        {
            String buffer = "";
            var variables = App.getSnmpManager().getNext(oidsListView.getItems());
            for(var v : variables)
            {
                buffer += v.toString() + "\n";
            }
            resultTextArea.setText(buffer + "\n\n" + resultTextArea.getText());
        }
        else
        {
            App.getSnmpManager().getNextAsync(oidsListView.getItems());
        }
    }

    @FXML
    public void set_Action()
    {
        App.getSnmpManager().setIp(ipAgentTextField.getText());
        App.getSnmpManager().setCommunity(communityTextField.getText());
        App.getSnmpManager().setRetries(retriesSpinner.getValue());
        App.getSnmpManager().setTimeout(timeoutSpinner.getValue());
        String buffer = "";
        OID oid = oidsListView.getSelectionModel().getSelectedItem();
        if(oid != null)
        {
            var variables = App.getSnmpManager().set(oid, newValuesTextField.getText());
            for (var v : variables)
            {
                buffer += v.toString() + "\n";
            }
            resultTextArea.setText(buffer + "\n\n" + resultTextArea.getText());
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "[ERROR] select an OID", ButtonType.OK);
            alert.showAndWait();
            resultTextArea.setText("[ERROR] select an OID\n" + resultTextArea.getText());
        }
    }

    @FXML
    public void clearOids_Action()
    {
        oidsListView.getItems().clear();
    }

    @FXML
    public void clearResults_Action()
    {
        resultTextArea.clear();
    }

    @FXML
    public void add_Action()
    {
        String oid = oidTextField.getText();
        if(oid != null && !oid.isBlank())
        {
            OID oid1 = new OID(oid);
            if(!oidsListView.getItems().contains(oid1))
            {
                System.err.println("ADD");
                oidsListView.getItems().add(oid1);
            }
            oidTextField.clear();
        }
    }

    @FXML
    public void ping_Action()
    {
        System.err.println("PING");
        boolean result = Ping.ping_device(ipAgentTextField.getText());
        resultTextArea.setText(ipAgentTextField.getText() + " is " + (result ? " reachable" : "not reachable") + "\n" + resultTextArea.getText());
    }
}
