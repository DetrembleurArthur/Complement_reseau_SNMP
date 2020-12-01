package org.bourgedetrembleur.snmp;

import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.smi.VariableBinding;

public class SnmpListener implements ResponseListener
{
    private SnmpManager snmpManager;

    public SnmpListener(SnmpManager snmpManager)
    {
        this.snmpManager = snmpManager;
    }

    @Override
    public void onResponse(ResponseEvent responseEvent)
    {
        ((Snmp) responseEvent.getSource()).cancel(responseEvent.getRequest(), this);

        PDU pdu = responseEvent.getResponse();
        for(VariableBinding variableBinding : pdu.getVariableBindings())
        {
            System.err.println(variableBinding);
        }
        synchronized (snmpManager)
        {
            snmpManager.notify();
        }
    }
}
