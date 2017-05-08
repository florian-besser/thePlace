package persistence;

import foo.bar.board.BoardDimensions;
import redis.clients.jedis.Jedis;
import redis.clients.util.SafeEncoder;

import java.awt.*;

import static redis.clients.util.SafeEncoder.encode;

public class RedisStore {

    public static final String COLORS = "colors";
    public static final int SECONDS = 300;
    private final Jedis jedis;

    public RedisStore() {
        this.jedis = new Jedis("localhost");
    }

    public void setPixel(BoardDimensions dimensions, int x, int y, Color color) {
        initBoardIfNotExists(dimensions);
        int offset = Board.calculateOffset(dimensions, x, y) * 8;
        int i = color.getRGB() & 0xffffff;
//        System.out.println(offset + " " + i);
        this.jedis.bitfield("colors", "set", "u24", offset + "", i + "");
    }

    public void resetBoard(BoardDimensions boardDimensions) {
        jedis.del(COLORS);
        initBoardIfNotExists(boardDimensions);
    }

    private void initBoardIfNotExists(BoardDimensions dimensions) {
        if (!jedis.exists(COLORS)) {
            byte[] bytes = new byte[dimensions.getSizeInBytes()];
//            System.out.println("Creating board with " + bytes.length + " bytes size");
            jedis.set(COLORS, SafeEncoder.encode(bytes));
        }
    }

    //Test Only
    public Board getBoard() {
        byte[] colors = jedis.get(encode("colors"));
        return new Board(BoardDimensions.DEFAULT, colors);
    }

    public Color[][] getBoardColors(BoardDimensions boardDimensions) {
        byte[] colors = jedis.get(encode("colors"));
        return new Board(boardDimensions, colors).getColors();
    }

    public boolean isUserAllowed(String userId) {
        return jedis.ttl("user_" + userId) < 0;
    }

    public void userHasSetPixel(String userId) {
        jedis.set("user_" + userId, "asdf");
        jedis.expire("user_" + userId, SECONDS);
    }

}
