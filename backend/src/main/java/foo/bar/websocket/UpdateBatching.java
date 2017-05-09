package foo.bar.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import foo.bar.model.Pixel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

public class UpdateBatching extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateBatching.class);

    private final ConcurrentLinkedDeque<Pixel> updatesToSend = new ConcurrentLinkedDeque<>();

    private static final int MAX_UPDATES_PER_SECOND = 10;

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
        updatesToSend.addFirst(update);
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

            ArrayList<EventSocket> eventSockets = new ArrayList<>(websockets);
            LOGGER.info("Sending to all websockets: " + toSetStr);
            eventSockets.parallelStream().forEach(eventSocket -> eventSocket.sendMessage(toSetStr));
        }
    }

    private String prepareUpdate() {
        ArrayList<Pixel> updates = new ArrayList<>();
        while (!updatesToSend.isEmpty()) {
            updates.add(updatesToSend.removeLast());
        }
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
