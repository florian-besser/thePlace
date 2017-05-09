package foo.bar.websocket;

import foo.bar.RandomBot;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.net.URI.create;

public class WebsocketFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketFactory.class);

    private static final URI TARGET = create("ws://" + RandomBot.TARGET_HOST + "/events/");
    private static final WebSocketClient client = new WebSocketClient();
    private static final int MAX_RETRIES = 30;


    public static EventSocketCounter getCounterInstance() throws Exception {
        startClientIfNecessary();

        // The socket that receives events
        EventSocketCounter socket = new EventSocketCounter();
        Session session = getSession(socket);

        // Send a message
        session.getRemote().sendString("Hello");

        return socket;
    }

    public static EventSocketListener getListenerInstance() throws Exception {
        startClientIfNecessary();

        // The socket that receives events
        EventSocketListener socket = new EventSocketListener();
        Session session = getSession(socket);

        // Send a message
        session.getRemote().sendString("Hello");

        return socket;
    }

    private static void startClientIfNecessary() throws Exception {
        if (!client.isStarted()) {
            client.setMaxTextMessageBufferSize(10 * 1024 * 1024);
            client.start();
        }
    }

    private static Session getSession(WebSocketAdapter socket) throws IOException, InterruptedException, ExecutionException {
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                // Attempt Connect
                Future<Session> fut = client.connect(socket, TARGET);
                // Wait for Connect
                return fut.get();
            } catch (RuntimeException e) {
                LOGGER.warn("Could not connect to Backend!");
                if (i == MAX_RETRIES - 1) {
                    throw e;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        //Ignore
                    }
                }
            }
        }
        throw new RuntimeException("Could not connect to Backend!");
    }

    public static void shutdown() throws Exception {
        client.stop();
    }
}
