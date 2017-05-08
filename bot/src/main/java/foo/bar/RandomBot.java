package foo.bar;

import foo.bar.board.Board;
import foo.bar.model.SimpleColor;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RandomBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomBot.class);

    public static void main(String[] args) {
        Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFeature.class));
        WebTarget webTarget = client.target("http://localhost:2222/rest/thePlace").path("place");

        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();

        Board board = response.readEntity(Board.class);
        int y = 0;
        for (SimpleColor[] row : board.getColors()) {
            int x = 0;
            for (SimpleColor c : row) {
                LOGGER.info("Initial content at " + y + " " + x + " is " + c);
                x++;
            }
            y++;
        }

    }
}
