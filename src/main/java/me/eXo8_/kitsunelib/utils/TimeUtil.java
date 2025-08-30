package me.eXo8_.kitsunelib.utils;

public class TimeUtil
{
    public static boolean hasPassed(long time, long durationMillis) {
        return System.currentTimeMillis() - time >= durationMillis;
    }

    public static long seconds(int s) { return s * 1000L; }
    public static long minutes(int m) { return m * 60L * 1000L; }
    public static long hours(int h)   { return h * 60L * 60L * 1000L; }
    public static long days(int d)    { return d * 24L * 60L * 60L * 1000L; }
}