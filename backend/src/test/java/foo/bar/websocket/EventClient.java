package foo.bar.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.concurrent.Future;

public class EventClient
{
    public static void main(String[] args)
    {
        URI uri = URI.create("ws://localhost:2222/events/");

        WebSocketClient client = new WebSocketClient();
        client.setMaxTextMessageBufferSize(10 * 1024 * 1024);
        try
        {
            try
            {
                client.start();
                // The socket that receives events
                EventSocket socket = new EventSocket();
                // Attempt Connect
                Future<Session> fut = client.connect(socket,uri);
                // Wait for Connect
                Session session = fut.get();

                // Send a message
                session.getRemote().sendString("Hello");

                // Wait until the server disconnects
                while (socket.isConnected()) {
                    Thread.sleep(1000);
                }

                // Close session
                session.close();
            }
            finally
            {
                client.stop();
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }
    }
}