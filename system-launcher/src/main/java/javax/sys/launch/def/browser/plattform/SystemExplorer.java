package javax.sys.launch.def.browser.plattform;
/**
 * This class serves to ensure and standardize the
 * factory classes and their instance functions.<br>
 * In addition, the logger is provided to the
 * realization of user information management.
 */
public interface SystemExplorer <T extends org.openqa.selenium.WebDriver> extends AutoCloseable {
    /** Logger is provided to the realization of user information management. */
    org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SystemExplorer.class);
    /** Temporary folder fot save downloaded files. */
    String tmpPath = org.apache.commons.io.FileUtils.getTempDirectoryPath();
    /**
     * Creates a list of web driver instances
     * @param count of instances
     * @return a List of web-driver instances
     */
    @org.jetbrains.annotations.NotNull java.util.List<T> createDriverInstances(int count) throws Exception;
    /**
     * Creates a web-driver instance of the default browser of the current OS.
     * @return  a web-driver instance of the system default driver
     */
    @org.jetbrains.annotations.Nullable T createDriverInstance() throws Exception;
}
