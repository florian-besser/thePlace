package foo.bar.model;

public class BoardDimensions {

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

}
