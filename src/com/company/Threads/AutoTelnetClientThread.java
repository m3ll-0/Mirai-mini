package com.company.Threads;

import com.company.Models.Credential;
import com.company.Tasks.AutoTelnetClientTask;
import com.company.Constants.Config;
import com.company.Helpers.IPScannerHelper;
import com.company.Helpers.TalkerHelper;
import org.apache.commons.collections4.MultiValuedMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AutoTelnetClientThread implements Runnable {

    private TalkerHelper talkerHelper;
    private String className;
    private IPScannerHelper ipScannerHelper;
    private String server;
    private boolean isPort2323;

    public AutoTelnetClientThread(String server, boolean isPort2323) {
        this.server = server;
        this.talkerHelper = TalkerHelper.getInstance();
        this.className = AutoTelnetClientThread.class.getSimpleName();
        this.ipScannerHelper = new IPScannerHelper();
        this.isPort2323 = isPort2323;
    }

    @Override
    public void run() {
        process();
    }

    private void process()
    {
        // Get credentials
        List<Credential> credentialList = ipScannerHelper.retrieveTelnetCredentialsMap();

        Iterator it = credentialList.iterator();

        int mapCounter = 1;
        int mapLength = credentialList.size();

        ExecutorService executor = Executors.newFixedThreadPool(Config.MAX_TELNET_THREADS);

        while (it.hasNext()) {

            Credential credential = (Credential) it.next();

            String user = credential.getUser();
            String pass = credential.getPassword();

            talkerHelper.talkInfo(this.className, "Starting Telnet task " + "[" + mapCounter + "/" + mapLength + "]" + " for server " + server + " with credentials: " + user + "/" + pass);

            try {
                executor.submit(new AutoTelnetClientTask(server, user, pass, executor, isPort2323));
            }
            catch (Exception e)
            {
                talkerHelper.talkJavaError(className, e);
            }

            mapCounter++;
        }
    }
}