package foo.bar.client;

import foo.bar.RandomBotConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class PixelPutterFactory {


    public static PixelPutterFactory IMAGE = new PixelPutterFactory() {
        @Override
        public List<Thread> createPixelPutters(RandomBotConfig config, int xMax, int yMax, String s) throws IOException {
            List<Thread> threads = new ArrayList<>(config.getRequesterThreads());
            for (int t = 0; t < config.getRequesterThreads(); t++) {
                Thread thread = new Thread(new ImagePixelPutter(config, t, xMax, yMax, "lena.jpg"));
                thread.start();
                threads.add(thread);
            }
            return threads;
        }
    };

    public static PixelPutterFactory RANDOM = new PixelPutterFactory() {
        @Override
        public List<Thread> createPixelPutters(RandomBotConfig config, int xMax, int yMax, String s) throws IOException {
            List<Thread> threads = new ArrayList<>(config.getRequesterThreads());
            for (int t = 0; t < config.getRequesterThreads(); t++) {
                Thread thread = new Thread(new RandomPixelPutter(config, xMax, yMax));
                thread.start();
                threads.add(thread);
            }
            return threads;
        }
    };


    public abstract List<Thread> createPixelPutters(RandomBotConfig config, int xMax, int yMax, String s)
            throws IOException;
}
