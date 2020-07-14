package com.company.Threads;

import com.company.Helpers.ThreadHelper;
import com.company.Models.IP;
import com.company.Helpers.TalkerHelper;

public class IPReaderThread implements Runnable {

    protected IP ip;
    protected TalkerHelper talkerHelper;
    private String className;
    private int prevLength;

    public IPReaderThread(IP ip)
    {
        this.ip = ip;
        this.className = IPReaderThread.class.getSimpleName();
        this.talkerHelper = TalkerHelper.getInstance();
    }

    @Override
    public void run() {

        for (;;)
        {
            int curLength = this.ip.getLength();
            int indexErrorCounter = 0;

            // If the current length is equal to the previous length, skip and wait
            if(curLength == prevLength)
            {
                talkerHelper.talkInfo(this.className, "Length of IP list is the same. Skipping!");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                continue;
            }
            else
            {
                // Get offset of what needs to be processed (queue might be more usable)
                for(int offset = prevLength; offset < curLength; offset++)
                {
                    String IPAddress = null;

                    try {
                        IPAddress = ip.getAtOffset(offset);
                    }
                    catch (Exception e)
                    {
                        talkerHelper.talkJavaError(className, e);
                        indexErrorCounter++;

                        if(indexErrorCounter > 10)
                        {
                            // Reset
                            talkerHelper.talkDebug(className, "Error limit reached! Resetting IP list.");
                            continue;
                        }
                    }

                    if(IPAddress == null)
                    {
                        talkerHelper.talkDebug(className, "IP address is null for offset " + offset + ", skipping.");
                    } else {
                        talkerHelper.talkDebug(this.className, IPAddress);
//                        new Thread(new IPScannerThread(IPAddress)).start();
                        ThreadHelper.IPScannerThreadPoolExecutor.submit((new IPScannerThread(IPAddress)));
                    }
                }

                prevLength = Integer.valueOf(curLength);
            }
        }
    }
}
