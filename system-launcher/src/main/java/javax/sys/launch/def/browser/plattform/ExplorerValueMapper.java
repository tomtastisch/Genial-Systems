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
}
