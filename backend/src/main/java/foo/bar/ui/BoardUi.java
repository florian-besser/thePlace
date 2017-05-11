package foo.bar.ui;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import foo.bar.board.BoardHolder;
import foo.bar.model.Pixel;
import foo.bar.model.SimpleColor;
import foo.bar.monitoring.Monitoring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.RedisStore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import static foo.bar.ui.BoardUi.DRAW;

public class BoardUi {
    private static final Logger LOGGER = LoggerFactory.getLogger(BoardUi.class);

    private BoardCanvas boardCanvas;
    public static final MetricRegistry registry = new MetricRegistry();


    private final RedisStore redisStore = new RedisStore();
    public static final com.codahale.metrics.Timer DRAW = registry.timer("draw");
    public static final com.codahale.metrics.Timer READ = registry.timer("read");

    public static void main(String[] args) {
        final Slf4jReporter slf4jReporter = Slf4jReporter.forRegistry(registry)
                .outputTo(LoggerFactory.getLogger(Monitoring.class))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        slf4jReporter.start(1, TimeUnit.SECONDS);

        new BoardUi().start();
    }

    public void start() {
        JFrame jFrame = new JFrame();
        boardCanvas = new BoardCanvas();
        jFrame.add(boardCanvas);
        jFrame.setTitle("Redis Content");
        jFrame.setSize(1000, 1000);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        new Timer(250, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LOGGER.info("Updating");
                com.codahale.metrics.Timer.Context time = READ.time();
                byte[] colors = redisStore.getRgbImage();
                time.stop();
                boardCanvas.paintColors(colors);
            }
        }).start();
    }

    public void updateBoard(Pixel pixel) {

        boardCanvas.paintPixel(pixel);
    }
}

class BoardCanvas extends Canvas {

    int scaling = 1;

    int xMax = BoardHolder.BOARD_DIMENSIONS.getXMaximum();
    int yMax = BoardHolder.BOARD_DIMENSIONS.getYMaximum();

    BufferedImage bufferedImage = new BufferedImage(xMax, yMax, BufferedImage.TYPE_INT_RGB);

    private void drawPixel(Graphics g, SimpleColor simpleColor, int x, int y) {
        g.setColor(Color.decode(simpleColor.getColor()));
        g.fillRect(x * scaling, y * scaling, scaling, scaling);
    }

    public void paintPixel(Pixel pixel) {
        drawPixel(getGraphics(), pixel.getColor(), pixel.getX(), pixel.getY());
    }


    public void paintColors(SimpleColor[][] boardColors) {
        com.codahale.metrics.Timer.Context time = DRAW.time();

        int[] ints = new int[xMax * yMax * 3];

        int offset = 0;
        for (int x = 0; x < xMax; x++) {
            for (int y = 0; y < yMax; y++) {
                int[] asIntArray = boardColors[y][x].getAsIntArray();
                System.arraycopy(asIntArray, 0, ints, offset, 3);

                offset += 3;
            }
        }

        bufferedImage.getRaster().setPixels(0, 0, xMax, yMax, ints);
        getGraphics().drawImage(bufferedImage, 0, 0, null);
        time.stop();

    }

    public void paintColors(byte[] colors) {

        com.codahale.metrics.Timer.Context time = DRAW.time();

        int[] ints = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            ints[i] = colors[i];
        }

        bufferedImage.getRaster().setPixels(0, 0, xMax, yMax, ints);
        getGraphics().drawImage(bufferedImage, 0, 0, null);
        time.stop();
    }
}