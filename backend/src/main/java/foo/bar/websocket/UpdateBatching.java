package foo.bar.websocket;

import com.codahale.metrics.Meter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import foo.bar.model.Pixel;
import foo.bar.monitoring.Monitoring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

public class UpdateBatching extends Thread {

    private final Meter updateWebsockets = Monitoring.registry.meter("ws_sentUpdate");

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateBatching.class);

    private final ConcurrentLinkedDeque<Pixel> updatesToSend = new ConcurrentLinkedDeque<>();

    private static final int MAX_UPDATES_PER_SECOND = 2;

    private static RateLimiter throttle = RateLimiter.create(MAX_UPDATES_PER_SECOND);

    private static UpdateBatching instance = null;

    private UpdateBatching() {
        setDaemon(true);
    }

    public static final UpdateBatching getInstance() {
        if (instance == null) {
            instance = new UpdateBatching();
            instance.start();
        }
        return instance;
    }

    public void addUpdate(Pixel update) {
        updatesToSend.addLast(update);
    }

    @Override
    public void run() {
        while (true) {
            throttle.acquire();
            if (updatesToSend.isEmpty()) {
                continue;
            }
            String toSetStr = prepareUpdate();

            // Update all connected clients
            Set<EventSocket> websockets = PooledSessionCreator.getWebsockets();

            ArrayList<EventSocket> eventSockets = new ArrayList<>();
            eventSockets.addAll(websockets);
            LOGGER.info("Sending to all websockets: " + toSetStr);
            websockets.parallelStream().forEach(eventSocket -> eventSocket.sendMessage(toSetStr));
        }
    }

    private String prepareUpdate() {
        ArrayList<Pixel> updates = new ArrayList<>();
        while (true) {
            Pixel pixel = updatesToSend.pollFirst();
            if (pixel == null) {
                break;
            }
            updates.add(pixel);
        }
        updateWebsockets.mark(updates.size());
        return serialize(updates);
    }

    private String serialize(Object o) {
        ObjectMapper mapper = new ObjectMapper();
        String toSetStr;
        try {
            toSetStr = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error wile converting Pixel to String.", e);
        }
        return toSetStr;
    }

}
