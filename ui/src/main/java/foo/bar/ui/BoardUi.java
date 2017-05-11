package foo.bar.ui;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.google.common.io.ByteStreams;
import foo.bar.board.BoardHolder;
import foo.bar.config.Config;
import foo.bar.model.Pixel;
import foo.bar.monitoring.Monitoring;
import foo.bar.websocket.WebsocketFactory;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.RedisStore;

import javax.swing.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static foo.bar.ui.BoardUi.DRAW;

public class BoardUi {
    private static final Logger LOGGER = LoggerFactory.getLogger(BoardUi.class);

    private BoardCanvas boardCanvasRedis;
    private BoardCanvas boardCanvasWS;
    public static final MetricRegistry registry = new MetricRegistry();
    private static Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFeature.class));


    private final RedisStore redisStore = new RedisStore();
    public static final com.codahale.metrics.Timer DRAW = registry.timer("draw");
    public static final com.codahale.metrics.Timer READ = registry.timer("read");
    private BoardCanvasDiff boardCanvasDiff;

    public static void main(String[] args) throws Exception {
        final Slf4jReporter slf4jReporter = Slf4jReporter.forRegistry(registry)
                .outputTo(LoggerFactory.getLogger(Monitoring.class))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        slf4jReporter.start(1, TimeUnit.SECONDS);

        new BoardUi().start();
    }

    public void start() throws Exception {
        JFrame jFrame = new JFrame();

        boardCanvasRedis = new BoardCanvas();
        boardCanvasWS = new BoardCanvas();
        boardCanvasDiff = new BoardCanvasDiff(boardCanvasRedis, boardCanvasWS);
        jFrame.setLayout(new MigLayout(new LC().wrapAfter(3)));
        jFrame.add(new JLabel("Redis"), new CC().growX());
        jFrame.add(new JLabel("Websocket"), new CC().growX());
        jFrame.add(new JLabel("Diff"), new CC().growX());
        jFrame.add(boardCanvasRedis, new CC().grow());
        jFrame.add(boardCanvasWS, new CC().grow());
        jFrame.add(boardCanvasDiff, new CC().grow());
        jFrame.setTitle("Redis vs Websocket Content");
        jFrame.setSize(1000, 1000);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        new Timer(500, e -> {
            LOGGER.info("Updating");
            com.codahale.metrics.Timer.Context time = READ.time();
            byte[] colors = redisStore.getRgbImage();
            time.stop();
            boardCanvasRedis.paintColors(colors);
            boardCanvasDiff.repaint();
        }).start();

        SwingUtilities.invokeLater(() -> {
            Response response = client.target("http://" + Config.getBackendTargetHost() + ":2222/rest/thePlace/rawrgb")
                    .request().buildGet().invoke();
            InputStream inputStream = response.readEntity(InputStream.class);
            try {
                byte[] bytes = ByteStreams.toByteArray(inputStream);
                boardCanvasWS.paintColors(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        WebsocketFactory.getListenerInstance(pixels -> {
            boardCanvasWS.updatePixels(pixels);
        });
    }

}

class BoardCanvas extends Canvas {

    private int xMax = BoardHolder.BOARD_DIMENSIONS.getXMaximum();
    private int yMax = BoardHolder.BOARD_DIMENSIONS.getYMaximum();

    private BufferedImage bufferedImage = new BufferedImage(xMax, yMax, BufferedImage.TYPE_INT_RGB);

    private boolean imagePainted = false;
    private List<Pixel> savedPixels = new ArrayList<>();

    public void paintPixel(Pixel pixel) {
        bufferedImage.setRGB(pixel.getX(), pixel.getY(), pixel.getColor().getAsRgbInt());
    }

    @Override
    public Dimension getPreferredSize() {
        return getParent().getSize();
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(bufferedImage, 0, 0, getWidth(), getHeight(), Color.black, null);
    }

    public void paintColors(byte[] colors) {
        com.codahale.metrics.Timer.Context time = DRAW.time();

        int[] ints = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            ints[i] = colors[i];
        }

        bufferedImage.getRaster().setPixels(0, 0, xMax, yMax, ints);
        if (!imagePainted) {
            imagePainted = true;
            updatePixels(savedPixels);
        }
        repaint();
        time.stop();
    }

    public void updatePixels(List<Pixel> pixels) {
        if (imagePainted) {
            pixels.forEach(this::paintPixel);
        } else {
            savedPixels.addAll(pixels);
        }
        repaint();
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }
}