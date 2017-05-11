package foo.bar.model;

import history.HistoryLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Board {
    private static final Logger LOGGER = LoggerFactory.getLogger(Board.class);

    // [Y][X]
    private SimpleColor[][] colors;

    private Board() {
        // Used by Jackson
    }

    public Board(SimpleColor[][] boardColors) {
        colors = boardColors;
    }

    // Used by Jackson
    public SimpleColor[][] getColors() {
        return colors;
    }

    // Used by Jackson
    public void setColors(SimpleColor[][] colors) {
        this.colors = colors;
    }

    public void setPixel(Pixel toSet) {
        LOGGER.info("Updating Pixel LOCALLY at " + toSet.getX() + " " + toSet.getY() +
                " with Color " + toSet.getColor());
        HistoryLogger.logPixelSet(toSet.getX(), toSet.getY(), toSet.getColor());

        // Change actual Pixel color
        setPixelInternal(toSet);
    }

    // Only to be used from this class and Bots!
    public void setPixelInternal(Pixel toSet) {
        colors[toSet.getY()][toSet.getX()] = toSet.getColor();
    }

}
