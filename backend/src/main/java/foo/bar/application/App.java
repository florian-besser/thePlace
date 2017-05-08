package foo.bar.application;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import foo.bar.mq.MessageFactory;
import foo.bar.mq.MessageReceiver;
import foo.bar.rest.CORSResponseFilter;
import foo.bar.websocket.EventServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);


    public static void main(String [] args) throws Exception {
        Server server = new Server();
        //Listen on 2222
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(2222);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ResourceConfig config = new ResourceConfig();
        config.packages("foo.bar");
        ServletHolder servlet = new ServletHolder("rs-rest", new ServletContainer(config));
        context.addServlet(servlet, "/rest/*");
        context.addFilter(CORSResponseFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

        // Add a websocket to a specific path spec
        ServletHolder holderEvents = new ServletHolder("ws-events", EventServlet.class);
        context.addServlet(holderEvents, "/events/*");

        try {
            server.start();

            LOGGER.info("Preparing to listen to RabbitMQ");
            // Start copying messages from Topic to our own Queue
            Channel channel = MessageFactory.getChannel();
            LOGGER.info("Listening to RabbitMQ");

            // Wait a little to make sure you read the right stuff from Redis
            Thread.sleep(5000);

            // TODO: 5/8/17 READ STATE FROM REDIS

            // Start consuming our queue
            LOGGER.info("Starting to consume RabbitMQ messages");
            Consumer consumer = new MessageReceiver(channel);
            channel.basicConsume(MessageFactory.getQueueName(), true, consumer);

            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.destroy();
        }


    }
}
