package foo.bar.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import foo.bar.board.BoardHolder;
import foo.bar.model.Board;
import foo.bar.model.BoardDimensions;
import foo.bar.model.Pixel;
import foo.bar.model.SimpleColor;
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
    @Path("timeout")
    @Produces(MediaType.APPLICATION_JSON)
    public int timeout() {
        return RedisStore.SECONDS;
    }

    @GET
    @Path("place")
    @Produces(MediaType.APPLICATION_JSON)
    public Board place() {
        return BoardHolder.THE_BOARD;
    }

    @PUT
    @Path("place/{x}/{y}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putPixel(@PathParam("x") int x, @PathParam("y") int y,
                             @NotNull PutPixelBody body) {
        if (body.getColor() == null || body.getUser() == null) {
            return Response.
                    status(Response.Status.BAD_REQUEST).
                    entity("Color and User must not be null!").
                    build();
        }
        Board board = BoardHolder.THE_BOARD;
        SimpleColor[][] colors = board.getColors();
        int xMax = colors[0].length;
        int yMax = colors.length;
        if (x < 0 || y < 0 || x >= xMax || y >= yMax) {
            return Response.
                    status(Response.Status.BAD_REQUEST).
                    entity("X or Y out of bounds").
                    build();
        }

        // Check for permission for this user to set another pixel, only allowed every 5 minutes
        if (!redisStore.tryToSetPixel(body.getUser())) {
            return Response.
                    status(Response.Status.BAD_REQUEST).
                    entity("User is not allowed").
                    build();
        }

        LOGGER.info("Updating Pixel IN REDIS at " + x + " " + y +
                " with Color " + body.getColor() + " for user " + body.getUser());
        BoardDimensions dimensions = new BoardDimensions(xMax, yMax);
        SimpleColor color = new SimpleColor(body.getColor());
        redisStore.setPixel(dimensions, x, y, color);

        // Send message
        new MessageSender().sendMessage(serialize(new Pixel(x, y, new SimpleColor(body.getColor()))));
        return Response.ok().build();
    }

    private String serialize(Pixel toSet) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(toSet);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error wile converting Pixel to String.", e);
        }
    }

}