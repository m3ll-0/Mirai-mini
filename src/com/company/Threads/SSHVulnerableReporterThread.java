package com.company.Threads;

import com.company.Constants.Config;
import com.company.Constants.Statistics;
import com.company.Helpers.SSHVulnerableValidationHelper;
import com.company.Helpers.TalkerHelper;
import com.company.Models.Vulnerable;

import java.util.*;
import java.util.stream.Collectors;

public class SSHVulnerableReporterThread implements Runnable {

    private String className;
    private TalkerHelper talkerHelper;

    public SSHVulnerableReporterThread() {
        this.className = SSHVulnerableValidationHelper.class.getSimpleName();
        this.talkerHelper = TalkerHelper.getInstance();
    }

    @Override
    public void run() {
        this.process();
    }

    private void process()
    {
        for(;;) {

            try {
                Thread.sleep(Config.DB_THREAD_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<Vulnerable> vulnerableList = SSHVulnerableValidationHelper.getSharedVulnerableSSHList();
            List<Vulnerable> cleanList = new ArrayList<>();

            // Continue if size appears to be empty
            if(vulnerableList.size() != 0)
            {
                talkerHelper.talkDebug(className, "Vulnerable list is not empty, size: " + vulnerableList.size());

                // Get count of each entry by IP address
                Map<String, Long> talleMap = vulnerableList.stream().collect(Collectors.groupingBy(Vulnerable::getServer, Collectors.counting()));

                // Remove false positives from list
                for (Map.Entry<String, Long> entry : talleMap.entrySet()) {
                    String server = entry.getKey();
                    Long serverCount = entry.getValue();

                    // Check if server count is 3 TODO: Check for right combo.
                    if (serverCount > 1) {
                        // Statistics
                        Statistics.totalFalsePositiveSSHServer++;

                        talkerHelper.talkDebug(className, "Detected false positive SSH server: " + server);

                        continue;
                    } else {
                        Optional<Vulnerable> matchingObject = vulnerableList.stream().filter(v -> v.getServer().equals(server)).findFirst();
                        cleanList.add(matchingObject.get());
                    }
                }

                // Insert clean vulnerables into database
                for (Vulnerable cleanVulnerable : cleanList)
                {
                    // Statistics
                    Statistics.totalCleanVulnerableSSHServer++;

                    new Thread(new ReporterThread(cleanVulnerable)).start();
                }

                // Clean global list
                SSHVulnerableValidationHelper.resetList();
            }
            else
            {
                talkerHelper.talkDebug(className, "Vulnerable list is empty");
            }
        }
    }
}
