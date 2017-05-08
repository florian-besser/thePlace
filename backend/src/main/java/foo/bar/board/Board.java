package foo.bar.board;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Board {

    public static Board DEFAULT = new Board(4, 4);

    private final int xMaximum;
    private final int yMaximum;
    // HEX Strings
    private final Pixel[] pixels;
    private final Map<String, LocalDateTime> userChanges;

    public Board(int xMaximum, int yMaximum) {
        this.xMaximum = xMaximum;
        this.yMaximum = yMaximum;
        this.pixels = new Pixel[xMaximum * yMaximum];
        this.userChanges = new HashMap<>();
        // Set everything to black
        for (int i = 0; i < pixels.length; i++) {
            int x = i % xMaximum;
            int y = i / xMaximum;
            String color = "000000";
            System.out.println("Creating Pixel at " + x + " " + y + " with Color " + color);
            pixels[i] = new Pixel(x, y, color);
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

    public void setPixel(Pixel toSet, String user) {
        if (!isAllowed(user)) {
            System.out.println("NOT updating Pixel at " + toSet.getX() + " " + toSet.getY() +
                    " with Color " + toSet.getColor() + " for user " + user);
            return;
        }

        System.out.println("Updating Pixel at " + toSet.getX() + " " + toSet.getY() +
                " with Color " + toSet.getColor() + " for user " + user);

        // Forbid user to change more Pixels for 5 minutes
        userChanges.put(user, LocalDateTime.now());

        // Change actual Pixel color
        int index = toSet.getX() + toSet.getY() * xMaximum;
        pixels[index] = toSet;
    }

    private boolean isAllowed(String user) {
        return userChanges.get(user) == null ||
                userChanges.get(user).isBefore(LocalDateTime.now().minusMinutes(5));
    }
}
