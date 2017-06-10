package com.karimchehab.IIFYM.Models;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Kareem on 22-Apr-17.
 */

public class DateHelper {

    public static final String dateformat = "dd-MM-yyyy";

    /**
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @return String representing date in specified format
     */
    public static String getDate(long milliSeconds)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateformat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    /**
     * Return today's date in specified format.
     * @return String representing date in specified format
     */
    public static String getTodaysDate()
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateformat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return formatter.format(calendar.getTime());
    }

    /**
     * Return the date N days ago from today's date in specified format.
     * @return String representing date in specified format
     */
    public static StringDate getDateRelativeToToday(int relative)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateformat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();

        final long dayInMillis = 24 * 60 * 60 * 1000;
        calendar.setTimeInMillis(System.currentTimeMillis() + (relative * dayInMillis));

        final String date = formatter.format(calendar.getTime());
        final String text = pretty(relative, date);
        return new StringDate(date, text);
    }

    /**
     * Returns a pretty human readable string of a day relative to today.
     * @param relativeDate 0 today, negative before today, positive after today.
     * @param unchanged string to default to if there is no pretty print.
     * @return
     */
    private static String pretty(int relativeDate, String unchanged) {
        String result = unchanged;
        switch (relativeDate) {
            case -1:
                result = "Yesterday";
                break;
            case 0:
                result = "Today";
                break;
            case 1:
                result = "Tomorrow";
                break;
        }
        return result;
    }

    public static class StringDate {
        public final String date;
        public final String text;

        public StringDate(String date, String text) {
            this.date = date;
            this.text = text;
        }
    }
}
