package org.bourgedetrembleur.snmp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Ping
{
    public static boolean ping_device(String ip)
    {
        try
        {
            Process p = Runtime.getRuntime().exec("ping -n 2 -w 1000 " + ip);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while((line = br.readLine()) != null)
            {
                if(line.contains("TTL="))
                    return true;
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
