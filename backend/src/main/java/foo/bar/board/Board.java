package foo.bar.board;

public class Board {

    public static Board DEFAULT = new Board(4, 4);

    private final int xMaximum;
    private final int yMaximum;
    // HEX Strings
    private final Pixel[] pixels;

    public Board(int xMaximum, int yMaximum) {
        this.xMaximum = xMaximum;
        this.yMaximum = yMaximum;
        this.pixels = new Pixel[xMaximum * yMaximum];
        // Set everything to black
        for (int i = 0; i < pixels.length; i++) {
            int x = i % xMaximum;
            int y = i / xMaximum;
            String color = "000000";
            System.out.println("Creating Pixel at " + x + " " + y + " with Color " + color);
            pixels[i] = new Pixel(x, y, color, "");
        }
    }

    public int getXMaximum() {
        return xMaximum;
    }

    public int getYMaximum() {
        return yMaximum;
    }

    public Pixel[] getPixels() {
        return pixels;
    }

    public void setPixel(Pixel toSet) {
        int index = toSet.getX() + toSet.getY() * xMaximum;
        pixels[index] = toSet;
    }
}
