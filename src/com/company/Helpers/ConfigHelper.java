package com.company.Helpers;

import com.company.Constants.Config;
import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigHelper {

    /**
     * Sets up configuration variables
     */
    public static void setupConfig(String[] args)
    {
        // Setup talker helper
        setupTalkerHelper();

        // Setup program config variables
        Config.GENERATE_IP_PER_LOOP = 100;
        Config.MAX_SSH_THREADS = 3;
        Config.MAX_TELNET_THREADS = 1;
        Config.THREAD_SSH_LATENCY = 5000; // Milliseconds
        Config.THREAD_TELNET_LATENCY = 7000; // Milliseconds
        Config.DB_THREAD_DELAY = 20000;
        Config.SUPPRESS_OUTPUT = false;

        // Setup DB config variables
        Config.DB_SERVER_URL = "jdbc:mariadb://192.168.2.2/mirai";
        Config.DB_USER = "mirai";
        Config.DB_PASS = "Jf7GX3GyX92oBp4";

        // Parse the CLI arguments after setting default configuration so they can be overwritten
        ConfigHelper.parseCLIArguments(args);
    }

    /**
     * Setup TalkerHelper
     */
    private static void setupTalkerHelper()
    {
        boolean info = true;
        boolean debug = true;
        boolean error = true;
        boolean success = true;

        TalkerHelper.configure(info, debug, error, success);
    }

    /**
     * Load configuration from file
     */
    private static void loadConfig(String configFile)
    {
        Properties prop = new Properties();
        InputStream is = null;
        try {
            is = new FileInputStream(configFile);
        } catch (FileNotFoundException ex) {
            System.out.println("Configuration specified but file " + configFile + " is not found.");
            ex.printStackTrace();
            System.exit(1);
        }
        try {
            prop.load(is);
        } catch (IOException ex) {
            System.out.println("Configuration file provided but file is corrupt. Exiting.");
            ex.printStackTrace();
            System.exit(1);
        }

        String GENERATE_IP_PER_LOOP = prop.getProperty("app.GENERATE_IP_PER_LOOP");
        String MAX_SSH_THREADS = prop.getProperty("app.MAX_SSH_THREADS");
        String MAX_TELNET_THREADS = prop.getProperty("app.MAX_TELNET_THREADS");
        String THREAD_SSH_LATENCY = prop.getProperty("app.THREAD_SSH_LATENCY");
        String THREAD_TELNET_LATENCY = prop.getProperty("app.THREAD_TELNET_LATENCY");
        String DB_THREAD_DELAY = prop.getProperty("app.DB_THREAD_DELAY");
        String DB_SERVER_URL = prop.getProperty("app.DB_SERVER_URL");
        String DB_USER = prop.getProperty("app.DB_USER");
        String DB_PASS = prop.getProperty("app.DB_PASS");
        String SUPPRESS_OUTPUT = prop.getProperty("app.SUPPRESS_OUTPUT");

        try {
            if (GENERATE_IP_PER_LOOP != null) Config.GENERATE_IP_PER_LOOP = Integer.parseInt(GENERATE_IP_PER_LOOP);
            if (MAX_SSH_THREADS != null) Config.MAX_SSH_THREADS = Integer.parseInt(MAX_SSH_THREADS);
            if (MAX_TELNET_THREADS != null) Config.MAX_TELNET_THREADS = Integer.parseInt(MAX_TELNET_THREADS);
            if (THREAD_SSH_LATENCY != null) Config.THREAD_SSH_LATENCY = Integer.parseInt(THREAD_SSH_LATENCY);
            if (THREAD_TELNET_LATENCY != null) Config.THREAD_TELNET_LATENCY = Integer.parseInt(THREAD_TELNET_LATENCY);
            if (DB_THREAD_DELAY != null) Config.DB_THREAD_DELAY = Integer.parseInt(DB_THREAD_DELAY);
            if (SUPPRESS_OUTPUT != null) Config.SUPPRESS_OUTPUT = Boolean.parseBoolean(SUPPRESS_OUTPUT);
            if (DB_SERVER_URL != null) Config.DB_SERVER_URL = DB_SERVER_URL;
            if (DB_USER != null) Config.DB_USER = DB_USER;
            if (DB_PASS != null) Config.DB_PASS = DB_PASS;

        }
        catch (Exception e)
        {
            System.out.println("There was an error trying to parse the config file values. Exiting.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Add command line arguments
     */
    private static void parseCLIArguments(String[] args)
    {
        CommandLine commandLine;

        Option option_config_file = Option.builder("c")
                .required(false)
                .hasArg()
                .desc("Specifies the config file to be used.")
                .longOpt("config")
                .build();

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();

        options.addOption(option_config_file);


        try
        {
            commandLine = parser.parse(options, args);

            if (commandLine.hasOption("config"))
            {
                loadConfig(commandLine.getOptionValue("config"));
            }
        }
        catch (ParseException exception)
        {
            System.out.print("Parse error: ");
            System.out.println(exception.getMessage());
        }
    }

}
