package me.array.ArrayPractice.util;

import org.bukkit.Location;

public final class MathUtil {

    public static String convertTicksToMinutes(int ticks) {
        long minute = (long) ticks / 1200L;
        long second = (long) ticks / 20L - (minute * 60L);

        String secondString = Math.round(second) + "";

        if (second < 10) {
            secondString = 0 + secondString;
        }
        String minuteString = Math.round(minute) + "";

        if (minute == 0) {
            minuteString = 0 + "";
        }

        return minuteString + ":" + secondString;
    }

    public static String convertToRomanNumeral(int number) {
        switch (number) {
            case 1:
                return "I";
            case 2:
                return "II";
        }
        return null;
    }

    public static double roundToHalves(double d) {
        return Math.round(d * 2.0D) / 2.0D;
    }

    public static Location getMiddle(Location a, Location b){

        double x = (a.getBlockX() + b.getBlockX()) /2;
        double y = a.getBlockY();
        double z = (a.getBlockZ() + b.getBlockZ()) /2;

        return new Location(a.getWorld(), x,y,z);
    }
}
