package foo.bar.board;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import foo.bar.model.Pixel;
import foo.bar.model.SimpleColor;
import foo.bar.websocket.EventSocket;
import foo.bar.websocket.PooledSessionCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.RedisStore;

import java.util.Set;

public class Board {
    private static final Logger LOGGER = LoggerFactory.getLogger(Board.class);

    public static Board THE_BOARD = new Board(4, 4);

    // [Y][X]
    private SimpleColor[][] colors;

    private Board() {
        // Used by Jackson
    }

    private Board(int xMaximum, int yMaximum) {
        BoardDimensions boardDimensions = new BoardDimensions(xMaximum, yMaximum);

        // Read from Redis
        RedisStore redisStore = new RedisStore();
        redisStore.resetBoard(boardDimensions);
        this.colors = redisStore.getBoardColors(boardDimensions);
    }

    // Used by Jackson
    public SimpleColor[][] getColors() {
        return colors;
    }

    // Used by Jackson
    public void setColors(SimpleColor[][] colors) {
        this.colors = colors;
    }

    public void setPixel(Pixel toSet) {
        LOGGER.info("Updating Pixel LOCALLY at " + toSet.getX() + " " + toSet.getY() +
                " with Color " + toSet.getColor());

        // Change actual Pixel color
        colors[toSet.getY()][toSet.getX()] = toSet.getColor();

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
