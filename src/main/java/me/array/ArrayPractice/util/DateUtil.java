package me.array.ArrayPractice.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    public static String formatDate(Date date) {
         /*
            This is ugly as fuck

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.getTime().toString();
        */

        SimpleDateFormat format = new SimpleDateFormat ("hh:mm:ss z 'on' dd//MM/yyyy");
        return format.format (date);
    }

    public static String getTodayDate() {
        Date todayDate = new Date();
        DateFormat todayDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        todayDateFormat.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
        String strTodayDate = todayDateFormat.format(todayDate);
        return strTodayDate;
    }

    public static String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("US/Eastern"));
        dateFormat.setTimeZone(cal.getTimeZone());
        String currentTime = dateFormat.format(cal.getTime());
        return currentTime;
    }
}
