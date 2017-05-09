package foo.bar.websocket;

import foo.bar.RandomBot;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.concurrent.Future;

import static java.net.URI.create;

public class WebsocketFactory {
    private static final URI TARGET = create("ws://" + RandomBot.TARGET_HOST + "/events/");
    private static final WebSocketClient client = new WebSocketClient();


    public static EventSocketCounter getCounterInstance() throws Exception {
        if (!client.isStarted()) {
            client.setMaxTextMessageBufferSize(10*1024*1024);
            client.start();
        }

        // The socket that receives events
        EventSocketCounter socket = new EventSocketCounter();
        // Attempt Connect
        Future<Session> fut = client.connect(socket, TARGET);
        // Wait for Connect
        Session session = fut.get();

        // Send a message
        session.getRemote().sendString("Hello");

        return socket;
    }

    public static EventSocketListener getListenerInstance() throws Exception {
        if (!client.isStarted())
            client.start();
        // The socket that receives events
        EventSocketListener socket = new EventSocketListener();
        // Attempt Connect
        Future<Session> fut = client.connect(socket, TARGET);
        // Wait for Connect
        Session session = fut.get();

        // Send a message
        session.getRemote().sendString("Hello");

        return socket;
    }

    public static void shutdown() throws Exception {
        client.stop();
    }
}
