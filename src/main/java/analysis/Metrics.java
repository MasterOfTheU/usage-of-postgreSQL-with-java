package analysis;

import java.util.concurrent.TimeUnit;

public class Metrics {
    private static long startTime;
    private static long stopTime;

    /**
     * Sets start time of method execution.
     */
    protected static void startMetrics(){
        startTime = System.currentTimeMillis();
    }

    /**
     * Sets stop time of method execution.
     */
    protected static void stopMetrics(){
        stopTime = System.currentTimeMillis();
    }

    /**
     * @return Returns method execution time.
     */
    private static long getExecutionTime() {
        return stopTime - startTime;
    }

    /**
     * Prints the name of recently executed method.
     * @param methodName Gets the name of the executed method.
     */
    public static void printMethodName(String methodName) {
        System.out.printf("%s() has executed.\n", methodName);
    }

    /**
     * @param bytes Gets the amount of used memory in app runtime.
     * @return Bytes converted to megabytes.
     */
    private static double bytesToMegabytes(long bytes) {
        return bytes / (1024.0 * 1024.0);
    }

    /**
     * Gathers metrics of executed methods. Prints to console the amount of used memory in megabytes and method execution time in ms.
     */
    public static boolean gatherPerformance(String methodName) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.printf("Used memory in megabytes: %f \n", bytesToMegabytes(usedMemory));
        System.out.printf("The runtime of %s() is %d min\n", methodName, TimeUnit.MILLISECONDS.toMinutes(getExecutionTime()));
        return true;
    }
}
