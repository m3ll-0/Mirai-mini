package com.company.Tasks;

import com.company.Constants.*;
import com.company.Helpers.SSHVulnerableValidationHelper;
import com.company.Interfaces.DAOFactory;
import com.company.Factories.VulnerableDAOFactory;
import com.company.Helpers.SSHManagerHelper;
import com.company.Helpers.TalkerHelper;
import com.company.Models.Vulnerable;
import com.company.Threads.ReporterThread;

import java.sql.Timestamp;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class AutoSSHClientTask implements Callable<Void> {

    private String server;
    private String user;
    private String pass;
    private String className;
    private TalkerHelper talkerHelper;
    private ExecutorService executorService;
    private DAOFactory daoFactory;
    private int errorCounter = 0;
    private SSHVulnerableValidationHelper sshVulnerableValidationHelper;

    public AutoSSHClientTask(String server, String user, String pass, ExecutorService executorService)
    {
        this.server = server;
        this.user = user;
        this.pass = pass;
        this.talkerHelper = TalkerHelper.getInstance();
        this.className = AutoSSHClientTask.class.getSimpleName();
        this.executorService = executorService;
        this.daoFactory = new VulnerableDAOFactory();
        this.sshVulnerableValidationHelper = new SSHVulnerableValidationHelper();
    }

    @Override
    public Void call() throws Exception {
        this.process();
        return null;
    }

    private void process()
    {
        // Check if we can authenticate
        SSHAuthResultTypes authenticationResult = this.authenticate();

        // If we can authenticate
        if(authenticationResult == SSHAuthResultTypes.AUTHENTICATED)
        {
            this.sendToReporter();

            // Statistics
            Statistics.totalVulnerableSSHServer++;

            // Shutdown remaining threads
            handleShutdown(true);
        } else if (authenticationResult == SSHAuthResultTypes.ERROR_CONNECTION_CLOSED || authenticationResult == SSHAuthResultTypes.ERROR_CONNECTION_TIMEOUT || authenticationResult == SSHAuthResultTypes.ERROR_CONNECTION_RESET || authenticationResult == SSHAuthResultTypes.ERROR_UNKNOWN ) {

            // Start error protocol
            this.handleError();
        } else if( authenticationResult == SSHAuthResultTypes.ERROR_AUTH_FAIL) {

            // Sleep for latency
            try {
                Thread.sleep(Config.THREAD_SSH_LATENCY);
            } catch (InterruptedException e) {
                talkerHelper.talkJavaError(this.className, e, ProtocolErrorTypes.SSH);
            }
        }
    }

    /**
     * Checks if authentication is true.
     */
    public SSHAuthResultTypes authenticate()
    {
        // Set password blank if password = none
        if(pass.equals("none"))
        {
            this.pass = "";
        }

        SSHManagerHelper instance = new SSHManagerHelper(user, pass, server, "");

        String errorMessage = instance.connect();

        if(errorMessage != null)
        {
            SSHAuthResultTypes SSHAuthResultType = this.handleErrorMessage(errorMessage);

            // Close session
            instance.close();

            return SSHAuthResultType;
        }
        else
        {
            // Close session
            instance.close();

            talkerHelper.talkGreatSuccess(className, Config.MESSAGE_PREDICATE_SSH+"Authentication succeeded for server " + server + " with credentials " + user + "/" + pass + "!");
            return SSHAuthResultTypes.AUTHENTICATED;
        }
    }

    /**
     * Shuts down remaining threats based on authentication result.
     *
     * @param authResult
     */
    private void handleShutdown(boolean authResult)
    {
        if (authResult) {
            talkerHelper.talkGreatDebug(className, Config.MESSAGE_PREDICATE_SSH+"Login succeeded for server "+server+". Shutting down all threads.");
        } else {

            // Statistics
            Statistics.totalConnectionFailedAndAbortedSSHServer++;

            talkerHelper.talkGreatDebug(className, Config.MESSAGE_PREDICATE_SSH+"Connection dropped too many times for server "+server+". Shutting down all threads.");
        }

        // Shut down all threads
        this.executorService.shutdownNow();
    }

    /**
     * Send details to reporter
     */
    private void sendToReporter()
    {
        Vulnerable vulnerable = new Vulnerable(server, user, pass, "SSH", new Timestamp(System.currentTimeMillis()));
        SSHVulnerableValidationHelper.insert(vulnerable);
    }

    /**
     * Protocol for error message
     * If first thread fails then why execute latter?
     * If 5th fails, do protocol?
     */
    private void handleError() {
        errorCounter++;

        if (errorCounter == 1) {
            talkerHelper.talkGreatDebug(className, Config.MESSAGE_PREDICATE_SSH + "Server " + server + " dropped the connection and is not responding. Error counter: " + errorCounter + ". Sleeping for 30 seconds.");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                talkerHelper.talkJavaError(this.className, e, ProtocolErrorTypes.SSH);
            }
        } else if (errorCounter == 2) {
            talkerHelper.talkGreatDebug(className, Config.MESSAGE_PREDICATE_SSH+"Server "+server+" dropped the connection and is not responding. Error counter: " + errorCounter + ". Sleeping for 60 seconds.");
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                talkerHelper.talkJavaError(this.className, e, ProtocolErrorTypes.SSH);
            }
        } else if (errorCounter == 3) {
            talkerHelper.talkGreatDebug(className, Config.MESSAGE_PREDICATE_SSH+"Server "+server+" dropped the connection and is not responding. Error counter: " + errorCounter + ". Sleeping for 120 seconds.");
            try {
                Thread.sleep(120000);
            } catch (InterruptedException e) {
                talkerHelper.talkJavaError(this.className, e, ProtocolErrorTypes.SSH);
            }
        } else if (errorCounter == 4) {
            talkerHelper.talkGreatError(className, Config.MESSAGE_PREDICATE_SSH+"Connection error reached " + errorCounter + " for server " + server + " with credentials " + user + "/" + pass+".");
            this.handleShutdown(false);
        }

        if(errorCounter < 4)
        {
            // Call process again
            this.process();
        }
    }

    /**
     * Returns a SSHAuthResultTypes based on the type of response
     *
     * @param errorMessage
     * @return
     */
    private SSHAuthResultTypes handleErrorMessage(String errorMessage)
    {
        if(errorMessage.equals("Auth fail"))
        {
            talkerHelper.talkGreatError(className, Config.MESSAGE_PREDICATE_SSH+"Authentication failed for server " + server + " with credentials " + user + "/" + pass+".");
            return SSHAuthResultTypes.ERROR_AUTH_FAIL;
        } else if(errorMessage.equals("connection is closed by foreign host"))
        {
            talkerHelper.talkGreatError(className, Config.MESSAGE_PREDICATE_SSH+" Server " + server + " closed connection with credentials " + user + "/" + pass+".");
            return SSHAuthResultTypes.ERROR_CONNECTION_CLOSED;
        } else if(errorMessage.equals("java.net.ConnectException: Connection timed out: connect"))
        {
            talkerHelper.talkGreatError(className, Config.MESSAGE_PREDICATE_SSH+" Server " + server + " timed out connection with credentials " + user + "/" + pass+".");
            return SSHAuthResultTypes.ERROR_CONNECTION_TIMEOUT;
        } else if (errorMessage.equals("Session.connect: java.net.SocketException: Connection reset"))
        {
            talkerHelper.talkGreatError(className, Config.MESSAGE_PREDICATE_SSH+" Server " + server + " send connection reset with credentials " + user + "/" + pass+".");
            return SSHAuthResultTypes.ERROR_CONNECTION_RESET;
        } else {
            talkerHelper.talkGreatError(className, Config.MESSAGE_PREDICATE_SSH+ "Unknown error occurred for server " + server + " with credentials " + user + "/" + pass+". Error: " + errorMessage);
            return SSHAuthResultTypes.ERROR_UNKNOWN;
        }
    }
}
