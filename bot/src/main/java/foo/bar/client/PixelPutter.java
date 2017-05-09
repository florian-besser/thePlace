package foo.bar.client;

import com.google.common.util.concurrent.RateLimiter;
import foo.bar.RandomBot;
import foo.bar.rest.PutPixelBody;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PixelPutter implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(PixelPutter.class);
    private final int xMax;
    private final int yMax;
    public ThreadLocalRandom current = ThreadLocalRandom.current();
    private RateLimiter throttle = RateLimiter.create(RandomBot.MAX_REQUESTS_PER_SECOND_PER_THREAD);
    private Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFeature.class));

    public PixelPutter(int xMax, int yMax) {
        this.xMax = xMax;
        this.yMax = yMax;
    }

    @Override
    public void run() {
        // initialize random
        for (int i = 0; i < Thread.currentThread().getId(); i++) {
            current.nextInt();
        }
        // Now start putting pixels
        for (int i = 0; i < RandomBot.MAX_REQUESTS / RandomBot.THREADS; i++) {
            putPixel(xMax, yMax);
        }
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
        entity.setColor("#" + getRandomHexString(6));
        invocationBuilder.put(Entity.json(entity));
        LOGGER.info("Updated Pixel " + x + " " + y);
    }

    private String getRandomHexString(int numchars) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(current.nextInt(0, 16)));
        }

        return sb.toString().substring(0, numchars);
    }
}
