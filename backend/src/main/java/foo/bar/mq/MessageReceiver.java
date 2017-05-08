package foo.bar.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import foo.bar.board.Board;
import foo.bar.board.Pixel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MessageReceiver extends DefaultConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiver.class);

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
        String message = new String(body, "UTF-8");
        LOGGER.info(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");

        ObjectMapper mapper = new ObjectMapper();
        Pixel pixel = mapper.readValue(message, Pixel.class);
        Board.THE_BOARD.setPixel(pixel);
    }
}
