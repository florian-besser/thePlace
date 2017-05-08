package foo.bar;

import foo.bar.board.Board;
import foo.bar.model.SimpleColor;
import foo.bar.rest.PutPixelBody;
import foo.bar.websocket.EventSocket;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomBot.class);
    private static ThreadLocalRandom current = ThreadLocalRandom.current();
    private static Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFeature.class));


    public static void main(String[] args) throws Exception {
        EventSocket socket = getSocket();
        Board board = getBoard();
        SimpleColor[][] colors = board.getColors();
        int xMax = colors[0].length;
        int yMax = colors.length;

        while (true) {
            putPixel(xMax, yMax);
        }
    }

    private static EventSocket getSocket() throws Exception {
        URI uri = URI.create("ws://localhost:2222/events/");

        WebSocketClient client = new WebSocketClient();
        client.start();
        // The socket that receives events
        EventSocket socket = new EventSocket();
        // Attempt Connect
        Future<Session> fut = client.connect(socket, uri);
        // Wait for Connect
        Session session = fut.get();

        // Send a message
        session.getRemote().sendString("Hello");

        return socket;

    }

    private static Board getBoard() {
        WebTarget webTarget = client.target("http://localhost:2222/rest/thePlace").path("place");

        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();

        Board board = response.readEntity(Board.class);
        int y = 0;
        for (SimpleColor[] row : board.getColors()) {
            int x = 0;
            for (SimpleColor c : row) {
                LOGGER.info("Initial content at " + y + " " + x + " is " + c);
                x++;
            }
            y++;
        }
        return board;
    }

    private static void putPixel(int xMax, int yMax) {
        int x = current.nextInt(0, xMax);
        int y = current.nextInt(0, yMax);
        WebTarget webTarget = client.target("http://localhost:2222/rest/thePlace").path("place")
                .path(Integer.toString(x))
                .path(Integer.toString(y));

        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        PutPixelBody entity = new PutPixelBody();
        UUID idOne = UUID.randomUUID();
        entity.setUser(idOne.toString());
        entity.setColor("#" + getRandomHexString(6));
        invocationBuilder.put(Entity.json(entity));
    }

    private static String getRandomHexString(int numchars) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(current.nextInt(0, 16)));
        }

        return sb.toString().substring(0, numchars);
    }
}
