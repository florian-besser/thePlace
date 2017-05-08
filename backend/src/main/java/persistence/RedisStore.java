package persistence;

import foo.bar.board.BoardDimensions;
import foo.bar.board.SimpleColor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.SafeEncoder;

import java.awt.*;

import static redis.clients.util.SafeEncoder.encode;

public class RedisStore {

    public static final String COLORS = "colors";
    public static final int SECONDS = 300;

    private static final JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
    public static int BYTES_PER_COLOR = 3;

    public RedisStore() {
    }

    private static int calculateOffset(BoardDimensions dimensions, int x, int y) {
        return (y * dimensions.getXMaximum() + x) * BYTES_PER_COLOR;
    }

    public void setPixel(BoardDimensions dimensions, int x, int y, Color color) {
        try (Jedis jedis = pool.getResource()) {
            initBoardIfNotExists(jedis, dimensions);
            int offset = calculateOffset(dimensions, x, y) * 8;
            int i = color.getRGB() & 0xffffff;
//        System.out.println(offset + " " + i);
            jedis.bitfield("colors", "set", "u24", offset + "", i + "");
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
            byte[] bytes = new byte[dimensions.getSizeInBytes()];
//            System.out.println("Creating board with " + bytes.length + " bytes size");
            jedis.set(COLORS, SafeEncoder.encode(bytes));
        }
    }

    public SimpleColor[][] getBoardColors(BoardDimensions boardDimensions) {
        try (Jedis jedis = pool.getResource()) {
            byte[] colorsInBytes = jedis.get(encode("colors"));
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
                    colors[x][y] = color;
                }
            }
            return colors;
        }
    }

    public boolean tryToSetPixel(String userId) {
        try (Jedis jedis = pool.getResource()) {
            String key = "user_" + userId;
            Long setnx = jedis.setnx(key, "user set pixel");
            if (setnx == 0) {
                return false;
            } else {
                jedis.expire(key, SECONDS);
                return true;
            }
        }
    }

}
