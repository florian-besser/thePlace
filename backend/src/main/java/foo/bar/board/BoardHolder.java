package foo.bar.board;

import foo.bar.model.Board;
import foo.bar.model.BoardDimensions;
import foo.bar.model.SimpleColor;
import foo.bar.ui.BoardUi;
import persistence.RedisStore;

public class BoardHolder {
    public static final Board THE_BOARD;
    public static final BoardUi UI;

    static {
        BoardDimensions boardDimensions = new BoardDimensions(1000, 1000);

        // Read from Redis
        RedisStore redisStore = new RedisStore();
        redisStore.resetBoard(boardDimensions);
        SimpleColor[][] boardColors = redisStore.getBoardColors(boardDimensions);

        THE_BOARD = new Board(boardColors);
        UI = new BoardUi();
        UI.start(THE_BOARD);
    }
}
