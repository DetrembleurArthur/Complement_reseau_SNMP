package org.bourgedetrembleur.snmp;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;

public class SnmpListener implements ResponseListener
{
    private final TextArea textArea;

    public SnmpListener(TextArea textArea)
    {
        this.textArea = textArea;
    }

    @Override
    public void onResponse(ResponseEvent responseEvent)
    {
        ((Snmp) responseEvent.getSource()).cancel(responseEvent.getRequest(), this);

        String buffer = "";
        var variables = responseEvent.getResponse().getVariableBindings();
        for(var v : variables)
        {
            buffer += v.toString() + "\n";
        }
        final String finalBuffer = buffer;
        System.err.println("ASYNC RECV");
        Platform.runLater(() -> textArea.setText(finalBuffer + "\n\n" + textArea.getText()));
    }
}
