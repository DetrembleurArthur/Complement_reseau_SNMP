package org.bourgedetrembleur.snmp;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.smi.OID;

public class SnmpListener implements ResponseListener
{
    private final TextArea textArea;
    private final ListView<OID> listView;

    public SnmpListener(TextArea textArea, ListView<OID> listView)
    {
        this.textArea = textArea;
        this.listView = listView;
    }

    @Override
    public void onResponse(ResponseEvent responseEvent)
    {
        ((Snmp) responseEvent.getSource()).cancel(responseEvent.getRequest(), this);

        String buffer = "";
        System.err.println("ASYNC");
        PDU resp = responseEvent.getResponse();
        if(resp == null)
        {
            Platform.runLater(() -> textArea.setText("Peer is not reachable"));
            return;
        }
        var variables = resp.getVariableBindings();

        int i = 0;
        for(var v : variables)
        {
            if(v.toString().contains("Null"))
            {
                Platform.runLater(() -> textArea.setText("OID(s) wrong" + "\n\n" + textArea.getText()));
                return;
            }
            if(responseEvent.getRequest().getType() == PDU.GETNEXT)
            {
                System.err.println("NEXT");
                listView.getItems().get(i).setValue(v.getOid().getValue());
                listView.getSelectionModel().selectFirst();
                i++;
            }
            buffer += v.toString() + "\n";
        }
        final String finalBuffer = buffer;
        System.err.println("ASYNC RECV");
        Platform.runLater(() -> textArea.setText(finalBuffer + "\n\n" + textArea.getText()));
    }
}
