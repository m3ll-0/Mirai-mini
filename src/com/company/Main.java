package com.company;

import com.company.Conn.MariaDB;
import com.company.Constants.Config;
import com.company.Helpers.ConfigHelper;
import com.company.Helpers.ExceptionHelper;
import com.company.Helpers.TalkerHelper;
import com.company.Models.IP;
import com.company.Models.Vulnerable;
import com.company.Tasks.AutoSSHClientTask;
import com.company.Threads.IPGeneratorThread;
import com.company.Threads.IPReaderThread;
import com.company.Threads.ReporterThread;
import com.company.Threads.SSHVulnerableReporterThread;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Timestamp;
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
        TalkerHelper talkerHelper = TalkerHelper.getInstance();
        talkerHelper.printBanner();

        // If database connection fails, abort
        testDatabaseConnection();

        // Setup program
        addStatisticsHook();

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

    /**
     * Add a keylistener to console to print out debugging details
     */
    private static void addStatisticsHook()
    {
        TalkerHelper talkerHelper = TalkerHelper.getInstance();

        new Thread(() -> {

            while(true)
            {
                Scanner scanner = new Scanner(System.in);
                 scanner.nextLine();

                talkerHelper.printDiagnosticDetails();

            }
        }).start();
    }

    /**
     * Test if the database connection works, if not, abort program.
     */
    private static void testDatabaseConnection()
    {
        Connection connection = MariaDB.getMariaDBConnection();
        TalkerHelper talkerHelper = TalkerHelper.getInstance();

        if(connection == null)
        {
            talkerHelper.talkGreatError("MAIN", "Connection to DB could not be established! Aborting execution.");
            System.exit(1);
        }
        else {
            talkerHelper.talkDebug("MAIN", "Connection could be made with database. Server URL: " + Config.MARIADB_SERVER);
        }
    }
}
