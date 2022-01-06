package javax.sys.launch.def.utils;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.util.Objects;
import java.util.concurrent.Callable;

public final class ExceptionUtils <V> {

    /**
     * @param callable {@link Callable}
     * @param <V>      callable return
     * @param timeOut  time to break up the call
     * @return null by throws
     */
    public static <V> V unthrow(Callable<V> callable, long timeOut) {
        V v = null;
        final long l = System.currentTimeMillis() + timeOut;
        while (l < System.currentTimeMillis() && Objects.isNull(v)) {
            try { v = defuse(callable);}
            catch (Throwable ignored) {/* None... */ }
        }
        return v;
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
