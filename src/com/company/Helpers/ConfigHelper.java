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
        String THREAD_TELNET_SO_TIMEOUT = prop.getProperty("app.THREAD_TELNET_SO_TIMEOUT");
        String DB_THREAD_DELAY = prop.getProperty("app.DB_THREAD_DELAY");
        String DB_SERVER_URL = prop.getProperty("app.DB_SERVER_URL");
        String DB_USER = prop.getProperty("app.DB_USER");
        String DB_PASS = prop.getProperty("app.DB_PASS");
        String SUPPRESS_OUTPUT = prop.getProperty("app.SUPPRESS_OUTPUT");
        String IPSCANNER_THREADPOOL_MAX_THREADS = prop.getProperty("app.IPSCANNER_THREADPOOL_MAX_THREADS");

        try {
            if (GENERATE_IP_PER_LOOP != null) Config.GENERATE_IP_PER_LOOP = Integer.parseInt(GENERATE_IP_PER_LOOP); else throw new UnsupportedOperationException();
            if (MAX_SSH_THREADS != null) Config.MAX_SSH_THREADS = Integer.parseInt(MAX_SSH_THREADS); else throw new UnsupportedOperationException();;
            if (IPSCANNER_THREADPOOL_MAX_THREADS != null) Config.IPSCANNER_THREADPOOL_MAX_THREADS = Integer.parseInt(IPSCANNER_THREADPOOL_MAX_THREADS); else throw new UnsupportedOperationException();
            if (MAX_TELNET_THREADS != null) Config.MAX_TELNET_THREADS = Integer.parseInt(MAX_TELNET_THREADS); else throw new UnsupportedOperationException();
            if (THREAD_SSH_LATENCY != null) Config.THREAD_SSH_LATENCY = Integer.parseInt(THREAD_SSH_LATENCY); else throw new UnsupportedOperationException();
            if (THREAD_TELNET_SO_TIMEOUT != null) Config.THREAD_TELNET_SO_TIMEOUT = Integer.parseInt(THREAD_TELNET_SO_TIMEOUT); else throw new UnsupportedOperationException();
            if (DB_THREAD_DELAY != null) Config.DB_THREAD_DELAY = Integer.parseInt(DB_THREAD_DELAY); else throw new UnsupportedOperationException();
            if (SUPPRESS_OUTPUT != null) Config.SUPPRESS_OUTPUT = Boolean.parseBoolean(SUPPRESS_OUTPUT); else throw new UnsupportedOperationException();
            if (DB_SERVER_URL != null) Config.DB_SERVER_URL = DB_SERVER_URL; else throw new UnsupportedOperationException();
            if (DB_USER != null) Config.DB_USER = DB_USER; else throw new UnsupportedOperationException();
            if (DB_PASS != null) Config.DB_PASS = DB_PASS; else throw new UnsupportedOperationException();
        }
        catch (UnsupportedOperationException e)
        {
            System.out.println("One or more values were not present in configuration file. See README for appropriate configuration variables. Exiting.");
            e.printStackTrace();
            System.exit(1);
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
                .required(true)
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
