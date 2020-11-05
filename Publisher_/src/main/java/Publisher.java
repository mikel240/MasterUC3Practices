import io.aeron.Aeron;
import io.aeron.Publication;
import org.agrona.BitUtil;
import org.agrona.BufferUtil;
import org.agrona.concurrent.UnsafeBuffer;

public class Publisher {
    //aeron:udp?endpoint=224.0.0.1:40456
    private static final String CHANNEL = "aeron:udp?endpoint=224.0.1.1:40456";
    private static final int STREAM_ID = 10;
    private static final int NUMBER_OF_MESSAGES = 10000;

    public static void main(final String[] args) {
        final UnsafeBuffer buffer = new UnsafeBuffer(BufferUtil.allocateDirectAligned(512, BitUtil.CACHE_LINE_LENGTH));
        final Aeron.Context ctx = new Aeron.Context();

        try (Aeron aeron = Aeron.connect(ctx);
             Publication publication = aeron.addPublication(CHANNEL, STREAM_ID)) {
            sendMessages(publication, buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendMessages(Publication publication, UnsafeBuffer buffer) throws InterruptedException {
        for (long i = 0; i < NUMBER_OF_MESSAGES; i++) {
            final String message = "Hello World " + i + "!";
            final byte[] messageBytes = message.getBytes();
            buffer.putBytes(0, messageBytes);

            final long result = publication.offer(buffer, 0, messageBytes.length);
            final String error = getError(result);

            // If something went wrong
            if (error.length() > 0) {
                System.out.println(error);
            } else {
                System.out.println("Message sended!");
            }

            Thread.sleep(1000);
        }
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
