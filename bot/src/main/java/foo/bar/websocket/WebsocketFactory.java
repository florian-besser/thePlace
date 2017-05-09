package foo.bar.websocket;

import foo.bar.RandomBot;
import foo.bar.util.EventualExecutor;
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

    public static EventSocketCounter getCounterInstance() throws Exception {
        startClientIfNecessary();

        // The socket that receives events
        EventSocketCounter socket = new EventSocketCounter();
        EventualExecutor<WebSocketAdapter, Session> exec = new EventualExecutor<>();
        Session session = exec.tryExecute(WebsocketFactory::getSession, socket);

        // Send a message
        session.getRemote().sendString("Hello");

        return socket;
    }

    public static EventSocketListener getListenerInstance() throws Exception {
        startClientIfNecessary();

        // The socket that receives events
        EventSocketListener socket = new EventSocketListener();
        EventualExecutor<WebSocketAdapter, Session> exec = new EventualExecutor<>();
        Session session = exec.tryExecute(WebsocketFactory::getSession, socket);

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

    private static Session getSession(WebSocketAdapter socket) {
        try {
            // Attempt Connect
            Future<Session> fut = client.connect(socket, TARGET);
            // Wait for Connect
            return fut.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException("Could not connect to Backend!");
        }
    }

    public static void shutdown() throws Exception {
        client.stop();
    }
}
