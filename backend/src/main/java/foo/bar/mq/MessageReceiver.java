package foo.bar.mq;

import com.codahale.metrics.Timer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import foo.bar.board.BoardHolder;
import foo.bar.model.Pixel;
import foo.bar.monitoring.Monitoring;
import foo.bar.websocket.UpdateBatching;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MessageReceiver extends DefaultConsumer {
    public static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiver.class);
    private final Timer receivedPixel = Monitoring.registry.timer("rabbit.receivedPixel");

    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     */
    public MessageReceiver(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties, byte[] body) throws IOException {
        Timer.Context time = receivedPixel.time();
        try {
            String message = new String(body, "UTF-8");
            LOGGER.debug("Received '" + envelope.getRoutingKey() + "':'" + message + "'");

            Pixel pixel = MAPPER.readValue(message, Pixel.class);

            // Update board
            BoardHolder.getInstance().setPixel(pixel);

            //BoardHolder.UI.updateBoard(pixel);

            // Send Websocket messages
            UpdateBatching.getInstance().addUpdate(pixel);
        } finally {
            time.stop();
        }
    }
}
