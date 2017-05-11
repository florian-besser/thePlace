package foo.bar.websocket;

import com.codahale.metrics.Gauge;
import foo.bar.monitoring.Monitoring;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PooledSessionCreator implements WebSocketCreator {

    static {
        Monitoring.registry.register("ws.connections", new Gauge<Integer>() {
            public Integer getValue() {
                return websockets.size();
            }
        });
    }

    private static Set<EventSocket> websockets = Collections.synchronizedSet(new HashSet<>());

    public static Set<EventSocket> getWebsockets() {
        return websockets;
    }

    public static void remove(EventSocket eventSocket) {
        websockets.remove(eventSocket);
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        EventSocket eventSocket = new EventSocket();
        websockets.add(eventSocket);
        return eventSocket;
    }
}
