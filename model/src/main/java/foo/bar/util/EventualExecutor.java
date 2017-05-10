package foo.bar.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;

public class EventualExecutor<T, R> {
    public static final int MAX_TRIES = 30;
    private static final Logger LOGGER = LoggerFactory.getLogger(EventualExecutor.class);

    public R tryExecute(Function<T, R> func, T argument) {
        int count = 0;
        while (true) {
            try {
                return func.apply(argument);
            } catch (Exception e) {
                LOGGER.warn("Failed to execute function");
                if (++count == MAX_TRIES) {
                    LOGGER.error("Failed to execute function", e);
                    throw e;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        //Ignore
                    }
                }
            }
        }
    }

    public void tryExecute(Consumer<T> func, T argument) {
        int count = 0;
        while (true) {
            try {
                func.accept(argument);
                return;
            } catch (Exception e) {
                LOGGER.warn("Failed to execute consumer");
                if (++count == MAX_TRIES) {
                    LOGGER.error("Failed to execute consumer", e);
                    throw e;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        //Ignore
                    }
                }
            }
        }
    }

    public void tryExecute(Consumer<T> func) {
        tryExecute(func, null);
    }
}
