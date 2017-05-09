package foo.bar.client;

import foo.bar.RandomBotConfig;
import foo.bar.model.Pixel;
import foo.bar.model.SimpleColor;

public class RandomPixelPutter extends PixelPutter {
    public RandomPixelPutter(RandomBotConfig config, int xMax, int yMax) {
        super(config, xMax, yMax);
    }

    private String getColor() {
        return "#" + getRandomHexString(6);
    }

    private String getRandomHexString(int numchars) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(current.nextInt(0, 16)));
        }

        return sb.toString().substring(0, numchars);
    }

    @Override
    protected Pixel getNextPixel() {
        int x = current.nextInt(0, getXMax());
        int y = current.nextInt(0, getYMax());
        return new Pixel(x, y, new SimpleColor(getColor()));
    }
}
