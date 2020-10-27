package subscriber;

import io.aeron.Aeron;
import io.aeron.FragmentAssembler;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.IdleStrategy;

public class Subscriber {
    private static final String CHANNEL = "aeron:udp?endpoint=224.0.1.1:40456";
    private static final int STREAM_ID = 2;
    private static final int FRAGMENT_LIMIT_COUNT = 1;

    public void main(final String[] args) {
        final Aeron.Context context = new Aeron.Context();

        try (final Aeron aeron = Aeron.connect(context);
             final Subscription subscription = aeron.addSubscription(CHANNEL, STREAM_ID)) {
            final FragmentHandler fragmentHandler = new FragmentAssembler(this::onFragmentReceived);
            callPolling(subscription, fragmentHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callPolling(Subscription subscription, FragmentHandler fragmentHandler) {
        new Thread(() -> {
            final IdleStrategy idleStrategy = new BusySpinIdleStrategy();
            while (true) {
                final int result = subscription.poll(fragmentHandler, FRAGMENT_LIMIT_COUNT);
                idleStrategy.idle(result);
            }
        }).start();
    }

    void onFragmentReceived(DirectBuffer buffer, int offset, int length, Header header) {
        final byte[] data = new byte[length];
        buffer.getBytes(offset, data);

        final String rcvString = new String(data);
        System.out.println(rcvString);
    }
}
