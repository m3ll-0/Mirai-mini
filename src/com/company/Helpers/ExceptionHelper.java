package com.company.Helpers;

public class ExceptionHelper {

    /**
     * Returns an getUncaughtExceptionHandler so that the error within the thread can be logged
     *
     * @return
     */
    public static Thread.UncaughtExceptionHandler getUncaughtExceptionHandler()
    {
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println(t.getName() + " throws exception: " + e);
                e.printStackTrace();
                System.exit(1);
            }
        };

        return uncaughtExceptionHandler;
    }

}
