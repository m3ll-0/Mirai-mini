package com.company.Helpers;

import com.company.Constants.Color;
import com.company.Constants.Config;
import com.company.Constants.ProtocolErrorTypes;
import com.company.Constants.Statistics;
import org.apache.commons.net.ntp.TimeStamp;

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class TalkerHelper {

    private final boolean info;
    private final boolean debug;
    private final boolean error;
    private final boolean success;

    public static TalkerHelper talkerHelperInstance = null;

    private TalkerHelper(boolean info, boolean debug, boolean error, boolean success)
    {
        this.info = info;
        this.debug = debug;
        this.error = error;
        this.success = success;
    }

    public synchronized static TalkerHelper configure(boolean info, boolean debug, boolean error, boolean success)
    {
        if(talkerHelperInstance == null)
        {
            talkerHelperInstance = new TalkerHelper(info, debug, error, success);
            return talkerHelperInstance;
        }
        else
        {
            // Should be assertionError
            return null;
        }
    }

    public static TalkerHelper getInstance()
    {
        return talkerHelperInstance;
    }

    private boolean checkSuppressOutput()
    {
        if(Config.SUPPRESS_OUTPUT){
            return true;
        }

        return false;
    }

    public void talkDebug(String className, String message)
    {
        if (this.checkSuppressOutput()) return;

        if(this.debug)
        {
            System.out.print(Color.RESET);
            System.out.println(Color.YELLOW + "*DEBUG*  " + "[" + className + "]: " +message);
        }
    }

    public void talkGreatDebug(String className, String message)
    {
        if (this.checkSuppressOutput()) return;

        if(this.debug)
        {
            System.out.print(Color.RESET);
            System.out.println(Color.ANSI_ORANGE_BG + "" + Color.ANSI_BACK_FG + "*DEBUG*  " + "[" + className + "]: " +message);
        }
    }

    public void talkInfo(String className, String message)
    {
        if (this.checkSuppressOutput()) return;

        if(this.info)
        {
            System.out.print(Color.RESET);
            System.out.println( Color.BLUE + "*INFO* " + "[" + className + "]: " +message);
        }
    }

    public void talkError(String className, String message)
    {
        if (this.checkSuppressOutput()) return;

        if(this.error)
        {
            System.out.print(Color.RESET);
            System.out.println( Color.RED + "*ERROR* " + "[" + className + "]: " +message);
        }
    }

    public void talkGreatError(String className, String message)
    {
        if (this.checkSuppressOutput()) return;

        if(this.error)
        {
            System.out.print(Color.RESET);
            System.out.println( Color.RED_BACKGROUND_BLACK_FOREGROUND + "*ERROR* " + "[" + className + "]: " +message);
        }
    }

    public void talkJavaError(String className, Exception e)
    {
        if (this.checkSuppressOutput()) return;

        if(this.error)
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.out.print(Color.RESET);
            System.out.println( Color.MAGENTA_BACKGROUND_BLACK_FOREGROUND + "*JAVA ERROR* " + "[" + className + "] " + e.getClass().getSimpleName());
        }
    }

    public void talkJavaError(String className, Exception e, ProtocolErrorTypes protocolErrorType)
    {
        if (this.checkSuppressOutput()) return;

        if(this.error)
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.out.print(Color.RESET);
            System.out.println( Color.MAGENTA_BACKGROUND_BLACK_FOREGROUND + "*JAVA ERROR* " + "[" + className + "] {" + protocolErrorType + "} " + e.getClass().getSimpleName());
        }
    }

    public void talkSuccess(String className, String message)
    {
        if (this.checkSuppressOutput()) return;

        if(this.success)
        {
            System.out.print(Color.RESET);
            System.out.println( Color.GREEN_BOLD + "*SUCCESS* " + "[" + className + "]: " +message);
        }
    }

    public void talkGreatSuccess(String className, String message)
    {
        if (this.checkSuppressOutput()) return;

        if(this.success)
        {
            System.out.print(Color.RESET);
            System.out.println( Color.GREEN_BACKGROUND_BLACK_FOREGROUND + "*SUCCESS* " + "[" + className + "]: " +message);
        }
    }

    public void printRedBannerLine(String message)
    {
        System.out.print(Color.RESET);
        System.out.println(Color.ANSI_BLACK_BG +"" + Color.ANSI_RED_FG+message);
    }

    public void printOrangeBannerLine(String message)
    {
        System.out.print(Color.RESET);
        System.out.println(Color.ANSI_BLACK_BG +"" + Color.ANSI_ORANGE_FG+message);
    }

    public void printCyanBannerLine(String message)
    {
        System.out.print(Color.RESET);
        System.out.println(Color.ANSI_BLACK_BG +"" + Color.ANSI_CYAN_FG+message);
    }

    public void printGreenBannerLine(String message)
    {
        System.out.print(Color.RESET);
        System.out.print(Color.ANSI_BLACK_BG +"" + Color.ANSI_GREEN_FG+message);
    }

    /**
     * Print diagnostics
     */
    public void printBanner()
    {
        TalkerHelper talkerHelper = TalkerHelper.getInstance();

        talkerHelper.printRedBannerLine("   *      (      (                (     ");
        talkerHelper.printRedBannerLine(" (  `     )\\ )   )\\ )     (       )\\ )  ");
        talkerHelper.printOrangeBannerLine(" )\\))(   (()/(  (()/(     )\\     (()/(");
        talkerHelper.printCyanBannerLine("((_)()\\   /(_))  /(_)) ((((_)(    /(_))");
        System.out.println("(_()((_) (_))   (_))    )\\ _ )\\  (_))   ");
        System.out.println("|  \\/  | |_ _|  | _ \\   (_)_\\(_) |_ _|  ");
        System.out.println("| |\\/| |  | |   |   /    / _ \\    | |   ");
        System.out.print("|_|  |_| |___|  |_|_\\   /_/ \\_\\  |___|  ");
        talkerHelper.printGreenBannerLine(" ~~~ MINI \n");

        this.printConfiguration();

        System.out.println(Color.RESET + "\n");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.print("Launching program in: ");

        for(int x = 3; x > 0; x--)
        {
            System.out.print(x + ".. ");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.print("Program started!\n");
    }

    public void printConfiguration()
    {
        System.out.println("\n***** DIAGNOSTICS *****");
        System.out.println( "Available Memory: " + Runtime.getRuntime().freeMemory()+"/" +Runtime.getRuntime().maxMemory());
        System.out.println( "Available Processor: " + Runtime.getRuntime().availableProcessors());
        System.out.println("\n***** CONFIGURATION *****");
        System.out.println("MAX_TELNET_THREADS: " + Config.MAX_TELNET_THREADS);
        System.out.println("MAX_SSH_THREADS: " + Config.MAX_SSH_THREADS);
        System.out.println("IPSCANNER_THREADPOOL_MAX_THREADS: " + Config.IPSCANNER_THREADPOOL_MAX_THREADS);
        System.out.println("GENERATE_IP_PER_LOOP: " + Config.GENERATE_IP_PER_LOOP);
        System.out.println("THREAD_SSH_LATENCY: " + Config.THREAD_SSH_LATENCY);
        System.out.println("THREAD_SSH_SO_TIMEOUT: " + Config.THREAD_SSH_SO_TIMEOUT);
        System.out.println("THREAD_TELNET_SO_TIMEOUT: " + Config.THREAD_TELNET_SO_TIMEOUT);
        System.out.println("SUPPRESS_OUTPUT: " + Config.SUPPRESS_OUTPUT);
        System.out.println("DB_THREAD_DELAY: " + Config.DB_THREAD_DELAY);
        System.out.println("MARIADB_SERVER: " + Config.MARIADB_SERVER);
        System.out.println("MARIADB_DATABASE: " + Config.MARIADB_DATABASE);
        System.out.println("MARIADB_USER: " + Config.MARIADB_USER);
        System.out.println("MARIADB_PASS: " + "-Omitted-");
    }

    /**
     * Print details about diagnostics
     */
    public void printDiagnosticDetails()
    {
        // Time Statistics
        long[] dateDiffValRounded = TimeHelper.getRoundedDateDifference(Statistics.startTimeStamp, new Date());
        double[] dateDiffValExact = TimeHelper.getExactDateDifference(Statistics.startTimeStamp, new Date());

        System.out.println(Color.RESET);
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "***************************************************************************");
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "***************************************************************************");
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "STATISTICS");
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "***************************************************************************");
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "***************************************************************************");
        System.out.println();
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "[General Statistics]");
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Time elapsed: " + dateDiffValRounded[2] + " hours, " + dateDiffValRounded[1] + " minutes, " + dateDiffValRounded[0] + " seconds");
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Available Memory: " + Runtime.getRuntime().freeMemory()+"/" +Runtime.getRuntime().maxMemory());
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Available Processor: " + Runtime.getRuntime().availableProcessors());
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Total number of threads: " + Thread.getAllStackTraces().keySet().size());
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Number of threads currently running: " + this.getRunningThreads());
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "---------------------------------------------------------------------------------");
        System.out.println();
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "[IP Statistics]");
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Total number of IP's generated: " + Statistics.totalIPGenerated);
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Total number of IP's up: " + Statistics.totalIPUp);
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Total number of IP's scanned: " + Statistics.totalIPScanned);
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Average IP's scans per hour: " + ( dateDiffValExact[2] == 0 ? Statistics.totalIPScanned : ( (double) Statistics.totalIPScanned / dateDiffValExact[2])));
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Total number of IP's found with with Telnet port open: " + Statistics.totalIPTelnetPortsOpen);
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Total number of IP's found with with SSH port open: " + Statistics.totalIPSSHPortOpen);
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "---------------------------------------------------------------------------------");
        System.out.println();
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "[Telnet Statistics]");
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Total number of vulnerable Telnet servers: " + Statistics.totalVulnerableTelnetServer);
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Total number of Telnet servers with direct shell access (no auth): " + Statistics.totalTelnetNoAuthDirectShell);
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Total number of unknown authentication Telnet servers: " + Statistics.totalUnknownTelnetAuth);
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Total number of refused connections for Telnet servers: " + Statistics.totalTelnetConnectionRefused);
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Average vulnerable Telnet servers per hour: " + ( dateDiffValExact[2] == 0 ? Statistics.totalVulnerableTelnetServer : ( (double) Statistics.totalVulnerableTelnetServer / dateDiffValExact[2])));
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "---------------------------------------------------------------------------------");
        System.out.println();
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "[SSH Statistics]");
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Total number of clean vulnerable SSH servers: " + Statistics.totalCleanVulnerableSSHServer);
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Total number of (unchecked) vulnerable SSH servers : " + Statistics.totalVulnerableSSHServer);
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Total number of pending vulnerable SSH servers: " + SSHVulnerableValidationHelper.getSharedVulnerableSSHList().size());
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Total number of false positive vulnerable SSH servers: " + Statistics.totalFalsePositiveSSHServer);
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Total number of aborted by SSH servers: " + Statistics.totalConnectionFailedAndAbortedSSHServer);
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "Average vulnerable SSH servers per hour: " + ( dateDiffValExact[2] == 0 ? Statistics.totalCleanVulnerableSSHServer : ( (double) Statistics.totalCleanVulnerableSSHServer / (double) dateDiffValExact[2])));
        System.out.println();
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "***************************************************************************");
        System.out.println(Color.YELLOW_BACKGROUND_BLACK_FOREGROUND + "***************************************************************************");
    }

    /**
     * Helper method to get running threads for diagnostics
     * @return
     */
    private int getRunningThreads()
    {
        int nbRunning = 0;
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getState()==Thread.State.RUNNABLE) nbRunning++;
        }
        return nbRunning;
    }

}
