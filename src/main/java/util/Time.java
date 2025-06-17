package util;

/*
 * Time provides utilities to track application runtime.
 * It records the start time and computes elapsed time.
 * Author(s):
 */
public class Time {
    public static float timeStarted = System.nanoTime(); // Time of application start

    /*
     * Returns elapsed time since application start in seconds.
     * @return elapsed time in seconds since application start
     */
    public static float getTime() {
        return (float)((System.nanoTime() - timeStarted) * 1E-9);
        // Time since application start converted from nanoseconds to seconds
    }
}
