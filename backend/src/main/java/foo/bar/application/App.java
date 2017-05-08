package foo.bar.application;

import foo.bar.websocket.EventServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class App {

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

        // Add a websocket to a specific path spec
        ServletHolder holderEvents = new ServletHolder("ws-events", EventServlet.class);
        context.addServlet(holderEvents, "/events/*");

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }
}
