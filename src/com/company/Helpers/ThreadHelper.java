package com.company.Helpers;

import com.company.Constants.Config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadHelper {
    public static ExecutorService IPScannerThreadPoolExecutor = Executors.newFixedThreadPool(Config.IPSCANNER_THREADPOOL_MAX_THREADS);
}
