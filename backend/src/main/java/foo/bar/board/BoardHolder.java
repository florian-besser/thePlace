package foo.bar.board;

import foo.bar.model.Board;
import foo.bar.model.BoardDimensions;
import foo.bar.model.SimpleColor;
import persistence.RedisStore;

public class BoardHolder {
    private static Board THE_BOARD;
    public static final BoardDimensions BOARD_DIMENSIONS = new BoardDimensions(500, 500);

    public static Board getInstance() {
        if (THE_BOARD == null) {
            setBoard();
        }
        return THE_BOARD;
    }

    private synchronized static void setBoard() {
        if (THE_BOARD != null) {
            return;
        }

        // Read from Redis
        RedisStore redisStore = new RedisStore();
        redisStore.resetBoard(BOARD_DIMENSIONS);
        SimpleColor[][] boardColors = redisStore.getBoardColors(BOARD_DIMENSIONS);

        THE_BOARD = new Board(boardColors);
    }
}
