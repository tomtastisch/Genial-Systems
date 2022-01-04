package javax.sys.launch.def.browser.plattform;

import org.openqa.selenium.WebDriver;

/**
 * Class for caching and editing stored Values,
 * which will be further processed later.
 */
public interface ExplorerValueMapper extends AutoCloseable {
    /** queue for the stored values */
    com.google.common.collect.Multimap<Long, WebDriver> queue = com.google.common.collect.ArrayListMultimap.create();
    /** @see AutoCloseable#close() */
    java.lang.ref.Cleaner cleaner = java.lang.ref.Cleaner.create();
    /** @return class id */
    long id();
    /**
     * Cleaning method for destroying all created and no longer required variables
     * @param o cleanable object
     * @param r action by clean
     */
    void clean(Object o, Runnable r);
}
