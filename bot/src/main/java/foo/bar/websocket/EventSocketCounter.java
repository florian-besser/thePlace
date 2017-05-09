package foo.bar.websocket;

import foo.bar.RandomBot;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.Future;

public class EventSocketCounter extends WebSocketAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventSocketCounter.class);
    int msgsReceived = 0;

    public static EventSocketCounter getNewInstance() throws Exception {
        URI uri = URI.create("ws://" + RandomBot.TARGET_HOST + "/events/");

        WebSocketClient client = new WebSocketClient();
        client.start();
        // The socket that receives events
        EventSocketCounter socket = new EventSocketCounter();
        // Attempt Connect
        Future<Session> fut = client.connect(socket, uri);
        // Wait for Connect
        Session session = fut.get();

        // Send a message
        session.getRemote().sendString("Hello");

        return socket;
    }

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        LOGGER.debug("Socket Connected: " + sess);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        LOGGER.debug("Received TEXT message: " + message);
        msgsReceived++;
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        LOGGER.debug("Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }

    public int getMsgsReceived() {
        return msgsReceived;
    }
}