package org.bourgedetrembleur;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.bourgedetrembleur.snmp.Ping;
import org.bourgedetrembleur.snmp.SnmpListener;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

    @FXML
    private ListView<OID> oidRegistryListView;

    @FXML
    private Tab oidTab;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        retriesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10));
        timeoutSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1000, 5000, 1000, 100));

        App.getSnmpManager().setSnmpListener(new SnmpListener(resultTextArea, oidsListView));


    }

    private void error(String message)
    {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING, "[ERROR] " + message, ButtonType.OK);
            alert.showAndWait();
            resultTextArea.setText("[ERROR] "+ message + "\n" + resultTextArea.getText());
        });
    }

    boolean searching = false;
    OID last = new OID("1");
    @FXML
    public ToggleButton fromBeginToggleButton;

    @FXML
    private void generateMibBrowser()
    {
        updateInfos();
        oidRegistryListView.getItems().clear();

        if(!searching)
        {
            if(fromBeginToggleButton.isSelected())
                last = new OID("1");
            new Thread(() -> {
                searching = true;
                ArrayList<OID> oids = new ArrayList<>();
                OID tmp = last;
                oids.add(tmp);
                try
                {
                    String soid = last.toDottedString();
                    int i = 0;
                    while (i < 500)
                    {
                        App.getSnmpManager().getNext(oids);

                        if (soid.equals(oids.get(0).toString()))
                            break;
                        soid = oids.get(0).toString();
                        String finalSoid = soid;
                        Platform.runLater(() -> {
                            if (!oidRegistryListView.getItems().contains(oids.get(0)))
                                oidRegistryListView.getItems().add(new OID(finalSoid));
                        });
                        i++;
                    }

                } catch (Exception e)
                {
                    error(e.getMessage());
                }
                searching = false;
            }).start();
        }

    }

    private void updateInfos()
    {
        try
        {
            App.getSnmpManager().setIp(ipAgentTextField.getText());
            App.getSnmpManager().setCommunity(communityTextField.getText());
            App.getSnmpManager().setRetries(retriesSpinner.getValue());
            App.getSnmpManager().setTimeout(timeoutSpinner.getValue());
        }
        catch(RuntimeException e)
        {
            error(e.getMessage());
        }
    }

    @FXML
    public void get_Action()
    {
        add_Action();
        updateInfos();
        if(!asynchronousCheckBox.isSelected())
        {
            try
            {
                var variables = App.getSnmpManager().get(oidsListView.getItems());
                updateResults(variables);
            }
            catch(Exception e)
            {
                error(e.getMessage());
            }
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
        updateInfos();
        if(!asynchronousCheckBox.isSelected())
        {
            try
            {
                var variables = App.getSnmpManager().getNext(oidsListView.getItems());
                updateResults(variables);
                oidsListView.getSelectionModel().selectFirst();
            }
            catch(Exception e)
            {
                error(e.getMessage());
            }
        }
        else
        {
            App.getSnmpManager().getNextAsync(oidsListView.getItems());
        }
    }

    private void updateResults(List<? extends VariableBinding> variables)
    {
        String buffer = "";
        for(var v : variables)
        {
            if(v.toString().contains("Null"))
            {
                error("OID(s) wrong");
                return;
            }
            buffer += v.toString() + "\n";
        }
        resultTextArea.setText(buffer + "\n\n" + resultTextArea.getText());
    }

    @FXML
    public void set_Action()
    {
        updateInfos();
        OID oid = oidsListView.getSelectionModel().getSelectedItem();
        if(oid != null)
        {
            try
            {
                if(newValuesTextField.getText().isBlank())
                    throw new Exception("set values is blank...");
                var variables = App.getSnmpManager().set(oid, newValuesTextField.getText());
                updateResults(variables);
            }
            catch(Exception e)
            {
                error(e.getMessage());
            }
        }
        else
        {
            error("select an OID");
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
        if(oidTab.isSelected())
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
        else
        {
            OID oid = oidRegistryListView.getSelectionModel().getSelectedItem();
            if(oid != null)
            {
                if(!oidsListView.getItems().contains(oid))
                    oidsListView.getItems().add(new OID(oid.getValue()));
            }
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
