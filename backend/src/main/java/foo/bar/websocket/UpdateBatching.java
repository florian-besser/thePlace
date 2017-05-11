package foo.bar.websocket;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import foo.bar.model.Pixel;
import foo.bar.monitoring.Monitoring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class UpdateBatching extends Thread {

    public static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateBatching.class);
    private static final int MAX_UPDATES_PER_SECOND = 5;
    private static RateLimiter throttle = RateLimiter.create(MAX_UPDATES_PER_SECOND);
    private static UpdateBatching instance = null;
    private final Timer updateTime = Monitoring.registry.timer("ws.update.time");
    private final Meter updateCount = Monitoring.registry.meter("ws.update.count");
    private final ConcurrentLinkedDeque<Pixel> updatesToSend = new ConcurrentLinkedDeque<>();

    private UpdateBatching() {
        setDaemon(true);
    }

    public static UpdateBatching getInstance() {
        if (instance == null) {
            startInstance();
        }
        return instance;
    }

    private static synchronized void startInstance() {
        if (instance == null) {
            instance = new UpdateBatching();
            instance.start();
        }
    }

    public void addUpdate(Pixel update) {
        updatesToSend.addLast(update);
    }

    @Override
    public void run() {
        boolean debugEnabled = LOGGER.isDebugEnabled();

        while (true) {
            throttle.acquire();
            if (updatesToSend.isEmpty()) {
                continue;
            }
            try (Timer.Context ignored = updateTime.time()) {
                String toSetStr = prepareUpdate();

                // Update all connected clients
                Set<EventSocket> websockets = PooledSessionCreator.getWebsockets();

                ArrayList<EventSocket> eventSockets = new ArrayList<>(websockets.size());
                eventSockets.addAll(websockets);
                if (debugEnabled) {
                    LOGGER.debug("Sending to all websockets: " + toSetStr);
                }
                List<Future<Void>> futures = websockets.stream()
                        .map(eventSocket -> eventSocket.sendMessageAsync(toSetStr))
                        .collect(Collectors.toList());
                futures.forEach(future -> {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        LOGGER.warn("Could not send message to client", e);
                    }
                });
            }
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
        updateCount.mark(updates.size());
        return serialize(updates);
    }

    private String serialize(Object o) {
        String toSetStr;
        try {
            toSetStr = MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error wile converting Pixel to String.", e);
        }
        return toSetStr;
    }

}
