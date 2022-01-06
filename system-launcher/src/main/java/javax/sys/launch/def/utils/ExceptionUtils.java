package javax.sys.launch.def.utils;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.util.Objects;
import java.util.concurrent.Callable;

public final class ExceptionUtils <V> {

    volatile V v = null;

    public static void main(String[] args) {
        WebDriverManager.getInstance();
    }

    /**
     * @param callable {@link Callable}
     * @param <V>      callable return
     * @return null by throws
     */
    public static <V> V unthrow(Callable<V> callable) {
        try {
            return defuse(callable);
        } catch (Throwable ignored) {
            return null;
        }
    }

    /**
     * this is a new one, n/a in public libs Callable just suits as a functional interface
     * in JDK throwing Exception
     *
     * @param callable x
     * @param <V>      x
     * @return callback
     */
    public static <V> V defuse(Callable<V> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            //else create a runtime exception with the error message
            throw new RuntimeException(e);
        }
    }
}
