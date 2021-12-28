package javax.sys.launch.def.browser.plattform;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Class for caching and editing stored Values,
 * which will be further processed later.
 */
public interface ExplorerValueMapper {
    /** queue for the stored values*/
    Multimap<Long, Object> queue = ArrayListMultimap.create();
    /** @return class id */
    long id();
    /**
     * Cleaning method for destroying all created and no longer required variables
     * @param o cleanable object
     * @param r action by clean
     */
    void clean(Object o, Runnable r);
}
