package foo.bar.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;

public class BoardCanvasDiff extends Canvas {
    private final BoardCanvas boardCanvasRedis;
    private final BoardCanvas boardCanvasWS;

    private final BufferedImage image;
    public BoardCanvasDiff(BoardCanvas boardCanvasRedis, BoardCanvas boardCanvasWS) {

        this.boardCanvasRedis = boardCanvasRedis;
        this.boardCanvasWS = boardCanvasWS;
        image = deepCopy(boardCanvasRedis.getBufferedImage());
    }

    @Override
    public void paint(Graphics g) {
        computeDiff(boardCanvasRedis.getBufferedImage(), boardCanvasWS.getBufferedImage());
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    }


    @Override
    public Dimension getPreferredSize() {
        return getParent().getSize();
    }

    private void computeDiff(BufferedImage bufferedImage, BufferedImage bufferedImage1) {
        getDifferenceImage(bufferedImage1, bufferedImage);
    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private void getDifferenceImage(BufferedImage img1, BufferedImage img2) {

        int width1 = img1.getWidth();
        int height1 = img1.getHeight();
        // Modified - Changed to int as pixels are ints
        int diff;
        int result; // Stores output pixel
        for (int i = 0; i < height1; i++) {
            for (int j = 0; j < width1; j++) {
                int rgb1 = img1.getRGB(j, i);
                int rgb2 = img2.getRGB(j, i);
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = (rgb1) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = (rgb2) & 0xff;
                diff = Math.abs(r1 - r2); // Change
                diff += Math.abs(g1 - g2);
                diff += Math.abs(b1 - b2);
                diff /= 3; // Change - Ensure result is between 0 - 255
                // Make the difference image gray scale
                // The RGB components are all the same
                result = (diff << 16) | (diff << 8) | diff;
                image.setRGB(j, i, result); // Set result
            }
        }
    }
}
