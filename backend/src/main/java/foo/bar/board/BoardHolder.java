package foo.bar.board;

import foo.bar.model.Board;
import foo.bar.model.BoardDimensions;
import foo.bar.model.SimpleColor;
import persistence.RedisStore;

public class BoardHolder {
    private static Board THE_BOARD;

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
        BoardDimensions boardDimensions = new BoardDimensions(800, 800);

        // Read from Redis
        RedisStore redisStore = new RedisStore();
        redisStore.resetBoard(boardDimensions);
        SimpleColor[][] boardColors = redisStore.getBoardColors(boardDimensions);

        THE_BOARD = new Board(boardColors);
    }
}
