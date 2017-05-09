package foo.bar;

import foo.bar.board.Board;
import foo.bar.client.PixelPutter;
import foo.bar.model.Pixel;
import foo.bar.model.SimpleColor;
import foo.bar.websocket.EventSocketListener;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

public class RandomBot {
    public static final String TARGET_HOST = "localhost:2222";
    public static final int MAX_REQUESTS = 1000;
    public static final int THREADS = 10;
    public static final int MAX_REQUESTS_PER_SECOND_PER_THREAD = 1000;
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomBot.class);
    private static Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFeature.class));

    public static void main(String[] args) throws Exception {
        EventSocketListener socket = getSocket();
        Board originalBoard = getBoard();
        SimpleColor[][] colors = originalBoard.getColors();
        int xMax = colors[0].length;
        int yMax = colors.length;
        List<Thread> threads = new ArrayList<>(THREADS);

        LOGGER.info("Starting load test");
        for (int t = 0; t < THREADS; t++) {
            Thread thread = new Thread(new PixelPutter(xMax, yMax));
            thread.start();
            threads.add(thread);
        }

        // Await
        for (Thread t : threads) {
            t.join();
        }
        LOGGER.info("Finished load test");
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
