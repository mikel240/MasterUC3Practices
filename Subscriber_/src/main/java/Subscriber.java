import io.aeron.Aeron;
import io.aeron.FragmentAssembler;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import org.agrona.concurrent.BusySpinIdleStrategy;

public class Subscriber {
    private static final String CHANNEL = "aeron:udp?endpoint=224.0.1.1:40456";
    private static final int STREAM_ID = 10;
    private static final int FRAGMENT_LIMIT_COUNT = 1;

    public static void main(final String[] args) {
        final FragmentHandler fragmentHandler = (buffer, offset, length, header) -> {
            final byte[] data = new byte[length];
            buffer.getBytes(offset, data);
            final String receivedString = new String(data);
            System.out.println(receivedString);
        };
        final Aeron.Context ctx = new Aeron.Context();

        try (Aeron aeron = Aeron.connect(ctx);
             Subscription subscription = aeron.addSubscription(CHANNEL, STREAM_ID)) {
            callPolling(subscription, fragmentHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void callPolling(Subscription subscription, FragmentHandler fragmentHandler) {
            final BusySpinIdleStrategy idleStrategy = new BusySpinIdleStrategy();

            while (true) {
                int fragmentsRead = subscription.poll(fragmentHandler, FRAGMENT_LIMIT_COUNT);
                idleStrategy.idle(fragmentsRead);
            }
    }
}
