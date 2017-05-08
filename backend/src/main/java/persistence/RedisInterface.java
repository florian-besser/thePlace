package persistence;

import java.awt.*;

public interface RedisInterface {

    /**
     * Initializes board if it does not exist
     */
    void setPixel(BoardDimensions dimensions, int x, int y, Color color);

    void resetBoard();

    Board getBoard();

}
