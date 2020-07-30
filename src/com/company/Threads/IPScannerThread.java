package com.company.Threads;

import com.company.Constants.Statistics;
import com.company.Helpers.IPScannerHelper;
import com.company.Helpers.TalkerHelper;

public class IPScannerThread implements Runnable{

    private final int timeOut = 3000;
    private String IPaddress;
    private TalkerHelper talkerHelper;
    private com.company.Helpers.IPScannerHelper IPScannerHelper;
    private String className;
    private int[] portList = {22, 23};

    public IPScannerThread(String IPaddress)
    {
        this.IPaddress = IPaddress;
        this.className = IPScannerThread.class.getSimpleName();
        this.talkerHelper = TalkerHelper.getInstance();
        this.IPScannerHelper = new IPScannerHelper();
    }

    @Override
    public void run() {
        this.process();
    }

    private void process()
    {
        // Check if IP address is up.
        if(this.IPScannerHelper.isIpUp(IPaddress, timeOut))
        {
            talkerHelper.talkDebug(this.className,"IP Address (" + IPaddress + ") appears to be up!");

            // Statistics
            Statistics.totalIPUp++;

            // Iterate over ports and check if ports are up
            for(int port : portList)
            {
                if(this.IPScannerHelper.isPortOpen(IPaddress, port, timeOut))
                {
                    // Port is open
                    talkerHelper.talkSuccess(this.className,"IP Address " + IPaddress + " with port " + port + " is open!");

                    if(port == 22)
                    {
                        // Statistics
                        Statistics.totalIPSSHPortOpen++;

//                        MasterThread.submitToExecutor(ThreadTypes.SSH_Pool_Thread, new AutoSSHClientThread(IPaddress));
                       new Thread(new AutoSSHClientThread(IPaddress)).start();

                        // Scan SSH port
                        talkerHelper.talkInfo(this.className, "Starting SSH thread for server" + IPaddress);
                    }
                    else if(port == 23)
                    {
                        // Statistics
                        Statistics.totalIPTelnetPortsOpen++;

                        // Start multiple treads to iterate through keypair list.
                      new Thread(new AutoTelnetClientThread(IPaddress)).start();
                      talkerHelper.talkInfo(this.className, "Starting Telnet thread for server" + IPaddress);
                    }
                }
                else
                {
                    // Port is closed
                    talkerHelper.talkError(this.className,"IP Address " + IPaddress + " with port " + port + " is closed.");
                }
            }
        }
        else
        {
            talkerHelper.talkError(this.className, "IP Address (" + IPaddress + ") appears to be down.");
            return;
        }

        // Statistics
        Statistics.totalIPScanned++;
    }

}
