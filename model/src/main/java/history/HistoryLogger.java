package history;

import foo.bar.model.SimpleColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HistoryLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryLogger.class);

    public static void logPixelSet(Integer x, Integer y, SimpleColor color) {
        LOGGER.debug(String.format("%d/%d;%s", x, y, color.getColor()));
    }
}
