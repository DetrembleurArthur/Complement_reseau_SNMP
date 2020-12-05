package org.bourgedetrembleur.snmp;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SNMPv3SecurityModel;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.net.SocketException;
import java.util.Collection;
import java.util.List;

public class SnmpManager
{
    private TransportMapping<UdpAddress> transportMapping;
    private CommunityTarget<UdpAddress> communityTarget;
    private UdpAddress address;
    private Snmp snmp;
    private SnmpListener snmpListener;

    public SnmpManager()
    {
        try
        {
            transportMapping = new DefaultUdpTransportMapping();
            transportMapping.listen();
            snmp = new Snmp(transportMapping);
            communityTarget = new CommunityTarget<>();
            communityTarget.setVersion(SnmpConstants.version1);
            communityTarget.setTimeout(1500);
            communityTarget.setRetries(2);
        } catch (SocketException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void setSnmpListener(SnmpListener snmpListener)
    {
        this.snmpListener = snmpListener;
    }

    public void setCommunity(String community)
    {
        communityTarget.setCommunity(new OctetString(community));
    }

    public void setIp(String ip)
    {
        address = new UdpAddress(ip + "/161");
        communityTarget.setAddress(address);
    }

    public List<? extends VariableBinding> get(Collection<OID> oids) throws Exception
    {
        PDU pdu = new PDU();
        for(OID oid : oids)
        {
            pdu.add(new VariableBinding(oid));
        }
        pdu.setType(PDU.GET);

        try
        {
            ResponseEvent responseEvent = snmp.get(pdu, communityTarget);
            if(responseEvent == null)
                throw new Exception(address.getInetAddress().getHostAddress() + " is not reachable");
            PDU resp = responseEvent.getResponse();
            if(resp == null)
                throw new Exception(address.getInetAddress().getHostAddress() + " is not reachable");
            return resp.getVariableBindings();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public List<? extends VariableBinding> getNext(Collection<OID> oids) throws Exception
    {
        PDU pdu = new PDU();
        for(OID oid : oids)
        {
            pdu.add(new VariableBinding(oid));
        }
        pdu.setType(PDU.GETNEXT);

        try
        {
            ResponseEvent responseEvent = snmp.getNext(pdu, communityTarget);
            if(responseEvent == null)
                throw new Exception(address.getInetAddress().getHostAddress() + " is not reachable");
            PDU resp = responseEvent.getResponse();
            if(resp == null)
                throw new Exception(address.getInetAddress().getHostAddress() + " is not reachable");
            var variables = resp.getVariableBindings();

            int i = 0;
            for(var oid : oids)
            {
                System.err.println(variables.get(i).getOid());
                oid.setValue(variables.get(i).getOid().getValue());
                i++;
            }
            return resp.getVariableBindings();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void getAsync(Collection<OID> oids)
    {
        PDU pdu = new PDU();
        for(OID oid : oids)
        {
            pdu.add(new VariableBinding(oid));
        }
        pdu.setType(PDU.GET);


        try
        {
            snmp.send(pdu, communityTarget, null, snmpListener);


        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void getNextAsync(Collection<OID> oids)
    {
        PDU pdu = new PDU();
        for(OID oid : oids)
        {
            pdu.add(new VariableBinding(oid));
        }
        pdu.setType(PDU.GETNEXT);

        try
        {
            snmp.send(pdu, communityTarget, null, snmpListener);


        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public List<? extends VariableBinding> set(OID oid, String value) throws Exception
    {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(oid, new OctetString(value)));
        pdu.setType(PDU.SET);

        try
        {
            ResponseEvent responseEvent = snmp.set(pdu, communityTarget);
            if(responseEvent == null)
                throw new Exception(address.getInetAddress().getHostAddress() + " is not reachable");
            PDU resp = responseEvent.getResponse();
            if(resp == null)
                throw new Exception(address.getInetAddress().getHostAddress() + " is not reachable");
            return resp.getVariableBindings();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }


    public void setTimeout(int timeout)
    {
        System.err.println(timeout);
        communityTarget.setTimeout(timeout);
    }

    public void setRetries(int retries)
    {
        System.err.println(retries);
        communityTarget.setRetries(retries);
    }


}
