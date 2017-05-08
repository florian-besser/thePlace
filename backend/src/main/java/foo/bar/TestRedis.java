package foo.bar;

import persistence.BoardDimensions;
import persistence.RedisInterfaceImpl;
import redis.clients.jedis.Jedis;

import java.awt.*;

public class TestRedis {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");
        jedis.set("foo", "bar");
        String value = jedis.get("foo");
        System.out.println(value);
        System.out.println(jedis.get("asdf"));
        System.out.println(jedis.get("asdf2"));


        RedisInterfaceImpl redisInterface = new RedisInterfaceImpl();
        redisInterface.setPixel(BoardDimensions.DEFAULT, 0, 0, Color.blue);
//        System.out.println(redisInterface.getBoard());
    }
}
