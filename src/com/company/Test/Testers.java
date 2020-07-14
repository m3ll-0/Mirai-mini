package com.company.Test;

import com.company.Constants.DAOTypes;
import com.company.Factories.VulnerableDAOFactory;
import com.company.Helpers.IPScannerHelper;
import com.company.Helpers.TalkerHelper;
import com.company.Interfaces.DAOFactory;
import com.company.Interfaces.VulnerableDAO;
import com.company.Models.Credential;
import com.company.Models.Vulnerable;
import com.company.Tasks.AutoSSHClientTask;
import com.company.Tasks.AutoTelnetClientTask;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Testers {

    //    private static void splitter()
//    {
//        try (Stream<String> lines = Files.lines(Paths.get("C:\\Users\\mihae\\credlist.txt"), Charset.defaultCharset())) {
//            lines.forEachOrdered(line -> handle(line));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    private static void handle(String line)
//    {
//        String[] creds = line.split(":");
//        String user = creds[0];
//        String pass = creds[1];
//
//        System.out.println("credentials.put(\""+user + "\", \""+pass+"\");");
//    }


    private static void test1()
    {
        // OMITED SETUPCONFIG

        TalkerHelper talkerHelper = TalkerHelper.getInstance();

        IPScannerHelper ipScannerHelper = new IPScannerHelper();

        // Get credentials
        List<Credential> credentialList = ipScannerHelper.retrieveTelnetCredentialsMap();

        Iterator it = credentialList.iterator();

        int mapCounter = 1;
        int mapLength = credentialList.size();
        int maxThreadsCount = 1;
        String server = "171.36.147.215";

        ExecutorService executor = Executors.newFixedThreadPool(maxThreadsCount);

        while (it.hasNext()) {
            Credential credential = (Credential) it.next();

            String user = credential.getUser();
            String pass = credential.getPassword();

            talkerHelper.talkInfo("", "Starting Telnet task " + "[" + mapCounter + "/" + mapLength + "]" + " for server " + server + " with credentials: " + user + "/" + pass);
            executor.submit(new AutoTelnetClientTask(server, user, pass, executor));

            mapCounter++;
        }
    }

    private static void test3()
    {
        // OMITED SETUPCONFIG

        String server = "127.0.0.1";
        String user = "user";
        String pass = "password";

        Vulnerable vulnerable = new Vulnerable(server, user, pass, "SSH", new Timestamp(System.currentTimeMillis()));

        DAOFactory daoFactory = new VulnerableDAOFactory();

        VulnerableDAO vulnerableDAO = daoFactory.getVulnerableDAO(DAOTypes.MARIADB);
        vulnerableDAO.insert(vulnerable);
    }

    private static void test2()
    {
        // OMITED SETUPCONFIG

        TalkerHelper talkerHelper = TalkerHelper.getInstance();

//        ExecutorService executor = Executors.newFixedThreadPool(3);

        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);


        IPScannerHelper ipScannerHelper = new IPScannerHelper();

        // Get credentials
        List<Credential> credentialList = ipScannerHelper.retrieveSSHCredentialsMap();

        Iterator it = credentialList.iterator();


        int mapCounter = 1;
        int mapLength = credentialList.size();
        String server = "14.44.111.44";

        while (it.hasNext()) {
            Credential credential = (Credential) it.next();

            String user = credential.getUser();
            String pass = credential.getPassword();

            talkerHelper.talkInfo("", "Starting SSH task " + "[" + mapCounter + "/" + mapLength + "]" + " for server " + server + " with credentials: " + user + "/" + pass);
//            scheduledThreadPoolExecutor.submit(new AutoSSHClientTask(server, user, pass, scheduledThreadPoolExecutor));
//            executor.submit(new AutoSSHClientTask(server, user, pass, executor));

            mapCounter++;
        }
    }
}
