import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import org.HdrHistogram.Histogram;
import org.agrona.BitUtil;
import org.agrona.BufferUtil;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;

import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

public class Publisher {
    private static final String CHANNEL_PUB = "aeron:udp?endpoint=224.0.1.1:40456";
    private static final String CHANNEL_SUB = "aeron:udp?endpoint=224.0.1.3:40455";
    private static final int STREAM_ID_PUB = 10;
    private static final int STREAM_ID_SUB = 11;
    final static long EXPECTED_TIME_BEETWEEN_CALLS = TimeUnit.SECONDS.toNanos(1) / 100000;
    private static final int COST_TO_MEASURE_NS_IN_NS = 40;
    private static int MESSAGES_TO_SEND = 100000;
    private static int messagesReceived = 0;
    private static Histogram histogram;
    private static Histogram accHistogram;

    private static void initHistograms() {
        // Create histogram objects
        histogram = new Histogram(
                TimeUnit.NANOSECONDS.toNanos(50),
                TimeUnit.MILLISECONDS.toNanos(5),
                3
        );
        accHistogram = new Histogram(
                TimeUnit.NANOSECONDS.toNanos(50),
                TimeUnit.MILLISECONDS.toNanos(30),
                3
        );
        // Error control
        histogram.setAutoResize(true);
        accHistogram.setAutoResize(true);
    }

    public static void main(final String[] args) {
        initHistograms();
        final Aeron.Context ctx = new Aeron.Context();

        try (Aeron aeron = Aeron.connect(ctx);
             final Publication publication = aeron.addPublication(CHANNEL_PUB, STREAM_ID_PUB);
             final Subscription subscription = aeron.addSubscription(CHANNEL_SUB, STREAM_ID_SUB)) {

            // Publisher and consumer threads
            Thread consumerThread = new Thread(() -> receiveMessage(subscription));
            Thread publisherThread = new Thread(() -> {
                try {
                    sendMessages(publication);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            consumerThread.start();
            Thread.sleep(1000); // Wait to consumer to rise up
            publisherThread.start();

            // wait threads to finish
            publisherThread.join();
            consumerThread.join();

            //Output histogram
            printHistograms();
        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }

    private static void sendMessages(Publication publication) throws InterruptedException {
        final UnsafeBuffer buffer = new UnsafeBuffer(
                BufferUtil.allocateDirectAligned(512, BitUtil.CACHE_LINE_LENGTH)
        );
        long zeroTime = System.nanoTime();
        long nextCallTime = zeroTime;
        int i = 0;

        while (i < MESSAGES_TO_SEND) {
            // Wait until there is the time for the next call
            while (System.nanoTime() < nextCallTime) ;

            // Message to send
            // Format: zeroTime$$timestamp$$content$$messageNumber
            final String message = zeroTime + "$$" + System.nanoTime() + "$$Hello World$$" + i;
            final byte[] messageBytes = message.getBytes();
            buffer.putBytes(0, messageBytes);

            // Send message
            final long result = publication.offer(buffer, 0, messageBytes.length);

            // Output errors
            if (offerHasErrors(result)) {
                // Cola llena > Esperamos a liberar espacio
                if (result == Publication.BACK_PRESSURED) {
                    System.out.println("BACK_PRESSURED - waiting 5ms for " + i);
                    Thread.sleep(5);
                } else {
                    System.out.print(getOfferError(result));
                }
                continue; // next iteration
            }

            // Calculate the next time limit to send the message
            nextCallTime += EXPECTED_TIME_BEETWEEN_CALLS;
            i++;
        }
    }

    private static void receiveMessage(Subscription subscription) {
        final FragmentHandler fragmentHandler = onFragmentReceived();
        final BusySpinIdleStrategy idleStrategy = new BusySpinIdleStrategy();
        while (messagesReceived < MESSAGES_TO_SEND) {
            int fragmentsRead = subscription.poll(fragmentHandler, 1);
            idleStrategy.idle(fragmentsRead);
        }
    }

    private static FragmentHandler onFragmentReceived() {
        return (buffer, offset, length, header) -> {
            // Get current time
            final long endTime = System.nanoTime();

            // Processing message
            final byte[] data = new byte[length];
            buffer.getBytes(offset, data);
            final String receivedString = new String(data);

            // Split message
            final String[] messageSplitted = receivedString.split("\\$\\$");
            final long zeroTime = Long.valueOf(messageSplitted[0]);
            final long startTime = Long.valueOf(messageSplitted[1]);
            final int messageNumber = Integer.valueOf(messageSplitted[3]);
            final long nextCallTime = zeroTime + (messageNumber * EXPECTED_TIME_BEETWEEN_CALLS);

            long latency = ((endTime - startTime) / 2) - (COST_TO_MEASURE_NS_IN_NS * 2);
            long gapBetweenCalls = (startTime - nextCallTime) - COST_TO_MEASURE_NS_IN_NS;
            // System.out.println(gapBetweenCalls);
            long accLatency = latency + (gapBetweenCalls > 0 ? gapBetweenCalls : 0);

            // Record values
            histogram.recordValue(latency);
            accHistogram.recordValue(accLatency);

            messagesReceived++;
        };
    }

    private static void printHistograms() {
        System.out.println("Histogram");
        histogram.outputPercentileDistribution(
                new PrintStream(System.out),
                1000.0
        );
        System.out.println("Acc. Histogram");
        accHistogram.outputPercentileDistribution(
                new PrintStream(System.out),
                1000.0
        );
    }

    private static boolean offerHasErrors(long result) {
        return result < 0;
    }

    private static String getOfferError(long result) {
        if (result == Publication.BACK_PRESSURED) {
            return "Offer failed due to back pressure";
        } else if (result == Publication.NOT_CONNECTED) {
            return "Offer failed because publisher is not connected to a subscriber";
        } else if (result == Publication.ADMIN_ACTION) {
            return "Offer failed because of an administration action in the system";
        } else if (result == Publication.CLOSED) {
            return "Offer failed because publication is closed";
        } else {
            return "Offer failed due to unknown reason: " + result;
        }
    }
}