package me.array.ArrayPractice.util;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil
{
    private static final String HOUR_FORMAT = "%02d:%02d:%02d";
    private static final String MINUTE_FORMAT = "%02d:%02d";

    public static String now() {
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    public static long nowlong() {
        return System.currentTimeMillis();
    }

    public static String when(final long time) {
        final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        return sdf.format(time);
    }

    public static String millisToTimer(final long millis) {
        final long seconds = millis / 1000L;
        if (seconds > 3600L) {
            return String.format("%02d:%02d:%02d", seconds / 3600L, seconds % 3600L / 60L, seconds % 60L);
        }
        return String.format("%02d:%02d", seconds / 60L, seconds % 60L);
    }

    public static String millisToSeconds(final long millis) {
        return new DecimalFormat("#0.0").format(millis / 1000.0f);
    }

    public static long a(final String a) {
        if (a.endsWith("s")) {
            return Long.valueOf(a.substring(0, a.length() - 1)) * 1000L;
        }
        if (a.endsWith("m")) {
            return Long.valueOf(a.substring(0, a.length() - 1)) * 60000L;
        }
        if (a.endsWith("h")) {
            return Long.valueOf(a.substring(0, a.length() - 1)) * 3600000L;
        }
        if (a.endsWith("d")) {
            return Long.valueOf(a.substring(0, a.length() - 1)) * 86400000L;
        }
        if (a.endsWith("m")) {
            return Long.valueOf(a.substring(0, a.length() - 1)) * 2592000000L;
        }
        if (a.endsWith("y")) {
            return Long.valueOf(a.substring(0, a.length() - 1)) * 31104000000L;
        }
        return -1L;
    }

    public static String date() {
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }

    public static String getTime(final int time) {
        Date timeDiff = new Date();
        timeDiff.setTime(time * 1000);
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
        String eventTimeDisplay = timeFormat.format(timeDiff);
        return eventTimeDisplay;
    }

    public static String since(final long epoch) {
        return "Took " + convertString(System.currentTimeMillis() - epoch, 1, TimeUnit.FIT) + ".";
    }

    public static double convert(final long time, final int trim, TimeUnit type) {
        if (type == TimeUnit.FIT) {
            type = ((time < 60000L) ? TimeUnit.SECONDS : ((time < 3600000L) ? TimeUnit.MINUTES : ((time < 86400000L) ? TimeUnit.HOURS : TimeUnit.DAYS)));
        }
        if (type == TimeUnit.DAYS) {
            return UtilMath.trim(trim, time / 8.64E7);
        }
        if (type == TimeUnit.HOURS) {
            return UtilMath.trim(trim, time / 3600000.0);
        }
        if (type == TimeUnit.MINUTES) {
            return UtilMath.trim(trim, time / 60000.0);
        }
        if (type == TimeUnit.SECONDS) {
            return UtilMath.trim(trim, time / 1000.0);
        }
        return UtilMath.trim(trim, time);
    }

    @SuppressWarnings("unused")
    public static String convertTime(long time) {
        if (time < 0L) {
            return null;
        }
        final long week = 0L;
        long day = 0L;
        long hour = 0L;
        long minute = 0L;
        long second = 0L;
        day = time / 86400L;
        hour = (time -= day * 86400L) / 3600L;
        minute = (time -= hour * 3600L) / 60L;
        second = (time -= minute * 60L);
        String build = "";
        if (day > 0L) {
            build = build + day + " day" + ((day == 1L) ? " " : "s ");
        }
        if (hour > 0L) {
            build = build + hour + " hour" + ((hour == 1L) ? " " : "s ");
        }
        if (minute > 0L) {
            build = build + minute + " minute" + ((minute == 1L) ? " " : "s ");
        }
        if (second > 0L) {
            build = build + second + " second" + ((second == 1L) ? " " : "s ");
        }
        if (build.length() == 0) {
            build = "0 seconds";
        }
        return build.trim();
    }

    public static String convertToFormat(long delay) {

        if (delay == 0L) {
            return "None";
        }

        return TimeUtil.formatTime(Math.abs((delay - System.currentTimeMillis())));
    }

    public static String formatTime(long millis) {
        final int sec = (int) (millis / 1000 % 60);
        final int min = (int) (millis / 60000 % 60);
        final int hr = (int) (millis / 3600000 % 24);
        return ((hr > 0) ? String.format("%02d:", hr) : "") + String.format("%02d:%02d", min, sec);
    }

    public static String MakeStr(final long time) {
        return convertString(time, 1, TimeUnit.FIT);
    }

    public static String MakeStr(final long time, final int trim) {
        return convertString(time, trim, TimeUnit.FIT);
    }

    public static String convertString(final long time, final int trim, TimeUnit type) {
        if (time == -1L) {
            return "Permanent";
        }
        if (type == TimeUnit.FIT) {
            type = ((time < 60000L) ? TimeUnit.SECONDS : ((time < 3600000L) ? TimeUnit.MINUTES : ((time < 86400000L) ? TimeUnit.HOURS : TimeUnit.DAYS)));
        }
        if (type == TimeUnit.DAYS) {
            return "" + UtilMath.trim(trim, time / 8.64E7) + " Days";
        }
        if (type == TimeUnit.HOURS) {
            return "" + UtilMath.trim(trim, time / 3600000.0) + " Hours";
        }
        if (type == TimeUnit.MINUTES) {
            return "" + UtilMath.trim(trim, time / 60000.0) + " Minutes";
        }
        if (type == TimeUnit.SECONDS) {
            return "" + UtilMath.trim(trim, time / 1000.0) + " Seconds";
        }
        return "" + UtilMath.trim(trim, time) + " Milliseconds";
    }

    public static boolean elapsed(final long from, final long required) {
        return System.currentTimeMillis() - from > required;
    }

    public static long elapsed(final long starttime) {
        return System.currentTimeMillis() - starttime;
    }

    public static long left(final long start, final long required) {
        return required + start - System.currentTimeMillis();
    }

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public enum TimeUnit
    {
        FIT,
        DAYS,
        HOURS,
        MINUTES,
        SECONDS,
        MILLISECONDS;
    }
}