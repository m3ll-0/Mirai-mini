package com.company.Constants;

public class Config {

    // Program configuration
    public static volatile int MAX_TELNET_THREADS;
    public static volatile int MAX_SSH_THREADS;
    public static volatile int GENERATE_IP_PER_LOOP;

    public static volatile int IPSCANNER_THREADPOOL_MAX_THREADS;

    public static volatile int THREAD_SSH_LATENCY;
    public static volatile int THREAD_TELNET_SO_TIMEOUT;
    public static volatile int THREAD_SSH_SO_TIMEOUT;


    public static volatile boolean SUPPRESS_OUTPUT = false;
    public static volatile String MESSAGE_PREDICATE_SSH = "{SSH} ";
    public static volatile String MESSAGE_PREDICATE_TELNET = "{TELNET} ";

    // Database configuration
    public static volatile String MARIADB_SERVER;
    public static volatile String MARIADB_DATABASE;
    public static volatile String MARIADB_USER;
    public static volatile String MARIADB_PASS;

    public static volatile int DB_THREAD_DELAY;
}
