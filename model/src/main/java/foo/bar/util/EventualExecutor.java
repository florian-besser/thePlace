package foo.bar.util;

import java.util.function.Consumer;
import java.util.function.Function;

public class EventualExecutor<T, R> {

    public static final int MAX_TRIES = 10;

    public R tryExecute(Function<T, R> func, T argument) {
        int count = 0;
        while (true) {
            try {
                return func.apply(argument);
            } catch (Exception e) {
                if (++count == MAX_TRIES) {
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
                if (++count == MAX_TRIES) {
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
