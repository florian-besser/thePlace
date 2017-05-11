package foo.bar.client;

import foo.bar.RandomBotConfig;
import foo.bar.model.Pixel;
import foo.bar.model.SimpleColor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;


public class ImagePixelPutter extends PixelPutter {

    private BufferedImage bufferedImage;
    int x;
    int y = 0;
    int xEnd;
    int xStart;


    public ImagePixelPutter(RandomBotConfig config, int id, int xMax, int yMax, String imageName) throws IOException {
        super(config, xMax, yMax);

        URL imageUrl = getClass().getResource("/" + imageName);
        bufferedImage = ImageIO.read(imageUrl);
        bufferedImage = rescale(bufferedImage);
        int width = xMax / config.getRequesterThreads();
        xStart = Math.max(id * width - 1, 0);
        x = xStart;
        xEnd = Math.min((id + 1) * width + 1, xMax);
    }

    @Override
    protected Pixel getNextPixel() {
        Pixel pixel = new Pixel(x, y, new SimpleColor(getColor(x, y)));
        x++;
        if (x == xEnd) {
            y += 1;
            x = xStart;
        }
        return pixel;
    }

    private BufferedImage rescale(BufferedImage original) {
        BufferedImage resized = new BufferedImage(getXMax(), getYMax(), original.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, getXMax(), getYMax(), 0, 0, original.getWidth(),
                original.getHeight(), null);
        g.dispose();
        return resized;
    }

    protected String getColor(int x, int y) {
        int rgb = bufferedImage.getRGB(x, y) & 0xffffff;
        return "#" + Integer.toHexString(rgb);
    }
}
