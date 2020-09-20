package com.cnebrera.uc3.tech.lesson1;

import com.cnebrera.uc3.tech.lesson1.simulator.BaseSyncOpSimulator;
import com.cnebrera.uc3.tech.lesson1.simulator.SyncOpSimulLongOperation;
import org.HdrHistogram.Histogram;

import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

/**
 * Second practice, measure latency with and without warmup
 */
public class PracticeLatency2 {
    private static final int COST_TO_MEASURE_NS = 40;
    private static final BaseSyncOpSimulator syncOpSimulator = new SyncOpSimulLongOperation();
    private static final Histogram histogram = new Histogram(
            TimeUnit.MICROSECONDS.toNanos(1),
            TimeUnit.MILLISECONDS.toNanos(6),
            1
    );

    public static void main(String[] args) {
        // Histogram error control
        histogram.setAutoResize(true);

        for (int i = 0; i < 50; i++) {
            runCalculations();
            printHistogramData(i);
            // Reset the contents and stats of this histogram
            histogram.reset();
        }
    }

    private static void runCalculations() {
        for (int i = 0; i < 200000; i++) {
            long startTime = System.nanoTime();
            syncOpSimulator.executeOp();
            long latency = (System.nanoTime() - startTime) - (COST_TO_MEASURE_NS * 2);
            histogram.recordValue(latency);
        }
    }

    private static void printHistogramData(int iterationNum) {
        double mean = histogram.getMean();
        long minValue = histogram.getMinValue();
        long maxValue = histogram.getMaxValue();
        long percentile99 = histogram.getValueAtPercentile(99);
        long percentile999 = histogram.getValueAtPercentile(99.9);

        System.out.println("Iteration: " +iterationNum);
        System.out.println("Min: " + minValue);
        System.out.println("Max: " + maxValue);
        System.out.println("Mean: " + mean);
        System.out.println("Percentile 99: " + percentile99);
        System.out.println("Percentile 99.9: " + percentile999);
        System.out.println("=================");
    }
}
