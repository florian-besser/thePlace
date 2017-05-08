package foo.bar.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import foo.bar.board.Board;
import foo.bar.board.BoardDimensions;
import foo.bar.board.Pixel;
import foo.bar.mq.MessageSender;
import foo.bar.websocket.EventSocket;
import foo.bar.websocket.PooledSessionCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.RedisStore;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.util.Set;

@Path("thePlace")
public class Resource {
    private static final Logger LOGGER = LoggerFactory.getLogger(Resource.class);

    private final RedisStore redisStore = new RedisStore();

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
        return Board.THE_BOARD;
    }

    @PUT
    @Path("place/{x}/{y}")
    public Response putPixel(@PathParam("x") int x, @PathParam("y") int y,
                             @NotNull @QueryParam("color") String color,
                             @NotNull @QueryParam("user") String user) {
        if (color == null || user == null) {
            return Response.
                    status(Response.Status.BAD_REQUEST).
                    entity("Color and User must not be null!").
                    build();
        }
        Board board = Board.THE_BOARD;
        if (x < 0 || y < 0 || x >= board.getXMaximum() || y >= board.getYMaximum()) {
            return Response.
                    status(Response.Status.BAD_REQUEST).
                    entity("X or Y out of bounds").
                    build();
        }

        // Check for permission for this user to set another pixel, only allowed every 5 minutes
        if (!redisStore.tryToSetPixel(user)) {
            return Response.
                    status(Response.Status.BAD_REQUEST).
                    entity("User is not allowed").
                    build();
        }

        LOGGER.info("Updating Pixel IN REDIS at " + x + " " + y +
                " with Color " + color + " for user " + user);
        redisStore.setPixel(new BoardDimensions(board.getXMaximum(), board.getYMaximum()), x, y, Color.decode(color));

        // Send message
        new MessageSender().sendMessage(serialize(new Pixel(x, y, color)));
        return Response.ok().build();
    }

    private String serialize(Pixel toSet) {
        ObjectMapper mapper = new ObjectMapper();
        String toSetStr;
        try {
            toSetStr = mapper.writeValueAsString(toSet);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error wile converting Pixel to String.", e);
        }
        return toSetStr;
    }
}