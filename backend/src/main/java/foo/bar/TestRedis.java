package foo.bar;

import redis.clients.jedis.Jedis;

public class TestRedis {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");
        jedis.set("foo", "bar");
        String value = jedis.get("foo");
        System.out.println(value);
        System.out.println(jedis.get("asdf"));
        System.out.println(jedis.get("asdf2"));
    }
}
