package com.company;

import com.company.Conn.MariaDB;
import com.company.Constants.Color;
import com.company.Constants.Config;
import com.company.Helpers.ConfigHelper;
import com.company.Helpers.ExceptionHelper;
import com.company.Helpers.TalkerHelper;
import com.company.Models.IP;
import com.company.Models.Vulnerable;
import com.company.Tasks.AutoSSHClientTask;
import com.company.Threads.*;
import org.apache.commons.math3.util.Precision;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Main {

    public static void main(String[] args) {
        entryPoint(args);
    }

    /**
     * Entry point to starting all threads
     */
    private static void entryPoint(String[] args)
    {
        // Setup configuration variables
        ConfigHelper.setupConfig(args);

        // Print banner
        TalkerHelper.getInstance().printBanner();

        // Add CLI hook
        ConfigHelper.addCLIHook();

        // Create shared object
        IP ip = new IP();

        // Setup main threads
        Thread IPGeneratorThread = new Thread(new IPGeneratorThread(ip));
        Thread IPReaderThread = new Thread(new IPReaderThread(ip));
        Thread SSHVulnerableReporterThread = new Thread(new SSHVulnerableReporterThread());

        // Set thread names
        IPGeneratorThread.setName("IPGeneratorThread");
        IPReaderThread.setName("IPReaderThread");
        SSHVulnerableReporterThread.setName("SSHVulnerableReporterThread");

        // Setup Uncaught ExceptionHandlers
        IPGeneratorThread.setUncaughtExceptionHandler(ExceptionHelper.getUncaughtExceptionHandler());
        IPReaderThread.setUncaughtExceptionHandler(ExceptionHelper.getUncaughtExceptionHandler());
        SSHVulnerableReporterThread.setUncaughtExceptionHandler(ExceptionHelper.getUncaughtExceptionHandler());

        // Start main threads
        IPGeneratorThread.start();
        IPReaderThread.start();
        SSHVulnerableReporterThread.start();
    }
}