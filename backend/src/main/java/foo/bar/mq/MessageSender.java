package foo.bar.mq;

import com.codahale.metrics.Meter;
import foo.bar.monitoring.Monitoring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static foo.bar.mq.MessageFactory.EXCHANGE_NAME;
import static foo.bar.mq.MessageFactory.ROUTING_KEY;

public class MessageSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);
    private final Meter sentPixel = Monitoring.registry.meter("rabbit_send");

    public void sendMessage(String message) {
        try {
            sentPixel.mark();
            MessageFactory.getChannel().basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Error while sending message", e);
        }
        LOGGER.debug("Sent '" + ROUTING_KEY + "':'" + message + "'");
    }
}
