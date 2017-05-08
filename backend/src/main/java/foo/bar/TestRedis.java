package foo.bar;

import foo.bar.board.BoardDimensions;
import persistence.RedisStore;
import redis.clients.jedis.Jedis;

import java.awt.*;

public class TestRedis {

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

        System.out.println(redisInterface.isUserAllowed("stivo"));
        redisInterface.userHasSetPixel("stivo");
        System.out.println(redisInterface.isUserAllowed("stivo"));
        System.out.println(Color.decode("#ffffff"));
    }
}
