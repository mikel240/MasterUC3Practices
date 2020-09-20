package com.cnebrera.uc3.tech.lesson1;

import com.cnebrera.uc3.tech.lesson1.simulator.BaseSyncOpSimulator;
import com.cnebrera.uc3.tech.lesson1.simulator.SyncOpSimulRndPark;
import org.HdrHistogram.Histogram;

import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

/**
 * First practice, measure latency on a simple operation
 */
public class PracticeLatency1 {
    private static final int COST_TO_MEASURE_MS_IN_NS = 40;

    public static void main(String[] args) {
        runCalculations();
    }

    private static void runCalculations() {
        // Create a random park time simulator
        BaseSyncOpSimulator syncOpSimulator =
                new SyncOpSimulRndPark(
                        TimeUnit.NANOSECONDS.toNanos(100),
                        TimeUnit.MICROSECONDS.toNanos(100)
                );
        // Create the histogram object
        Histogram histogram = new Histogram(
                TimeUnit.NANOSECONDS.toNanos(100),
                TimeUnit.MILLISECONDS.toNanos(3),
                3
        );
        // Error control
        histogram.setAutoResize(true);

        for (int i = 0; i < 100000; i++) {
            long startTime = System.nanoTime();
            syncOpSimulator.executeOp();
            long latency = (System.nanoTime() - startTime) - (COST_TO_MEASURE_MS_IN_NS * 2);
            histogram.recordValue(latency);
        }
        // Output
        histogram.outputPercentileDistribution(new PrintStream(System.out), 1000.0);
    }
}
