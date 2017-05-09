package foo.bar.client;

import foo.bar.RandomBotConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;


public class ImagePixelPutter extends PixelPutter {

    private BufferedImage bufferedImage;

    public ImagePixelPutter(RandomBotConfig config, int xMax, int yMax, String imageName) throws IOException {
        super(config, xMax, yMax);

        URL imageUrl = getClass().getResource("/" + imageName);
        bufferedImage = ImageIO.read(imageUrl);
        bufferedImage = rescale(bufferedImage);
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
