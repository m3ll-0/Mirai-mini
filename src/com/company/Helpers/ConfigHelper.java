package com.company.Helpers;

import com.company.Conn.MariaDB;
import com.company.Constants.Color;
import com.company.Constants.Config;
import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;
import java.util.Scanner;

public class ConfigHelper {

    /**
     * Sets up configuration variables
     */
    public static void setupConfig(String[] args)
    {
        // Setup talker helper
        setupTalkerHelper();

        // Parse the CLI arguments after setting default configuration so they can be overwritten
        parseCLIArguments(args);

        // If database connection fails, abort
        testDatabaseConnection();
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
        String THREAD_SSH_SO_TIMEOUT = prop.getProperty("app.THREAD_SSH_SO_TIMEOUT");
        String THREAD_TELNET_SO_TIMEOUT = prop.getProperty("app.THREAD_TELNET_SO_TIMEOUT");
        String DB_THREAD_DELAY = prop.getProperty("app.DB_THREAD_DELAY");

        String MARIADB_SERVER = prop.getProperty("app.MARIADB_SERVER");
        String MARIADB_DATABASE = prop.getProperty("app.MARIADB_DATABASE");
        String MARIADB_USER = prop.getProperty("app.MARIADB_USER");
        String MARIADB_PASS = prop.getProperty("app.MARIADB_PASS");

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
            if (MARIADB_SERVER != null) Config.MARIADB_SERVER = MARIADB_SERVER; else throw new UnsupportedOperationException();
            if (MARIADB_DATABASE != null) Config.MARIADB_DATABASE = MARIADB_DATABASE; else throw new UnsupportedOperationException();
            if (MARIADB_USER != null) Config.MARIADB_USER = MARIADB_USER; else throw new UnsupportedOperationException();
            if (MARIADB_PASS != null) Config.MARIADB_PASS = MARIADB_PASS; else throw new UnsupportedOperationException();
            if (THREAD_SSH_SO_TIMEOUT != null) Config.THREAD_SSH_SO_TIMEOUT = Integer.parseInt(THREAD_SSH_SO_TIMEOUT); else throw new UnsupportedOperationException();
        }
        catch (UnsupportedOperationException e)
        {
            System.out.println("One or more values were not present in configuration file. See README for the appropriate configuration variables. Exiting.");
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
            System.exit(1);
        }
    }

    /**
     * Add a keylistener to console to print out debugging details
     */
    public static void addCLIHook()
    {
        TalkerHelper talkerHelper = TalkerHelper.getInstance();

        new Thread(() -> {

            while(true)
            {
                if(Config.SUPPRESS_OUTPUT) {

                    System.out.print(Color.RESET + "\nCommand: ");
                    Scanner scanner = new Scanner(System.in);

                    String command = scanner.nextLine().trim();

                    if(command.startsWith("s"))
                    {
                        String subcommand = command.substring(1);

                        if (subcommand.equals("s")) talkerHelper.printDiagnosticDetails();
                        else if (subcommand.equals("c")) talkerHelper.printConfiguration();
                        else System.out.println("Information type doesn't exist. Use 'h' command for help.");
                    }
                    else if(command.equals("v")) {
                        Config.SUPPRESS_OUTPUT = false;
                    }
                    else if (command.startsWith("c "))
                    {
                        try {
                            String[] subcommand = command.substring(2).split(" ");

                            String configParam = subcommand[0];
                            String configVal = subcommand[1];
                            boolean commandExists = true;

                            // parse param
                            if (configParam.equals("GENERATE_IP_PER_LOOP"))
                                Config.GENERATE_IP_PER_LOOP = Integer.parseInt(configVal);
                            else if (configParam.equals("MAX_SSH_THREADS"))
                                Config.MAX_SSH_THREADS = Integer.parseInt(configVal);
                            else if (configParam.equals("MAX_TELNET_THREADS"))
                                Config.MAX_TELNET_THREADS = Integer.parseInt(configVal);
                            else if (configParam.equals("THREAD_SSH_LATENCY"))
                                Config.THREAD_SSH_LATENCY = Integer.parseInt(configVal);
                            else if (configParam.equals("THREAD_SSH_SO_TIMEOUT"))
                                Config.THREAD_SSH_SO_TIMEOUT = Integer.parseInt(configVal);
                            else if (configParam.equals("THREAD_TELNET_SO_TIMEOUT"))
                                Config.THREAD_TELNET_SO_TIMEOUT = Integer.parseInt(configVal);
                            else if (configParam.equals("DB_THREAD_DELAY"))
                                Config.DB_THREAD_DELAY = Integer.parseInt(configVal);
                            else if (configParam.equals("SUPPRESS_OUTPUT"))
                                Config.SUPPRESS_OUTPUT = Boolean.parseBoolean(configVal);
                            else if (configParam.equals("IPSCANNER_THREADPOOL_MAX_THREADS"))
                                Config.IPSCANNER_THREADPOOL_MAX_THREADS = Integer.parseInt(configVal);
                            else { commandExists = false; System.out.println("Parameter ["+configParam+"] doesn't exist. Check your command."); }

                            if(commandExists) System.out.println("Successfully changed config param: " + subcommand[0] + " -> " + subcommand[1]);
                        } catch (Exception e)
                        {
                            System.out.println("Error changing configuration value. Check your command.");
                        }
                    }
                    else if (command.equals("h"))
                    {
                        System.out.println("\n- Available commands");
                        System.out.println("s (Show) - Show information. Usage: s <type>. Types: [s]tatistics, [c]onfig.");
                        System.out.println("v (Verbose) - Enable verbose mode.");
                        System.out.println("c (Change) - Change config variable. Usage: c <param> <val>");
                    }
                    else {
                        System.out.println("Command not found. Press 'h' for help.");
                    }

                } else {
                    Scanner scanner = new Scanner(System.in);
                    scanner.nextLine();

                    // Turn on surpress output
                    Config.SUPPRESS_OUTPUT = true;
                }
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
