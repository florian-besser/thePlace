package foo.bar.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import foo.bar.RandomBot;
import foo.bar.model.Pixel;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class EventSocketListener extends EventSocketCounter {
    List<Pixel> setPixels = new ArrayList<>();

    public static EventSocketListener getNewInstance() throws Exception {
        URI uri = URI.create("ws://" + RandomBot.TARGET_HOST + "/events/");

        WebSocketClient client = new WebSocketClient();
        client.start();
        // The socket that receives events
        EventSocketListener socket = new EventSocketListener();
        // Attempt Connect
        Future<Session> fut = client.connect(socket, uri);
        // Wait for Connect
        Session session = fut.get();

        // Send a message
        session.getRemote().sendString("Hello");

        return socket;
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        setPixels.addAll(deserialize(message));
    }

    private List<Pixel> deserialize(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            CollectionType javatype = mapper.getTypeFactory().constructCollectionType(List.class, Pixel.class);
            return mapper.readValue(json, javatype);
        } catch (IOException e) {
            throw new RuntimeException("Error wile converting String to Pixel.", e);
        }
    }

    public List<Pixel> getSetPixels() {
        return setPixels;
    }
}