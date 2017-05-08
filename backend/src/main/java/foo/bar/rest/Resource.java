package foo.bar.rest;

import foo.bar.board.Board;
import foo.bar.board.Pixel;
import foo.bar.websocket.EventSocket;
import foo.bar.websocket.PooledSessionCreator;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
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

    @GET
    @Path("place")
    @Produces(MediaType.APPLICATION_JSON)
    public Board place() {
        return Board.DEFAULT;
    }

    @PUT
    @Path("place/{x}/{y}")
    public void putPixel(@PathParam("x") int x, @PathParam("y") int y,
                         @NotNull @QueryParam("color") String color,
                         @NotNull @QueryParam("user") String user) {
        if (color == null || user == null) {
            throw new RuntimeException("Color and User must not be null!");
        }
        Board.DEFAULT.setPixel(new Pixel(x, y, color), user);
    }
}