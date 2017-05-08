package foo.bar.board;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import foo.bar.websocket.EventSocket;
import foo.bar.websocket.PooledSessionCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.RedisStore;

import java.awt.*;
import java.util.Set;

public class Board {
    private static final Logger LOGGER = LoggerFactory.getLogger(Board.class);

    public static Board THE_BOARD = new Board(4, 4);

    // [Y][X]
    private final SimpleColor[][] colors;

    private Board(int xMaximum, int yMaximum) {
        BoardDimensions boardDimensions = new BoardDimensions(xMaximum, yMaximum);

        this.colors = new SimpleColor[yMaximum][xMaximum];

        // Read from Redis
        RedisStore redisStore = new RedisStore();
        redisStore.resetBoard(boardDimensions);
        Color[][] boardColors = redisStore.getBoardColors(boardDimensions);
        for (int i = 0; i < boardColors.length; i++) {
            for (int j = 0; j < boardColors[i].length; j++) {
                Color color = boardColors[i][j];
                String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
                this.colors[i][j] = new SimpleColor(hex);

            }
        }
    }

    public int getXMaximum() {
        return colors[0].length;
    }

    // Used by Jackson
    public SimpleColor[][] getColors() {
        return colors;
    }

    public int getYMaximum() {
        return colors.length;
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