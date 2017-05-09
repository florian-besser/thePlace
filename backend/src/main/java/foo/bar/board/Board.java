package foo.bar.board;

import foo.bar.model.Pixel;
import foo.bar.model.SimpleColor;
import foo.bar.websocket.UpdateBatching;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.RedisStore;

public class Board {
    private static final Logger LOGGER = LoggerFactory.getLogger(Board.class);

    public static Board THE_BOARD = new Board(50, 50);

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
        setPixelInternal(toSet);

        UpdateBatching.getInstance().addUpdate(toSet);
    }

    // Only to be used from this class and Bots!
    public void setPixelInternal(Pixel toSet) {
        colors[toSet.getY()][toSet.getX()] = toSet.getColor();
    }

}
