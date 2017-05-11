package persistence;

import com.codahale.metrics.Timer;
import foo.bar.config.Config;
import foo.bar.model.BoardDimensions;
import foo.bar.model.SimpleColor;
import foo.bar.monitoring.Monitoring;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.SafeEncoder;

import static redis.clients.util.SafeEncoder.encode;

public class RedisStore {

    public static final String COLORS = "colors";
    public static final int SECONDS = 10;

    private static final JedisPool pool = new JedisPool(new JedisPoolConfig(), Config.getRedisTargetHost());
    public static int BYTES_PER_COLOR = 3;
    private static final Timer setPixel = Monitoring.registry.timer("redis.setPixel");
    private static final Timer tryToSetPixel = Monitoring.registry.timer("redis.tryToSetPixel");
    private static final Timer readBoard = Monitoring.registry.timer("redis.getboard");

    public RedisStore() {
    }

    private static int calculateOffset(BoardDimensions dimensions, int x, int y) {
        return (y * dimensions.getXMaximum() + x) * BYTES_PER_COLOR;
    }

    public void setPixel(BoardDimensions dimensions, int x, int y, SimpleColor color) {
        Timer.Context time = setPixel.time();
        try (Jedis jedis = pool.getResource()) {
            initBoardIfNotExists(jedis, dimensions);
            int offset = calculateOffset(dimensions, x, y) * 8;
            String color1 = color.getColor().substring(1);
            int i = Integer.parseInt(color1, 16) & 0xffffff;
//        System.out.println(offset + " " + i);
            jedis.bitfield("colors", "set", "u24", offset + "", i + "");
        } finally {
            time.stop();
        }
    }

    public void resetBoard(BoardDimensions boardDimensions) {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(COLORS);
            initBoardIfNotExists(jedis, boardDimensions);
        }
    }

    private void initBoardIfNotExists(Jedis jedis, BoardDimensions dimensions) {
        if (!jedis.exists(COLORS)) {
            byte[] bytes = new byte[getSizeInBytes(dimensions)];
//            System.out.println("Creating board with " + bytes.length + " bytes size");
            jedis.set(COLORS, SafeEncoder.encode(bytes));
        }
    }

    private int getSizeInBytes(BoardDimensions dimensions) {
        return dimensions.getXMaximum() * dimensions.getYMaximum() * RedisStore.BYTES_PER_COLOR;
    }

    public byte[] getRgbImage() {
        Timer.Context timeRead = readBoard.time();
        try (Jedis jedis = pool.getResource()) {
            return jedis.get(encode("colors"));
        } finally {
            timeRead.stop();
        }
    }

    public SimpleColor[][] getBoardColors(BoardDimensions boardDimensions) {
        byte[] colorsInBytes = getRgbImage();
        int yMax = boardDimensions.getYMaximum();
        int xMax = boardDimensions.getXMaximum();
        SimpleColor[][] colors = new SimpleColor[yMax][xMax];
        for (int y = 0; y < yMax; y++) {
            for (int x = 0; x < xMax; x++) {
                int offset = calculateOffset(boardDimensions, x, y);
                int red = colorsInBytes[offset] & 0xff;
                int green = colorsInBytes[offset + 1] & 0xff;
                int blue = colorsInBytes[offset + 2] & 0xff;
                String hex = String.format("#%02x%02x%02x", red, green, blue);

                SimpleColor color = new SimpleColor(hex);
                colors[y][x] = color;
            }
        }
        return colors;
    }

    public boolean tryToSetPixel(String userId) {
        Timer.Context time = tryToSetPixel.time();
        try (Jedis jedis = pool.getResource()) {
            String key = "user_" + userId;
            Long setnx = jedis.setnx(key, "user set pixel");
            if (setnx == 0) {
                return false;
            } else {
                jedis.expire(key, SECONDS);
                return true;
            }
        } finally {
            time.stop();
        }
    }

    public static JedisPool getPool() {
        return pool;
    }
}
