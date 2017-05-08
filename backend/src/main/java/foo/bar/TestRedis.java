package foo.bar;

import foo.bar.rest.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import foo.bar.board.BoardDimensions;
import persistence.RedisStore;
import redis.clients.jedis.Jedis;

import java.awt.*;

public class TestRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(Resource.class);

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");

        System.out.println(jedis.get("colors"));


        RedisStore redisInterface = new RedisStore();
        BoardDimensions boardDimensions = BoardDimensions.DEFAULT;
        redisInterface.resetBoard(boardDimensions);
        for (int x = 0; x < boardDimensions.getXMaximum(); x++) {
            for (int y = 0; y < boardDimensions.getYMaximum(); y++) {
                System.out.println("pixel " + x + " " + y);
                redisInterface.setPixel(boardDimensions, x, y, Color.blue);
                System.out.println(redisInterface.getBoard());
            }
        }

        LOGGER.info("" + redisInterface.tryToSetPixel("stivo"));
        LOGGER.info("" + redisInterface.tryToSetPixel("stivo"));

    }
}
