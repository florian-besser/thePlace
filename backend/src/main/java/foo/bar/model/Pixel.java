package foo.bar.model;

public class Pixel {

    private int x;
    private int y;
    private SimpleColor color;

    public Pixel() {
        // Only to be used from Jackson
    }

    public Pixel(int x, int y, SimpleColor simpleColor) {
        this.x = x;
        this.y = y;
        this.color = simpleColor;
    }

    public int getX() {
        return x;
    }

    // Only to be used from Jackson
    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    // Only to be used from Jackson
    public void setY(int y) {
        this.y = y;
    }

    public SimpleColor getColor() {
        return color;
    }

    // Only to be used from Jackson
    public void setColor(SimpleColor color) {
        this.color = color;
    }
}
