package persistence;

import foo.bar.model.BoardDimensions;
import foo.bar.model.SimpleColor;
import foo.bar.rest.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

public class TestRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(Resource.class);

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");

        System.out.println(jedis.get("colors"));


        RedisStore redisInterface = new RedisStore();
        BoardDimensions boardDimensions = new BoardDimensions(2, 2);
        redisInterface.resetBoard(boardDimensions);
        for (int x = 0; x < boardDimensions.getXMaximum(); x++) {
            for (int y = 0; y < boardDimensions.getYMaximum(); y++) {
                System.out.println("pixel " + x + " " + y);
                redisInterface.setPixel(boardDimensions, x, y, new SimpleColor("0000ff"));
                SimpleColor[][] boardColors = redisInterface.getBoardColors(boardDimensions);
                for (SimpleColor[] colors : boardColors) {
                    for (SimpleColor c : colors) {
                        System.out.println(c);
                    }
                }
            }
        }

        LOGGER.info("" + redisInterface.tryToSetPixel("stivo"));
        LOGGER.info("" + redisInterface.tryToSetPixel("stivo"));

    }
}
