package com.company.Threads;

import com.company.Constants.Config;
import com.company.Constants.Statistics;
import com.company.Models.IP;
import com.company.Helpers.TalkerHelper;

import java.util.Random;

public class IPGeneratorThread implements Runnable {

    protected IP ip;
    protected TalkerHelper talkerHelper;

    private String className;

    public IPGeneratorThread(IP ip)
    {
        this.ip = ip;
        this.className = IPGeneratorThread.class.getSimpleName();
        this.talkerHelper = TalkerHelper.getInstance();
    }

    @Override
    public void run() {

        for(;;)
        {
            for(int c = 0; c < Config.GENERATE_IP_PER_LOOP; c++)
            {
                if(this.ip.getLength() >= 50000)
                {
                    // Reset the list to save memory
                    this.talkerHelper.talkInfo(this.className, "Resetting IP list.");
                    this.ip.reset();
                }

                int[] ip = new int[4];


                while(true)
                {
                    ip = generateIP();

                    if(ipIsValid(ip))
                        break;
                }

                // Add ip to iplist
                this.ip.add(ip[0] + "." + ip[1] + "."+ ip[2] + "." + ip[3]);
            }

            // Diagnostics
            Statistics.totalIPGenerated += Config.GENERATE_IP_PER_LOOP;

            // Sleep 3 seconds after generating 10 IP addresses
            try {
                this.talkerHelper.talkInfo(this.className, "Added " +Config.GENERATE_IP_PER_LOOP+ " IP addresses. Sleeping for 3 seconds.");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                talkerHelper.talkJavaError(className, e);
            }
        }
    }

    /**
     * Generates a random IP address and returns it.
     *
     * @return
     */
    protected int[] generateIP()
    {
        int low = 0;
        int high = 255;

        Random rand = new Random();
        int b1 = rand.nextInt(high - low) + low;
        int b2 = rand.nextInt(high - low) + low;
        int b3 = rand.nextInt(high - low) + low;
        int b4 = rand.nextInt(high - low) + low;

        return new int[]{b1, b2, b3, b4};
    }

    private boolean ipIsValid(int[] ip)
    {
        int b1 = ip[0];
        int b2 = ip[1];
        int b3 = ip[2];
        int b4 = ip[3];

        if(b1 == 127) // loopback
            return false;
        if(b1 == 0) // Invalid
            return false;
        if(b1 == 192 && b2 == 168) // Internal network
            return false;
        if( b1 == 172 && b2 >= 16 || b1 ==172 && b2 <=31 ) // Internal network
            return false;
        if(b1 == 10) // Internal network
            return false;
        if(b1 == 100 && b2 >= 64 && b2 < 127) // IANA NAT reserved
            return false;
        if(b1 == 169 && b2 > 254) // IANA NAT reserved
            return false;
        if(b1 == 198 && b2 >= 18 && b2 < 20) // IANA NAT reserved
            return false;
        if(b1 >= 224) // Multicast
            return false;

        return true;
    }
}
