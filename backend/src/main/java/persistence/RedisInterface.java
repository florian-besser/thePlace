package persistence;

import java.awt.*;

public interface RedisInterface {

    void setPixel(int x, int y, Color color);
    Board getBoard();

}
