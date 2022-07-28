package org.system.factory.launcher.browser.plattform;

import org.jetbrains.annotations.NotNull;

/**
 * Types of creatable driver instances
 */
public enum DriverInstance {

    CHROME("ChromeHTML", "chrome.exe"),
    @Deprecated EDGE("AppXq0fevzme2pys62n3e0fbqa7peapykr8v", "msedge.exe"),
    MSEDGE("MSEdgeHTM", "msedge.exe"),
    FIREFOX("FirefoxURL-308046B0AF4A39CB", ""),
    IEXPLORER ("IE.HTTP","iexplore.exe");

    private final JBrowser browser;

    DriverInstance(String rgx, String prc) {
        this.browser = new JBrowser(rgx, prc);
    }

    public @NotNull JBrowser get() {
        return this.browser;
    }

    /**
     * Template class for System-driver-constant
     */
    record JBrowser(String regex, String processCall) {
        //None content...
    }
}
