package com.cnebrera.uc3.tech.lesson1;

import com.cnebrera.uc3.tech.lesson1.simulator.BaseSyncOpSimulator;
import com.cnebrera.uc3.tech.lesson1.simulator.SyncOpSimulSleep;
import org.HdrHistogram.Histogram;
import org.HdrHistogram.SynchronizedHistogram;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Third practice, measure accumulated latency with multiple threads
 */
public class PracticeLatency3 {
    final static int NUM_THREADS = 8;
    final static int NUM_EXECUTIONS = 100;
    final static int MAX_EXPECTED_EXECUTIONS_PER_SECOND = 50;
    final static int COST_TO_MEASURE_MS_IN_NS = 50;
    final static Histogram syncHistogram = new SynchronizedHistogram(10,600,1);
    final static Histogram syncAccLatencyHistogram = new SynchronizedHistogram(10,9000,1);

    public static void main(String[] args) {
        runCalculations();
    }

    private static void runCalculations() {
        // Create a sleep time simulator, it will sleep for 10 milliseconds in each call
        BaseSyncOpSimulator syncOpSimulator = new SyncOpSimulSleep(10);
        // List of threads
        List<Runner> runners = new LinkedList<>();
        // Histograms error control
        syncHistogram.setAutoResize(true);
        syncAccLatencyHistogram.setAutoResize(true);

        // Create the threads and start them
        for (int i = 0; i < NUM_THREADS; i++) {
            final Runner runner = new Runner(syncOpSimulator);
            runners.add(runner);
            new Thread(runner).start();
        }

        // Wait for runners to finish
        runners.forEach(Runner::waitToFinish);

        // Print histograms
        syncHistogram.outputPercentileDistribution(new PrintStream(System.out), 1.0);
        syncAccLatencyHistogram.outputPercentileDistribution(new PrintStream(System.out), 1.0);
    }

    /**
     * The runner that represent a thread execution
     */
    private static class Runner implements Runnable {
        final BaseSyncOpSimulator syncOpSimulator;
        volatile boolean finished = false;

        /**
         * Create a new runner
         *
         * @param syncOpSimulator shared operation simulator
         */
        private Runner(BaseSyncOpSimulator syncOpSimulator) {
            this.syncOpSimulator = syncOpSimulator;
        }

        @Override
        public void run() {
            // Calculate the expected time between consecutive calls, considering the number of executions per second
            final long expectedTimeBetweenCalls = TimeUnit.SECONDS.toMillis(1) / MAX_EXPECTED_EXECUTIONS_PER_SECOND;

            // Calculate the next call time, the first time should be immediate
            long nextCallTime = System.currentTimeMillis();

            // Execute the operation the required number of times
            for (int i = 0; i < NUM_EXECUTIONS; i++) {
                // Wait until there is the time for the next call
                while (System.currentTimeMillis() < nextCallTime) ;

                long startTime = System.currentTimeMillis();
                syncOpSimulator.executeOp(); // sleep 10ms
                long latency = (System.currentTimeMillis() - startTime) -
                        (TimeUnit.NANOSECONDS.toMillis(COST_TO_MEASURE_MS_IN_NS) * 2);
                long gapBetweenCalls = (startTime - nextCallTime)
                        - TimeUnit.NANOSECONDS.toMillis(COST_TO_MEASURE_MS_IN_NS);
                long accLatency = latency + (gapBetweenCalls > 0 ? gapBetweenCalls : 0);

                // Save histogram records
                syncHistogram.recordValue(latency);
                syncAccLatencyHistogram.recordValue(accLatency);

                // Calculate the next time to call execute op
                nextCallTime += expectedTimeBetweenCalls;
            }

            finished = true;
        }

        /**
         * Wait for the runner execution to complete
         */
        public void waitToFinish() {
            while (!this.finished) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}
