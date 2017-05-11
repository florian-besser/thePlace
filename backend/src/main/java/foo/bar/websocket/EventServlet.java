package foo.bar.websocket;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class EventServlet extends WebSocketServlet
{
    @Override
    public void configure(WebSocketServletFactory factory)
    {
        // set a 1 hour timeout
        factory.getPolicy().setIdleTimeout(1000 * 60 * 60);

        factory.getPolicy().setMaxTextMessageBufferSize(10 * 1024 * 1024);
        factory.getPolicy().setMaxTextMessageSize(10 * 1024 * 1024);
        // set a custom WebSocket creator
        factory.setCreator(new PooledSessionCreator());
    }
}