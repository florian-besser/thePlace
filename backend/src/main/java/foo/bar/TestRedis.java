package foo.bar;

import persistence.BoardDimensions;
import persistence.RedisInterfaceImpl;
import redis.clients.jedis.Jedis;

import java.awt.*;

public class TestRedis {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");

        System.out.println(jedis.get("colors"));


        RedisInterfaceImpl redisInterface = new RedisInterfaceImpl();
        redisInterface.resetBoard();
        for (int x = 0; x < BoardDimensions.DEFAULT.getXMaximum(); x++) {
            for (int y = 0; y < BoardDimensions.DEFAULT.getYMaximum(); y++) {
                System.out.println("pixel " + x + " " + y);
                redisInterface.setPixel(BoardDimensions.DEFAULT, x, y, Color.blue);
                System.out.println(redisInterface.getBoard());
            }
        }

        System.out.println(redisInterface.isUserAllowed("stivo"));
        redisInterface.userHasSetPixel("stivo");
        System.out.println(redisInterface.isUserAllowed("stivo"));
        System.out.println(Color.decode("#ffffff"));
    }
}
