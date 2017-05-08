package foo.bar.board;

public class BoardDimensions {

    public static BoardDimensions DEFAULT = new BoardDimensions(2, 2);

    private final int xMaximum;
    private final int yMaximum;

    public BoardDimensions(int xMaximum, int yMaximum) {
        this.xMaximum = xMaximum;
        this.yMaximum = yMaximum;
    }

    public int getXMaximum() {
        return xMaximum;
    }

    public int getYMaximum() {
        return yMaximum;
    }

    public int getSizeInBytes() {
        return xMaximum * yMaximum * persistence.Board.BYTES_PER_COLOR;
    }
}
