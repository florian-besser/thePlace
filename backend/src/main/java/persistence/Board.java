package persistence;

import foo.bar.board.BoardDimensions;

import java.awt.*;
import java.util.Arrays;

public class Board {

    public static int BYTES_PER_COLOR = 3;
    private final BoardDimensions boardDimensions;

    private Color[][] colors;


    public Board(BoardDimensions boardDimensions, byte[] colorsInBytes) {
        this.boardDimensions = boardDimensions;
        int yMax = boardDimensions.getYMaximum();
        int xMax = boardDimensions.getXMaximum();
        colors = new Color[xMax][yMax];
        for (int y = 0; y < yMax; y++) {
            for (int x = 0; x < xMax; x++) {
                int offset = calculateOffset(boardDimensions, x, y);
                Color color = new Color(colorsInBytes[offset] & 0xff, colorsInBytes[offset + 1] & 0xff, colorsInBytes[offset + 2] & 0xff);
                colors[x][y] = color;
            }
        }

    }

    public static int calculateOffset(BoardDimensions dimensions, int x, int y) {
        return (y * dimensions.getXMaximum() + x) * BYTES_PER_COLOR;
    }

    public Color[][] getColors() {
        return colors;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int x = 0; x < boardDimensions.getXMaximum(); x++) {
            stringBuilder.append(Arrays.toString(colors[x])).append("\n");
        }
        return stringBuilder.toString();
    }
}
