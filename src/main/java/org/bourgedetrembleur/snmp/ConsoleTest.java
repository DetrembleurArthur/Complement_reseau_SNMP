package org.bourgedetrembleur.snmp;

public class ConsoleTest
{
    public static void main(String[] args)
    {/*
        SnmpManager snmpManager = new SnmpManager();

        snmpManager.setCommunity("2326bldaWR");
        snmpManager.setIp("127.0.0.1");

        for(VariableBinding variableBinding : snmpManager.get("1.3.6.1.2.1.1.5.0", "1.3.6.1.2.1.1.1.0"))
        {
            System.err.println(variableBinding);
        }

        for(VariableBinding variableBinding : snmpManager.getNext("1.3.6.1.2.1.1.5.0", "1.3.6.1.2.1.1.1.0"))
        {
            System.err.println(variableBinding);
        }

*/








        /*try
        {
            TransportMapping transportMappings = new DefaultUdpTransportMapping();
            transportMappings.listen();

            CommunityTarget target = new CommunityTarget();
            target.setVersion(SnmpConstants.version1);
            target.setCommunity(new OctetString("2326bldaWR"));
            Address address = new UdpAddress("127.0.0.1/161");
            target.setAddress(address);
            target.setRetries(2);
            target.setTimeout(1500);

            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5.0")));
            pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.1.0")));
            pdu.setType(PDU.GETNEXT);
            Snmp snmp = new Snmp(transportMappings);

            ResponseEvent responseEvent = snmp.getNext(pdu, target);

            PDU resp = responseEvent.getResponse();
            System.err.println("Status: " + resp.getErrorStatus());
            System.err.println("Status: " + resp.getErrorStatusText());
            List list = resp.getVariableBindings();
            for(var v : list)
            {
                System.err.println("Element: " + v);
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }*/
    }
}
