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
    private static final int NUMBER_OF_MESSAGES = 10000;
    private static final int COST_TO_MEASURE_NS_IN_NS = 40;
    final static long EXPECTED_TIME_BEETWEEN_CALLS = TimeUnit.SECONDS.toNanos(1) / 1000;
    final static long MAX_TIME_ELAPSED_RECEIVING_MESSAGES_NS = EXPECTED_TIME_BEETWEEN_CALLS * 15;
    private static int warmUpIterations = 10;
    private static long nextCallTime;
    private static Histogram histogram;
    private static Histogram accHistogram;

    public static void main(final String[] args) {
        // Create histogram objects
        histogram = new Histogram(
                TimeUnit.NANOSECONDS.toNanos(50),
                TimeUnit.MILLISECONDS.toNanos(5),
                3
        );
        accHistogram = new Histogram(
                TimeUnit.NANOSECONDS.toNanos(50),
                TimeUnit.MILLISECONDS.toNanos(50),
                3
        );

        // Error control
        histogram.setAutoResize(true);
        accHistogram.setAutoResize(true);

        final UnsafeBuffer buffer = new UnsafeBuffer(BufferUtil.allocateDirectAligned(512, BitUtil.CACHE_LINE_LENGTH));
        final FragmentHandler fragmentHandler = onFragmentReceived();
        final Aeron.Context ctx = new Aeron.Context();

        try (Aeron aeron = Aeron.connect(ctx);
             Publication publication = aeron.addPublication(CHANNEL_PUB, STREAM_ID_PUB);
             Subscription subscription = aeron.addSubscription(CHANNEL_SUB, STREAM_ID_SUB)) {
            sendMessages(publication, subscription, fragmentHandler, buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendMessages(
            Publication publication,
            Subscription subscription,
            FragmentHandler fragmentHandler,
            UnsafeBuffer buffer
    ) {
        // Calculate the next call time, the first time should be immediate
        nextCallTime = System.nanoTime();

        for (long i = 0; i < NUMBER_OF_MESSAGES; i++) {
            // Wait until there is the time for the next call
            while (System.nanoTime() < nextCallTime) ;

            // Creating message to send
            // timestamp + $$ + content
            long startTime = System.nanoTime();
            final String message = startTime + "$$Hello World " + i;
            final byte[] messageBytes = message.getBytes();
            buffer.putBytes(0, messageBytes);

            // send message
            final long result = publication.offer(buffer, 0, messageBytes.length);

            // Error control
            final String error = getError(result);
            if (error.length() == 0) { // no errors
                // Waiting for roundtrip message
                receiveMessage(subscription, fragmentHandler);
            }

            // Calculate the next time to call execute op
            nextCallTime += EXPECTED_TIME_BEETWEEN_CALLS;
        }
        // Output
        System.out.println("Histogram");
        histogram.outputPercentileDistribution(new PrintStream(System.out), 1000.0);
        System.out.println("Acc. Histogram");
        accHistogram.outputPercentileDistribution(new PrintStream(System.out), 1000.0);
    }

    private static void receiveMessage(Subscription subscription, FragmentHandler fragmentHandler) {
        final BusySpinIdleStrategy idleStrategy = new BusySpinIdleStrategy();
        final long startLoopTime = System.nanoTime();

        while ((System.nanoTime() - startLoopTime) <= MAX_TIME_ELAPSED_RECEIVING_MESSAGES_NS) {
            int fragmentsRead = subscription.poll(fragmentHandler, 1);
            idleStrategy.idle(fragmentsRead);

            // Si recibimos datos...
            if (fragmentsRead > 0) {
                warmUpIterations--;
                if (warmUpIterations == 0) {
                    histogram.reset();
                    accHistogram.reset();
                }
                break;
            }
        }
    }

    private static FragmentHandler onFragmentReceived() {
        return (buffer, offset, length, header) -> {
            // Get current time  inmediatly
            final long endTime = System.nanoTime();

            // Process message
            final byte[] data = new byte[length];
            buffer.getBytes(offset, data);
            final String receivedString = new String(data);

            final long startTime = Long.valueOf(receivedString.split("\\$\\$")[0]);
            long latency = ((endTime - startTime) / 2) - (COST_TO_MEASURE_NS_IN_NS * 2);
            long gapBetweenCalls = (startTime-nextCallTime)
                    - TimeUnit.NANOSECONDS.toMillis(COST_TO_MEASURE_NS_IN_NS);
            long accLatency = latency + (gapBetweenCalls > 0 ? gapBetweenCalls : 0);

            // Record values
            histogram.recordValue(latency);
            accHistogram.recordValue(accLatency);
        };
    }

    private static String getError(long result) {
        if (result >= 0L) {
            return "";
        } else if (result == Publication.BACK_PRESSURED) {
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
