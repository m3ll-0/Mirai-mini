package com.company.Threads;

import com.company.Constants.Config;
import com.company.Helpers.IPScannerHelper;
import com.company.Helpers.SSHVulnerableValidationHelper;
import com.company.Helpers.TalkerHelper;
import com.company.Models.Credential;
import com.company.Models.Vulnerable;
import com.company.Tasks.AutoSSHClientTask;
import org.apache.commons.collections4.MultiValuedMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class AutoSSHClientThread implements Runnable{

    private TalkerHelper talkerHelper;
    private String className;
    private IPScannerHelper ipScannerHelper;
    private String server;

    public AutoSSHClientThread(String server) {
        this.server = server;
        this.talkerHelper = TalkerHelper.getInstance();
        this.className = AutoSSHClientThread.class.getSimpleName();
        this.ipScannerHelper = new IPScannerHelper();
    }

    @Override
    public void run() {
        process();
    }

    private void process()
    {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(Config.MAX_SSH_THREADS);
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);

        IPScannerHelper ipScannerHelper = new IPScannerHelper();

        List<Credential> credentialList = ipScannerHelper.retrieveSSHCredentialsMap();

        Iterator it = credentialList.iterator();

        int mapCounter = 1;
        int mapLength = credentialList.size();

        while (it.hasNext()) {

            Credential credential = (Credential) it.next();

            String user = credential.getUser();
            String pass = credential.getPassword();

            talkerHelper.talkInfo(className, "Starting SSH task " + "[" + mapCounter + "/" + mapLength + "]" + " for server " + server + " with credentials: " + user + "/" + pass);
            scheduledThreadPoolExecutor.submit(new AutoSSHClientTask(server, user, pass, scheduledThreadPoolExecutor));

            mapCounter++;
        }
    }
}
