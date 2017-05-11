package foo.bar.application;

import com.codahale.metrics.jersey2.InstrumentedResourceMethodApplicationListener;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import foo.bar.monitoring.Monitoring;
import foo.bar.mq.MessageFactory;
import foo.bar.mq.MessageReceiver;
import foo.bar.rest.CORSResponseFilter;
import foo.bar.websocket.EventServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
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
    private static final String STATIC_FILE_PATH = "/home/backend/static/";

    public static void main(String[] args) throws Exception {
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
        config.register(new InstrumentedResourceMethodApplicationListener(Monitoring.registry));
        config.packages("foo.bar");
        ServletHolder servlet = new ServletHolder("rs-rest", new ServletContainer(config));
        context.addServlet(servlet, "/rest/*");
        context.addFilter(CORSResponseFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

        // add static file handler
        ServletHolder staticFileServletHolder = new ServletHolder("static-home", DefaultServlet.class);
        staticFileServletHolder.setInitParameter("resourceBase", STATIC_FILE_PATH);
        staticFileServletHolder.setInitParameter("dirAllowed", "false");
        staticFileServletHolder.setInitParameter("pathInfoOnly", "false");
        context.addServlet(staticFileServletHolder, "/*");

        // Add a websocket to a specific path spec
        ServletHolder holderEvents = new ServletHolder("ws-events", EventServlet.class);
        context.addServlet(holderEvents, "/events/*");

        try {
            server.start();

            LOGGER.info("Preparing to listen to RabbitMQ");
            // Start copying messages from Topic to our own Queue
            Channel channel = MessageFactory.getChannel();

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
