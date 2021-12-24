package javax.sys.launch.def.browser.plattform;

/**
 *
 */
enum Driver {

    CHROME ("ChromeHTML","chrome.exe"),
    @Deprecated EDGE("AppXq0fevzme2pys62n3e0fbqa7peapykr8v","msedge.exe"),
    MSEDGE("MSEdgeHTM","msedge.exe"),
    FIREFOX("FirefoxURL-308046B0AF4A39CB",""),
    IEXPLORER ("IE.HTTP","iexplore.exe");

    private final JBrowser browser;

    Driver(String rgx, String prc) {
        this.browser = new JBrowser(rgx, prc);
    }

    public @org.jetbrains.annotations.NotNull JBrowser get() {
        return this.browser;
    }

    /**
     * Template class for System-driver-constant
     */
    record JBrowser(String regex, String processCall) {
        //None content...
    }
}
