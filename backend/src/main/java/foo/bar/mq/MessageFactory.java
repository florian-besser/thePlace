package foo.bar.mq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageFactory {
    public static final String EXCHANGE_NAME = "topic_logs";
    public static final String ROUTING_KEY = "anonymous.info";
    private static Channel THE_CHANNEL;
    private static String QUEUE_NAME;

    public static Channel getChannel() {
        if (THE_CHANNEL == null) {
            setChannel();
        }
        return THE_CHANNEL;
    }

    public static String getQueueName() {
        if (THE_CHANNEL == null) {
            setChannel();
        }
        return QUEUE_NAME;
    }

    private synchronized static void setChannel() {
        if (THE_CHANNEL != null) {
            return;
        }
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            THE_CHANNEL = connection.createChannel();

            // Also create Queue / Exchange here
            //THE_CHANNEL.queueDeclare(QUEUE_NAME, false, false, false, null);
            THE_CHANNEL.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            // Actively declare a server-named exclusive, autodelete, non-durable queue.
            QUEUE_NAME = THE_CHANNEL.queueDeclare().getQueue();
            THE_CHANNEL.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Could not create RabbitMq channel", e);
        }


    }
}
