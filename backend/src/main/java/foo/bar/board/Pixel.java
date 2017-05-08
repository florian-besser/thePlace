package foo.bar.board;

public class Pixel {

    private int x;
    private int y;
    private String color;

    public Pixel() {
        // Only to be used from Jackson
    }

    public Pixel(int x, int y, String color) {
        this.x = x;
        this.y = y;
        this.color = color;
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

    public String getColor() {
        return color;
    }

    // Only to be used from Jackson
    public void setColor(String color) {
        this.color = color;
    }
}
