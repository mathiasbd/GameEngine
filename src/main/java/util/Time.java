package util;

public class Time {
    public static float timeStarted = System.nanoTime(); // Time of application start
    public static float getTime() {
        return (float)((System.nanoTime() - timeStarted) * 1E-9);
        // Time since application start converted from nanoseconds to seconds
    }
}
