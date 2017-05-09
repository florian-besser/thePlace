package foo.bar.client;

import com.google.common.util.concurrent.RateLimiter;
import foo.bar.RandomBot;
import foo.bar.RandomBotConfig;
import foo.bar.rest.PutPixelBody;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public abstract class PixelPutter implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(PixelPutter.class);
    private RandomBotConfig config;
    private final int xMax;
    private final int yMax;
    public ThreadLocalRandom current = ThreadLocalRandom.current();
    private RateLimiter throttle;
    private Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFeature.class));

    public PixelPutter(RandomBotConfig config, int xMax, int yMax) {
        this.config = config;
        this.xMax = xMax;
        this.yMax = yMax;
        throttle = RateLimiter.create(config.getMaxRequestsPerSecondPerRequesterThread());
    }

    @Override
    public void run() {
        // initialize random
        for (int i = 0; i < Thread.currentThread().getId(); i++) {
            current.nextInt();
        }
        // Now start putting pixels
        for (int i = 0; i < config.getMaxRequests() / config.getRequesterThreads(); i++) {
            putPixel(xMax, yMax);
        }
        client.close();
    }

    private void putPixel(int xMax, int yMax) {
        throttle.acquire();
        int x = current.nextInt(0, xMax);
        int y = current.nextInt(0, yMax);
        WebTarget webTarget = client.target("http://" + RandomBot.TARGET_HOST + "/rest/thePlace").path("place")
                .path(Integer.toString(x))
                .path(Integer.toString(y));

        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        PutPixelBody entity = new PutPixelBody();
        UUID idOne = UUID.randomUUID();
        entity.setUser(idOne.toString());
        entity.setColor(getColor(x, y));
        invocationBuilder.put(Entity.json(entity));
        LOGGER.debug("Updated Pixel " + x + " " + y);
    }

    protected abstract String getColor(int x, int y);


    protected int getXMax() {
        return xMax;
    }

    protected int getYMax() {
        return yMax;
    }
}
