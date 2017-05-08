package foo.bar.mq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageFactory {
    private final static String QUEUE_NAME = "hello";
    private static final String EXCHANGE_NAME = "topic_logs";
    private static Channel THE_CHANNEL;

    public static Channel getChannel() {
        if (THE_CHANNEL == null) {
            setChannel();
        }
        return THE_CHANNEL;
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
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Could not create RabbitMq channel", e);
        }


    }

    public static void main(String[] argv) throws IOException, TimeoutException {
        Channel channel = getChannel();

        String routingKey = "anonymous.info";
        String message = "Hello World!";

        // Actively declare a server-named exclusive, autodelete, non-durable queue.
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
        // And a second queue
        String queueName2 = channel.queueDeclare().getQueue();
        channel.queueBind(queueName2, EXCHANGE_NAME, routingKey);

        // Send msg
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");

        //channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        //System.out.println(" [x] Sent '" + message + "'");

        // Receive msg
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Delivered '" + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);
        channel.basicConsume(queueName2, true, consumer);

        //String received = channel.basicConsume(QUEUE_NAME, true, consumer);
        //System.out.println(" [x] Received '" + received + "'");


    }
}
