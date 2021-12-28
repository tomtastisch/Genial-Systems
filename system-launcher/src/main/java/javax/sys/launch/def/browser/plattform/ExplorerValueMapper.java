package javax.sys.launch.def.browser.plattform;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Class for caching and editing stored Values,
 * which will be further processed later.
 */
public interface ExplorerValueMapper extends AutoCloseable {
    /** queue for the stored values*/
    Multimap<Long, Object> queue = ArrayListMultimap.create();
    /** @return class id */
    long id();

    /**
     * The call {@link AutoCloseable#close() <class>.close()} is effectively equivalent to the call:<br>
     * <pre>    {@link ExplorerValueMapper#clean(Object, Runnable) <class>.clean(o, r)}</pre>
     * in automated form with own error handling.
     *
     * @throws Exception for handling errors
     */
    @Override void close() throws Exception;

    /**
     * Cleaning method for destroying all created and no longer required variables
     * @param o cleanable object
     * @param r action by clean
     */
    void clean(Object o, Runnable r);
}
