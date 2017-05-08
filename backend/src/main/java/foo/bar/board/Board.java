package foo.bar.board;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import foo.bar.websocket.EventSocket;
import foo.bar.websocket.PooledSessionCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.RedisStore;

import java.util.Set;

public class Board {
    private static final Logger LOGGER = LoggerFactory.getLogger(Board.class);

    public static Board DEFAULT = new Board(4, 4);

    private final int xMaximum;
    private final int yMaximum;
    // HEX Strings
    private final Pixel[] pixels;

    private final RedisStore redisStore = new RedisStore();

    public Board(int xMaximum, int yMaximum) {
        redisStore.resetBoard();
        this.xMaximum = xMaximum;
        this.yMaximum = yMaximum;
        this.pixels = new Pixel[xMaximum * yMaximum];
        // Set everything to black
        for (int i = 0; i < pixels.length; i++) {
            int x = i % xMaximum;
            int y = i / xMaximum;
            String color = "000000";
            LOGGER.info("Creating Pixel at " + x + " " + y + " with Color " + color);
            pixels[i] = new Pixel(x, y, color);
        }
    }

    public int getXMaximum() {
        return xMaximum;
    }

    public int getYMaximum() {
        return yMaximum;
    }

    public Pixel[] getPixels() {
        return pixels;
    }

    public void setPixel(Pixel toSet) {
        LOGGER.info("Updating Pixel LOCALLY at " + toSet.getX() + " " + toSet.getY() +
                " with Color " + toSet.getColor());

        // Change actual Pixel color
        int index = toSet.getX() + toSet.getY() * xMaximum;
        pixels[index] = toSet;

        // Update all connected clients
        Set<EventSocket> websockets = PooledSessionCreator.getWebsockets();
        String toSetStr = serialize(toSet);
        websockets.parallelStream().forEach(eventSocket -> eventSocket.sendMessage(toSetStr));
    }

    private String serialize(Pixel toSet) {
        ObjectMapper mapper = new ObjectMapper();
        String toSetStr;
        try {
            toSetStr = mapper.writeValueAsString(toSet);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error wile converting Pixel to String.", e);
        }
        return toSetStr;
    }
}
