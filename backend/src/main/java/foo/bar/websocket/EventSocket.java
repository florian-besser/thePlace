package foo.bar.websocket;

import foo.bar.rest.Resource;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class EventSocket extends WebSocketAdapter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Resource.class);

    @Override
    public void onWebSocketConnect(Session sess)
    {
        super.onWebSocketConnect(sess);
        LOGGER.info("Socket Connected: " + sess);
    }

    @Override
    public void onWebSocketText(String message)
    {
        super.onWebSocketText(message);
        LOGGER.info("Received TEXT message: " + message);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode,reason);
        PooledSessionCreator.remove(this);
        LOGGER.info("Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause)
    {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }

    public void sendMessage(String text) {
        LOGGER.info("Sending message: " + text);
        Future<Void> voidFuture = getRemote().sendStringByFuture(text);
        //Do something
        try {
            voidFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Failed to send message: " + text, e);
        }
        LOGGER.info("Sent message: " + text);
    }
}