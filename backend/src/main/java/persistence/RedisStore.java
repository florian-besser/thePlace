package persistence;

import foo.bar.board.BoardDimensions;
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

    public RedisStore() {
    }

    public void setPixel(BoardDimensions dimensions, int x, int y, Color color) {
        try (Jedis jedis = pool.getResource()) {
            initBoardIfNotExists(jedis, dimensions);
            int offset = Board.calculateOffset(dimensions, x, y) * 8;
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

    //Test Only
    public Board getBoard() {
        try (Jedis jedis = pool.getResource()) {
            byte[] colors = jedis.get(encode("colors"));
            return new Board(BoardDimensions.DEFAULT, colors);
        }
    }

    public Color[][] getBoardColors(BoardDimensions boardDimensions) {
        try (Jedis jedis = pool.getResource()) {
            byte[] colors = jedis.get(encode("colors"));
            return new Board(boardDimensions, colors).getColors();
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
