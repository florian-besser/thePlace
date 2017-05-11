package foo.bar.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventSocketCounter extends WebSocketAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventSocketCounter.class);
    protected int msgsReceived = 0;

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        sess.getPolicy().setMaxTextMessageSize(10 * 1024 * 1024);
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