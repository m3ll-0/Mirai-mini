package com.company.Helpers;

import java.util.Date;

public class TimeHelper {

    /**
     * Returns the string of difference between two dates
     *
     * @param startDate
     * @param endDate
     */
    public static long[] getRoundedDateDifference(Date startDate, Date endDate){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        
        long elapsedHours = different / hoursInMilli;
        double x =(double)   different / (double) hoursInMilli;

        different = different % hoursInMilli;


        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        return new long[]{elapsedSeconds, elapsedMinutes, elapsedHours};
    }

    /**
     * Returns the string of difference between two dates
     *
     * @param startDate
     * @param endDate
     */
    public static double[] getExactDateDifference(Date startDate, Date endDate){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        // MAKE DOUBLE AND CHECK IF devision

        double elapsedHours = (double) different / (double) hoursInMilli;
        different = different % hoursInMilli;


        double elapsedMinutes = (double)different / (double)minutesInMilli;
        different = different % minutesInMilli;

        double elapsedSeconds = (double)different / (double)secondsInMilli;

        return new double[]{elapsedSeconds, elapsedMinutes, elapsedHours};
    }
}
