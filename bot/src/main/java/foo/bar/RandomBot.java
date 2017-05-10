package foo.bar;

import foo.bar.client.RandomPixelPutter;
import foo.bar.config.Config;
import foo.bar.model.Board;
import foo.bar.model.Pixel;
import foo.bar.model.SimpleColor;
import foo.bar.util.EventualExecutor;
import foo.bar.websocket.EventSocketCounter;
import foo.bar.websocket.EventSocketListener;
import foo.bar.websocket.WebsocketFactory;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RandomBot {
    public static final String TARGET_HOST = Config.getBackendTargetHost() + ":2222";

    private static final Logger LOGGER = LoggerFactory.getLogger(RandomBot.class);
    private static final RandomBotConfig config = RandomBotConfig.valueOf(Config.getBotConfig());
    private static Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFeature.class));

    public static void main(String[] args) throws Exception {
        EventSocketListener listener = WebsocketFactory.getListenerInstance();

        LOGGER.info("Reading initial board");
        Board originalBoard = getBoard();

        LOGGER.info("Creating clients");
        List<EventSocketCounter> clients = new ArrayList<>(config.getClientThreads());
        for (int i = 0; i < config.getClientThreads(); i++) {
            clients.add(WebsocketFactory.getCounterInstance());
        }

        LOGGER.info("Starting load test");
        SimpleColor[][] colors = originalBoard.getColors();
        int xMax = colors[0].length;
        int yMax = colors.length;
        List<Thread> threads = new ArrayList<>(config.getRequesterThreads());
        for (int t = 0; t < config.getRequesterThreads(); t++) {
            Thread thread = new Thread(new RandomPixelPutter(config, xMax, yMax));
            thread.start();
            threads.add(thread);
        }

        // Await
        for (Thread t : threads) {
            t.join();
        }
        LOGGER.info("Finished load test");
        Thread.sleep(5000);

        assertEqualMessages(listener, clients);
        Board finishedBoard = getBoard();
        replayMessagesOn(originalBoard, listener.getSetPixels());
        assertEquals(originalBoard, finishedBoard);

        // Close
        listener.getSession().close();
        for (EventSocketCounter c : clients) {
            c.getSession().close();
        }
        client.close();
        WebsocketFactory.shutdown();

        LOGGER.info("Closed stuff");
    }

    private static Board getBoard() {
        WebTarget webTarget = client.target("http://" + TARGET_HOST + "/rest/thePlace").path("place");

        EventualExecutor<WebTarget, Response> exec = new EventualExecutor<>();
        Response response = exec.tryExecute(RandomBot::getResponse, webTarget);

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

    private static Response getResponse(WebTarget webTarget) {
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        if (response.getStatus() >= 400) {
            throw new RuntimeException("Received " + response.getStatus() + " status code when doing REST.");
        }
        return response;
    }

    private static void replayMessagesOn(Board originalBoard, List<Pixel> pixels) {
        for (Pixel p : pixels) {
            originalBoard.setPixelInternal(p);
        }
    }

    private static void assertEqualMessages(EventSocketListener socket, List<EventSocketCounter> clients) {
        boolean anyFailed = false;
        int expected = socket.getMsgsReceived();
        for (EventSocketCounter c : clients) {
            int actual = c.getMsgsReceived();
            if (actual != expected) {
                anyFailed = true;
                LOGGER.warn("A client did not receive all messages. Expected " + expected + " actual " + actual);
            }
        }
        if (!anyFailed) {
            LOGGER.info("No messages dropped");
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
