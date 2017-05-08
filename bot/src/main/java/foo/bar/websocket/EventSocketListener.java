package foo.bar.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import foo.bar.model.Pixel;
import foo.bar.rest.Resource;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventSocketListener extends WebSocketAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Resource.class);
    List<Pixel> setPixels = new ArrayList<>();

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        LOGGER.info("Socket Connected: " + sess);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        LOGGER.debug("Received TEXT message: " + message);
        Pixel p = serialize(message);
        setPixels.add(p);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        LOGGER.info("Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }


    private Pixel serialize(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, Pixel.class);
        } catch (IOException e) {
            throw new RuntimeException("Error wile converting String to Pixel.", e);
        }
    }

    public List<Pixel> getSetPixels() {
        return setPixels;
    }
}