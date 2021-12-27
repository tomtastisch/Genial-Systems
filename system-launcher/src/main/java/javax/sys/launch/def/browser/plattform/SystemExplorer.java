package javax.sys.launch.def.browser.plattform;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class serves to ensure and standardize the
 * factory classes and their instance functions.<br>
 * In addition, the logger is provided to the
 * realization of user information management.
 */
public interface SystemExplorer extends AutoCloseable {
    /** Logger is provided to the realization of user information management. */
    Logger LOGGER = LoggerFactory.getLogger(SystemExplorer.class);
    /** Temporary folder fot save downloaded files. */
    String tmpPath = FileUtils.getTempDirectoryPath();
    /**
     * Creates a web-driver instance of the default browser of the current OS.
     * @return a web-driver instance of the system default driver
     */
    @Nullable WebDriver createDriverInstance() throws Exception;
}
