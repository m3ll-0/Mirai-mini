package com.company.Constants;

public class Statistics {

    // IP statistics
    public static volatile long totalIPGenerated = 0;
    public static volatile long totalIPUp = 0;
    public static volatile long totalIPScanned = 0;
    public static volatile long totalIPTelnetPortsOpen = 0;
    public static volatile long totalIPSSHPortOpen = 0;

    // Telnet statistics
    public static volatile long totalVulnerableTelnetServer = 0;
    public static volatile long totalUnknownTelnetAuth = 0;
    public static volatile long totalTelnetNoAuthDirectShell = 0;
    public static volatile long totalTelnetConnectionRefused = 0;

    // SSH statistics
    public static volatile long totalVulnerableSSHServer = 0;
    public static volatile long totalConnectionFailedAndAbortedSSHServer = 0;
    public static volatile long totalCleanVulnerableSSHServer = 0;
    public static volatile long totalFalsePositiveSSHServer = 0;

}
