package persistence;

import java.awt.*;
import java.util.Arrays;

public class Board {

    public static int BYTES_PER_COLOR = 3;

    private Color[][] colors;


    public Board(BoardDimensions boardDimensions, byte[] colorsInBytes) {
        int yMax = boardDimensions.getYMaximum();
        int xMax = boardDimensions.getXMaximum();
        colors = new Color[xMax][yMax];
        for (int y = 0; y < yMax; y++) {
            for (int x = 0; x < xMax; x++) {
                int offset = calculateOffset(boardDimensions, y, x);

                Color color = new Color(
                        colorsInBytes[offset],
                        colorsInBytes[offset + 1],
                        colorsInBytes[offset + 2]);
                colors[x][y] = color;
            }
        }

    }

    public static int calculateOffset(BoardDimensions dimensions, int x, int y) {
        return (y * dimensions.getXMaximum() + x) * BYTES_PER_COLOR;
    }

    @Override
    public String toString() {
        return Arrays.toString(colors);
    }
}
