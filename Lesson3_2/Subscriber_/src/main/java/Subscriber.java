import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import org.agrona.BitUtil;
import org.agrona.BufferUtil;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;


public class Subscriber {
    private static final String CHANNEL_SUB = "aeron:udp?endpoint=224.0.1.1:40456";
    private static final String CHANNEL_PUB = "aeron:udp?endpoint=224.0.1.3:40455";
    private static final int STREAM_ID_SUB = 10;
    private static final int STREAM_ID_PUB = 11;
    private static final UnsafeBuffer BUFFER
            = new UnsafeBuffer(BufferUtil.allocateDirectAligned(512, BitUtil.CACHE_LINE_LENGTH));

    public static void main(final String[] args) {
        final Aeron.Context ctx = new Aeron.Context();

        try (
                Aeron aeron = Aeron.connect(ctx);
                Subscription subscription = aeron.addSubscription(CHANNEL_SUB, STREAM_ID_SUB);
                Publication publication = aeron.addPublication(CHANNEL_PUB, STREAM_ID_PUB)
        ) {
            final FragmentHandler fragmentHandler = (directBuffer, offset, length, header) -> {
                final byte[] data = new byte[length];
                directBuffer.getBytes(offset, data);
                final String receivedString = new String(data);
                System.out.println(receivedString);

                final byte[] messageBytes = receivedString.getBytes();
                BUFFER.putBytes(0, messageBytes);
                publication.offer(BUFFER, 0, messageBytes.length);
            };
            callPolling(subscription, fragmentHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void callPolling(Subscription subscription, FragmentHandler fragmentHandler) {
        final BusySpinIdleStrategy idleStrategy = new BusySpinIdleStrategy();
        while (true) {
            int fragmentsRead = subscription.poll(fragmentHandler, 1);
            idleStrategy.idle(fragmentsRead);
        }
    }
}
