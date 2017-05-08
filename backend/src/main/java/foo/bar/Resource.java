package foo.bar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Set;

@Path("thePlace")
public class Resource {
    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String helloWorld() {
        return "Hello, world!";
    }

    @GET
    @Path("testMessage")
    @Produces(MediaType.TEXT_PLAIN)
    public String testMessage() {
        Set<EventSocket> websockets = PooledSessionCreator.getWebsockets();
        websockets.parallelStream().forEach(eventSocket -> eventSocket.sendMessage("Get this"));
        return websockets.size() + " message(s) sent";
    }
}