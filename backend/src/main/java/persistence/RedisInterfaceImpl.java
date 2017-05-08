package persistence;

import redis.clients.jedis.Jedis;
import redis.clients.util.SafeEncoder;

import java.awt.*;
import java.util.Arrays;

import static redis.clients.util.SafeEncoder.encode;

public class RedisInterfaceImpl implements RedisInterface {

    public static final String COLORS = "colors";
    private final Jedis jedis;

    public RedisInterfaceImpl() {
        this.jedis = new Jedis("localhost");
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    @Override
    public void setPixel(BoardDimensions dimensions, int x, int y, Color color) {
        initBoardIfNotExists(dimensions);
        byte[] bytes = {(byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue()};
        System.out.println(Arrays.toString(bytes));
        int offset = Board.calculateOffset(dimensions, x, y);
        System.out.println(offset);
        this.jedis.bitfield("colors", "set", "u32", offset+"", SafeEncoder.encode(bytes));
//        this.jedis.bitfield(encode("colors"), encode("set"), encode("u24"), offset, bytes);
    }

    private void initBoardIfNotExists(BoardDimensions dimensions) {
        if (!jedis.exists(COLORS)) {
            byte[] bytes = new byte[dimensions.getSizeInBytes()];
            System.out.println("Creating board with "+ bytes.length);
            jedis.set(COLORS, SafeEncoder.encode(bytes));
        }
    }

    @Override
    public void resetBoard() {
        jedis.del(COLORS);
    }

    @Override
    public Board getBoard() {
        byte[] colors = jedis.get(encode("colors"));
        return new Board(BoardDimensions.DEFAULT, colors);
    }
}
