package foo.bar.client;

import foo.bar.RandomBotConfig;

public class RandomPixelPutter extends PixelPutter {
    public RandomPixelPutter(RandomBotConfig config, int xMax, int yMax) {
        super(config, xMax, yMax);
    }

    protected String getColor(int x, int y) {
        return getRandomHexString(6);
    }

    private String getRandomHexString(int numchars) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(current.nextInt(0, 16)));
        }

        return sb.toString().substring(0, numchars);
    }
}
