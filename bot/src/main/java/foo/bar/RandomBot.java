package foo.bar;

import com.google.common.util.concurrent.RateLimiter;
import foo.bar.board.Board;
import foo.bar.model.Pixel;
import foo.bar.model.SimpleColor;
import foo.bar.rest.PutPixelBody;
import foo.bar.websocket.EventSocketListener;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBot {
    public static final String TARGET_HOST = "192.168.2.96:2222";
    public static final int MAX_REQUESTS = 1000;
    public static final int MAX_REQUESTS_PER_SECOND = 10;
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomBot.class);
    private static RateLimiter throttle = RateLimiter.create(MAX_REQUESTS_PER_SECOND);
    private static ThreadLocalRandom current = ThreadLocalRandom.current();
    private static Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFeature.class));

    public static void main(String[] args) throws Exception {
        EventSocketListener socket = getSocket();
        Board originalBoard = getBoard();
        SimpleColor[][] colors = originalBoard.getColors();
        int xMax = colors[0].length;
        int yMax = colors.length;

        for (int i = 0; i < MAX_REQUESTS; i++) {
            putPixel(xMax, yMax);
        }

        Thread.sleep(5000);

        Board finishedBoard = getBoard();
        replayMessagesOn(originalBoard, socket.getSetPixels());
        assertEquals(originalBoard, finishedBoard);

        socket.getSession().close();
    }

    private static EventSocketListener getSocket() throws Exception {
        URI uri = URI.create("ws://" + TARGET_HOST + "/events/");

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

    private static Board getBoard() {
        WebTarget webTarget = client.target("http://" + TARGET_HOST + "/rest/thePlace").path("place");

        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();

        Board board = response.readEntity(Board.class);
        int y = 0;
        for (SimpleColor[] row : board.getColors()) {
            int x = 0;
            for (SimpleColor c : row) {
                LOGGER.debug("Content at " + y + " " + x + " is " + c);
                x++;
            }
            y++;
        }
        return board;
    }

    private static void putPixel(int xMax, int yMax) {
        throttle.acquire();
        int x = current.nextInt(0, xMax);
        int y = current.nextInt(0, yMax);
        WebTarget webTarget = client.target("http://" + TARGET_HOST + "/rest/thePlace").path("place")
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

    private static void replayMessagesOn(Board originalBoard, List<Pixel> pixels) {
        for (Pixel p : pixels) {
            originalBoard.setPixelInternal(p);
        }
    }

    private static void assertEquals(Board replayBoard, Board finishedBoard) {
        SimpleColor[][] replayBoardColors = replayBoard.getColors();
        SimpleColor[][] finishedBoardColors = finishedBoard.getColors();
        boolean anyFailed = false;
        for (int y = 0; y < replayBoardColors.length; y++) {
            for (int x = 0; x < replayBoardColors[0].length; x++) {
                SimpleColor replayColor = replayBoardColors[y][x];
                SimpleColor finishedColor = finishedBoardColors[y][x];
                if (Objects.equals(replayColor.getColor(), finishedColor.getColor())) {
                    LOGGER.debug("EQUAL content at " + y + " " + x + " is " + replayColor.getColor());
                } else {
                    anyFailed = true;
                    LOGGER.warn("NON-EQUAL content at " + y + " " + x + ": " +
                            "App says " + finishedColor.getColor() + ", Messages say " + replayColor.getColor());
                }
            }
        }
        if (!anyFailed) {
            LOGGER.info("Test successful, thePlace is consistent");
        }
    }
}
